package model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * This stores count of requests for every window.
 */
@Getter
@Setter
@AllArgsConstructor
public class WindowState {
    private long window;
    private long count;
}
