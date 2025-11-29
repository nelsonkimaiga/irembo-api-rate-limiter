package com.irembo.ratelimiter.service;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class RateLimitService {
	
	private static final DateTimeFormatter MONTH_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM");
	
	private final StringRedisTemplate redisTemplate;
	
	public RateLimitService(StringRedisTemplate redisTemplate) {
		this.redisTemplate = redisTemplate;
	}
	
	private boolean checkAndIncrement(String key, long limit, Duration duration) {
		Long count = redisTemplate.opsForValue().increment(key);
		if (count == null) {
			return false;
		}
		
		if (count == 1) {
			redisTemplate.expire(key, duration);
		}
		return count <= limit;
	}
	
	// window-based client limit
	public boolean allowRequestPerMinute(String clientId, int limit) {
		// Simple Fixed Window: Key is unique per minute
		String minuteKey = String.format("client:%s:minute:%s", clientId,
				LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmm")));
		
		return checkAndIncrement(minuteKey, limit, Duration.ofMinutes(1));
	}
	
	// monthly client limit
	public boolean allowRequestPerMonth(String clientId, int limit) {
		String monthKey = String.format("client:%s:month:%s", clientId, LocalDateTime.now().format(MONTH_FORMATTER));
		
		return checkAndIncrement(monthKey, limit, Duration.ofDays(31));
	}
	
	//system-wide limit
	public boolean allowRequestSystemWide(long limit, int windowSeconds) {
		String globalKey = String.format("system:global:%d", System.currentTimeMillis() / (windowSeconds * 1000L));
		return checkAndIncrement(globalKey, limit, Duration.ofSeconds(windowSeconds));
	}
}