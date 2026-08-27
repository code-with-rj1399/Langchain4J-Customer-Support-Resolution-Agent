package com.rj1399.customersupport.api;

import com.rj1399.customersupport.agent.AgentTrace;
import com.rj1399.customersupport.agent.AgentTraceStore;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/agent/executions")
public class AgentTraceController {
    private final AgentTraceStore traces;

    public AgentTraceController(AgentTraceStore traces) {
        this.traces = traces;
    }

    @GetMapping("/{executionId}/traces")
    public List<AgentTrace> traces(@PathVariable String executionId) {
        return traces.get(executionId);
    }

    @GetMapping(value = "/{executionId}/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter stream(@PathVariable String executionId) {
        SseEmitter emitter = new SseEmitter(60_000L);
        var scheduler = Executors.newSingleThreadScheduledExecutor();
        final int[] sent = {0};
        scheduler.scheduleAtFixedRate(() -> {
            try {
                List<AgentTrace> events = traces.get(executionId);
                while (sent[0] < events.size()) {
                    emitter.send(SseEmitter.event().name("trace").data(events.get(sent[0]++)));
                }
                if (!events.isEmpty() && events.get(events.size() - 1).type().name().startsWith("AGENT_")) {
                    String last = events.get(events.size() - 1).type().name();
                    if (last.equals("AGENT_COMPLETED") || last.equals("AGENT_ERROR")) {
                        emitter.complete();
                        scheduler.shutdown();
                    }
                }
            } catch (IOException ex) {
                emitter.completeWithError(ex);
                scheduler.shutdown();
            }
        }, 0, 250, TimeUnit.MILLISECONDS);
        emitter.onCompletion(scheduler::shutdown);
        emitter.onTimeout(() -> { emitter.complete(); scheduler.shutdown(); });
        return emitter;
    }
}
