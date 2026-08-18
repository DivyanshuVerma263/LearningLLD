package limiter;

import enums.RateLimitType;
import model.RateLimitConfig;
import model.TokenBucketState;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class TokenBucketRateLimiter extends RateLimiter {

    // Map to store TokenBucketState(token, lastRefillTime) for every user
    private final Map<String, TokenBucketState> userTokenBuckets = new ConcurrentHashMap<>();

    public TokenBucketRateLimiter(RateLimitConfig rateLimitConfig) {
        super(rateLimitConfig, RateLimitType.TOKEN_BUCKET);
    }

    @Override
    public boolean allowRequest(String userId) {

        // Using AtomicBoolean because we want to update the value of the variable in the lambda expressions
        AtomicBoolean allowed = new AtomicBoolean(false);

        long currentTime =  System.currentTimeMillis();

        // ConcurrentHashMap.compute() atomically updates the request count handling concurrent requests
        userTokenBuckets.compute(userId, (id, tokenBucket) -> {

            // First request from user i.e. windowState will be null hence consume 1 token & return
            if(tokenBucket == null) {
                allowed.set(true);
                return new TokenBucketState(rateLimitConfig.getMaxRequests() - 1,  currentTime);
            }

            // Calculate elapsed time from last refill time
            long elapsedTime = currentTime - tokenBucket.getLastRefillTime();

            // Calculate refill time for each token to be added
            double refillRate = (double) rateLimitConfig.getMaxRequests() / rateLimitConfig.getWindowInSeconds();

            // Calculate the number of tokens to be added from last refill time
            double tokensToAdd = ((double) elapsedTime / 1000) * refillRate;

            // Limit the maximum amount of added tokens till the bucket capacity
            double newTokenCount = Math.min(tokenBucket.getTokens() + tokensToAdd, rateLimitConfig.getMaxRequests());

            // Set the new token bucket state
            tokenBucket.setTokens(newTokenCount);
            tokenBucket.setLastRefillTime(currentTime);

            // Check if there are tokens to process the request & reduce the token by 1
            if(tokenBucket.getTokens() >= 1)  {
                allowed.set(true);
                tokenBucket.setTokens(tokenBucket.getTokens() - 1);
            }

            // Return the updated bucket
            return tokenBucket;
        });

        return allowed.get();
    }
}
