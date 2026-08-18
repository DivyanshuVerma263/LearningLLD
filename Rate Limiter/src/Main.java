import enums.UserTier;
import model.User;
import service.RateLimiterService;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) throws InterruptedException {
        RateLimiterService rateLimiterService = new RateLimiterService();

        User freeUser = new User("user1", UserTier.FREE);
        User premiumUser = new User("user2", UserTier.PREMIUM);

        System.out.println("=== Free User Requests ===");
        for (int i = 1; i <= 25; i++) {
            boolean allowed = rateLimiterService.allowRequest(freeUser);
            System.out.println("Request " + i + " for Free User: " + (allowed ? "ALLOWED" : "BLOCKED"));
            Thread.sleep(100);
        }

        System.out.println("\n=== Premium User Requests ===");
        for (int i = 1; i <= 120; i++) {
            boolean allowed = rateLimiterService.allowRequest(premiumUser);
            System.out.println("Request " + i + " for Premium User: " + (allowed ? "ALLOWED" : "BLOCKED"));
            Thread.sleep(100);
        }
    }
}