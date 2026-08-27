package com.rj1399.customersupport.agent;
import org.springframework.stereotype.Service;
@Service
public class MultiAgentSupervisor {
 private final SpecialistAgents agents;
 public MultiAgentSupervisor(SpecialistAgents agents){this.agents=agents;}
 public AgentResult resolve(AgentTask task){SpecialistAgents.Investigation investigation=agents.investigate(task.orderNumber());SpecialistAgents.Resolution resolution=agents.resolve(task.orderNumber(),task.customerMessage());return new AgentResult(investigation,resolution,agents.communicate(investigation,resolution));}
 public record AgentTask(String orderNumber,String customerMessage){}
 public record AgentResult(SpecialistAgents.Investigation investigation,SpecialistAgents.Resolution resolution,String response){}
}
