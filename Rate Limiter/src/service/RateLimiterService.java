package service;
import enums.RateLimitType;
import enums.UserTier;
import factory.RateLimiterFactory;
import limiter.RateLimiter;
import model.RateLimitConfig;
import model.User;

import java.util.HashMap;
import java.util.Map;

/**
 * This class determines the rate limiter for users depending on their tier.
 * It uses RateLimiterFactory to create RateLimiter based on the algo & configuration.
 */
public class RateLimiterService {
   private final Map<UserTier, RateLimiter> rateLimiters =  new HashMap<>();

   public RateLimiterService() {
       rateLimiters.put(
               UserTier.FREE,
               RateLimiterFactory.createRateLimiter(
                       RateLimitType.TOKEN_BUCKET,
                       new RateLimitConfig(20, 60)
               )
       );

       rateLimiters.put(
               UserTier.PREMIUM,
               RateLimiterFactory.createRateLimiter(
                       RateLimitType.FIXED_WINDOW,
                       new RateLimitConfig(60, 60)
               )
       );
   }

   public boolean allowRequest(User user) {
       String userId = user.getUserId();
       UserTier userTier = user.getTier();

       RateLimiter rateLimiter = rateLimiters.get(userTier);
       if (rateLimiter == null) {
           throw new IllegalArgumentException("No rate limiter configured for tier " + userTier);
       }
       return rateLimiter.allowRequest(userId);
   }
}
