package factory;

import enums.RateLimitType;
import limiter.FixedWindowRateLimiter;
import limiter.RateLimiter;
import limiter.SlidingWindowLogRateLimiter;
import limiter.TokenBucketRateLimiter;
import model.RateLimitConfig;

/**
 * This factory creates the respective RateLimiter based on the algorithm & config defined in RateLimitService
 */
public class RateLimiterFactory {
    public static RateLimiter createRateLimiter(RateLimitType algo, RateLimitConfig rateLimitConfig) {
        return switch (algo) {
            case TOKEN_BUCKET -> new TokenBucketRateLimiter(rateLimitConfig);
            case FIXED_WINDOW -> new FixedWindowRateLimiter(rateLimitConfig);
            case SLIDING_WINDOW_LOG -> new SlidingWindowLogRateLimiter(rateLimitConfig);
            default -> throw new IllegalArgumentException("Invalid algo type");
        };
    }
}
