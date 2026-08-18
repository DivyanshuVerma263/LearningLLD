package limiter;

import enums.RateLimitType;
import model.RateLimitConfig;

/**
 * This is an abstract class to initialise the rate limiter with the proper config.
 * Use of an interface is avoided else constructor initialization would not be possible.
 */
public abstract class RateLimiter {
    RateLimitType rateLimitType;
    RateLimitConfig rateLimitConfig;

    public RateLimiter(RateLimitConfig rateLimitConfig, RateLimitType rateLimitType) {
        this.rateLimitConfig = rateLimitConfig;
        this.rateLimitType = rateLimitType;
    }

    public abstract boolean allowRequest(String userId);
}
