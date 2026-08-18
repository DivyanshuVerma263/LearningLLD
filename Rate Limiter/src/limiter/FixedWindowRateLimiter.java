package limiter;

import enums.RateLimitType;
import model.RateLimitConfig;
import model.WindowState;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class FixedWindowRateLimiter extends RateLimiter {

    // Map to store WindowState(window, count) for every user
    private final Map<String, WindowState> userStates = new ConcurrentHashMap<>();

    public FixedWindowRateLimiter(RateLimitConfig rateLimitConfig) {
        super(rateLimitConfig, RateLimitType.FIXED_WINDOW);
    }

    @Override
    public boolean allowRequest(String userId) {

        // Using AtomicBoolean because we want to update the value of the variable in the lambda expressions
        AtomicBoolean allowed = new AtomicBoolean(false);

        // calculating the current window by converting current time in milliseconds to seconds
        // & then dividing by windowSize to get the window
        long currRequestWindow = System.currentTimeMillis() / 1000 / rateLimitConfig.getWindowInSeconds();

        // ConcurrentHashMap.compute() atomically updates the request count handling concurrent requests
        userStates.compute(userId, (id, windowState) -> {

            // First request from user i.e. windowState will be null
            if(windowState == null) {
                allowed.set(true);
                return new WindowState(currRequestWindow, 1);
            }

            // Check if request is in new window
            if(currRequestWindow != windowState.getWindow()) {
                allowed.set(true);
                windowState.setWindow(currRequestWindow);
                windowState.setCount(1);
                return windowState;
            }

            // request in same window so check if it is within the range
            if(windowState.getCount() < rateLimitConfig.getMaxRequests()) {
                allowed.set(true);
                windowState.setCount(windowState.getCount() + 1);
            }

            // Return state
            return windowState;
        });

        return allowed.get();
    }
}
