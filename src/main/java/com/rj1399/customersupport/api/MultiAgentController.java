package com.rj1399.customersupport.api;

import com.rj1399.customersupport.agent.MultiAgentSupervisor;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/multi-agent")
public class MultiAgentController {

    private final MultiAgentSupervisor supervisor;

    public MultiAgentController(MultiAgentSupervisor supervisor) {
        this.supervisor = supervisor;
    }

    @PostMapping("/resolve")
    public MultiAgentSupervisor.AgentResult resolve(@Valid @RequestBody ResolveRequest request) {
        return supervisor.resolve(request.message());
    }

    /**
     * The public API intentionally accepts only the user's natural-language prompt.
     * The multi-agent workflow extracts the order number internally.
     */
    public record ResolveRequest(@NotBlank String message) {
    }
}
