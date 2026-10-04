package saka1029.csp;

import java.util.function.Consumer;

public interface Solver {
    int solve(Consumer<int[]> callback);
    String[] variables();
}
