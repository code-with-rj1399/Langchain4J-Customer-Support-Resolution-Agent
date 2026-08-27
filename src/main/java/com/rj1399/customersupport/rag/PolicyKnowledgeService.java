package com.rj1399.customersupport.rag;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.Metadata;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.EmbeddingStore;
import jakarta.annotation.PostConstruct;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PolicyKnowledgeService {
 private final EmbeddingStore<TextSegment> store; private final EmbeddingModel model; private final ResourcePatternResolver resolver;
 public PolicyKnowledgeService(EmbeddingStore<TextSegment> store, EmbeddingModel model, ResourcePatternResolver resolver){this.store=store;this.model=model;this.resolver=resolver;}
 @PostConstruct public void indexPolicies(){try{for(Resource r:resolver.getResources("classpath:/knowledge/*.md")){String source=Objects.requireNonNullElse(r.getFilename(),"unknown");String text=new String(r.getInputStream().readAllBytes(),StandardCharsets.UTF_8);Document d=Document.from(text,Metadata.from("source",source,"knowledgeBase","customer-support-policy","documentType","policy"));List<TextSegment> segments=DocumentSplitters.recursive(1200,200).split(d);for(TextSegment s:segments) store.add(model.embed(s).content(),s);}}catch(IOException e){throw new IllegalStateException("Unable to initialize policy knowledge base",e);}}
 public KnowledgeSearchResult search(String query){long started=System.nanoTime();List<EmbeddingMatch<TextSegment>> matches=store.findRelevant(model.embed(query).content(),4,0.60);long duration=(System.nanoTime()-started)/1_000_000;return new KnowledgeSearchResult(query,matches.stream().map(m->new KnowledgeMatch(m.embedded().metadata().getString("source"),m.score(),m.embedded().text())).toList(),duration);}
 public record KnowledgeSearchResult(String query,List<KnowledgeMatch> matches,long durationMs){public String context(){return matches.stream().map(m->"Source: "+m.source()+"\n"+m.content()).collect(Collectors.joining("\n\n---\n\n"));}}
 public record KnowledgeMatch(String source,double score,String content){}
}
