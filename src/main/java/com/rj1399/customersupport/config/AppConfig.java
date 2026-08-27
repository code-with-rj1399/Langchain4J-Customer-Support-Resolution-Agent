package com.rj1399.customersupport.config;
import org.springframework.context.annotation.*; import java.time.*;
@Configuration public class AppConfig { @Bean public Clock clock(){ return Clock.systemUTC(); } }
