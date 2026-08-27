package com.rj1399.customersupport.api;

import com.rj1399.customersupport.agent.MultiAgentSupervisor;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/multi-agent")
public class MultiAgentController {
    private final MultiAgentSupervisor supervisor;
    public MultiAgentController(MultiAgentSupervisor supervisor) { this.supervisor = supervisor; }

    @PostMapping("/resolve")
    public MultiAgentSupervisor.AgentResult resolve(@Valid @RequestBody ResolveRequest request) {
        return supervisor.resolve(new MultiAgentSupervisor.AgentTask(request.orderNumber(), request.customerMessage()));
    }

    public record ResolveRequest(@NotBlank String orderNumber, @NotBlank String customerMessage) {}
}
