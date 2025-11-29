package com.irembo.ratelimiter.interceptor;

import com.irembo.ratelimiter.exception.RateLimitExceededException;
import com.irembo.ratelimiter.service.RateLimitService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class RateLimitInterceptor implements HandlerInterceptor {
	
	private final RateLimitService rateLimitService;
	
	@Value("${client.id.demo-client.api-key}")
	private String clientApiKey;
	
	@Value("${client.id.demo-client.per-minute-limit}")
	private int minuteLimit;
	
	@Value("${client.id.demo-client.per-month-limit}")
	private int monthLimit;
	
	@Value("${rate.limit.system-wide-limit}")
	private long systemWideLimit;
	
	@Value("${rate.limit.system-wide-window-sec}")
	private int systemWindowSec;
	
	public RateLimitInterceptor(RateLimitService rateLimitService) {
		this.rateLimitService = rateLimitService;
	}
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		
		if (HttpMethod.OPTIONS.matches(request.getMethod())) {
			return true;
		}
		
		String clientId = "demo-client";
		String requestApiKey = request.getHeader("x-api-key");
		
		// api key check
		if (requestApiKey == null || !requestApiKey.equals(clientApiKey)) {
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid API Key");
			return false;
		}
		
		// check and limit the number of requests across the entire system
		if (!rateLimitService.allowRequestSystemWide(systemWideLimit, systemWindowSec)) {
			throw new RateLimitExceededException("System-wide capacity exceeded.");
		}
		
		//  define and check the number of requests from a specific client on a per month basis
		if (!rateLimitService.allowRequestPerMonth(clientId, monthLimit)) {
			throw new RateLimitExceededException("Client monthly request limit exceeded.");
		}
		
		// 4. check and define the number of requests within a windows time from a specific client
		if (!rateLimitService.allowRequestPerMinute(clientId, minuteLimit)) {
			throw new RateLimitExceededException("Client per-minute limit exceeded.");
		}
		
		return true;
	}
}