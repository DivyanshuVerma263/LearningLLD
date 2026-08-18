package limiter;

import enums.RateLimitType;
import model.RateLimitConfig;

import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicBoolean;

public class SlidingWindowLogRateLimiter extends RateLimiter {

    // Map to store request timestamps for every user
    private final Map<String, Deque<Long>> userRequestLogs = new ConcurrentHashMap<>();

    public SlidingWindowLogRateLimiter(RateLimitConfig rateLimitConfig) {
        super(rateLimitConfig, RateLimitType.SLIDING_WINDOW_LOG);
    }

    @Override
    public boolean allowRequest(String userId) {

        // Using AtomicBoolean because we want to update the value of the variable in the lambda expressions
        AtomicBoolean allowed = new AtomicBoolean(false);

        long currentTime = System.currentTimeMillis();

        // ConcurrentHashMap.compute() atomically updates the request log handling concurrent requests
        userRequestLogs.compute(userId, (id, requestLog) -> {

            // First request from user i.e. requestLog will be null hence create a new request log & allow request
            if(requestLog == null) {
                requestLog = new ConcurrentLinkedDeque<>();
                requestLog.addLast(currentTime);
                allowed.set(true);
                return requestLog;
            }

            // Calculate the start time of the current sliding window
            long windowStartTime = currentTime - (rateLimitConfig.getWindowInSeconds() * 1000L);

            // Remove all requests which are outside the current sliding window
            while(!requestLog.isEmpty() && requestLog.peekFirst() <= windowStartTime) {
                requestLog.pollFirst();
            }

            // Check if the number of requests in the current sliding window is less than the maximum allowed requests
            if(requestLog.size() < rateLimitConfig.getMaxRequests()) {
                allowed.set(true);
                requestLog.addLast(currentTime);
            }

            // Return the updated request log
            return requestLog;
        });

        return allowed.get();
    }
}