package com.rj1399.customersupport.agent;
import java.time.Instant; import java.util.Map;
public record AgentTrace(String executionId, Instant timestamp, TraceEventType type, String component, String operation, long durationMs, Map<String,Object> metadata){ public enum TraceEventType { AGENT_STARTED, AGENT_COMPLETED, AGENT_ERROR, MODEL_REQUEST, MODEL_RESPONSE, TOOL_REQUEST, TOOL_RESPONSE, TOOL_ERROR, RETRY } }
