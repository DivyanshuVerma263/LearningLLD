package model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Amount of token left in the bucket & last time when the bucket was refilled
 */

@Getter
@Setter
@AllArgsConstructor
public class TokenBucketState {
    private double tokens;
    private long lastRefillTime;
}
