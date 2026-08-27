package com.rj1399.customersupport.guardrails;
public record GuardrailResult(boolean allowed, String reason) { public static GuardrailResult allow(){return new GuardrailResult(true, "ALLOWED");} public static GuardrailResult block(String reason){return new GuardrailResult(false, reason);} }
