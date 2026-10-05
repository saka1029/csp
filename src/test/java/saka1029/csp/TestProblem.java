package saka1029.csp;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import org.junit.Test;

import saka1029.csp.JavaCompilerInMemory.CompileError;

public class TestProblem {

    static int number(int... digits) {
        int result = 0;
        for (int d : digits)
            result = result * 10 + d;
        return result;
    }

    @Test
    public void testNumber() {
        assertEquals(1234, number(1,2,3,4));
    }

    @Test
    public void testProblem() {
        Problem problem = new Problem();
        problem.className("SendMoreMoney");
        problem.variable(1, 9, "S");
        problem.variable(0, 9, "E", "N", "D");
        problem.variable(1, 9, "M");
        problem.variable(0, 9, "O", "R", "Y");
        problem.constraint("number(S, E, N, D) + number(M, O, R, E) == number(M, O, N, E, Y)");
        problem.allDifferent("S", "E", "N", "D", "M", "O", "R", "Y");
        System.out.println(problem);
    }

    @Test
    public void testGenerate() {
        Problem problem = new Problem();
        problem.className("SendMoreMoney");
        problem.variable(1, 9, "S");
        problem.variable(0, 9, "E", "N", "D");
        problem.variable(1, 9, "M");
        problem.variable(0, 9, "O", "R", "Y");
        problem.constraint("number(S, E, N, D) + number(M, O, R, E) == number(M, O, N, E, Y)");
        problem.allDifferent("S", "E", "N", "D", "M", "O", "R", "Y");
        String generated = problem.generate();
        System.out.println(generated);
    }

    @Test
    public void testSolve() throws
            IllegalAccessException, InvocationTargetException,
            NoSuchMethodException, SecurityException,
            ClassNotFoundException, CompileError {
        Problem problem = new Problem();
        problem.className("SimpleProblem");
        problem.variable(1, 4, "A");
        problem.variable(3, 7, "B", "C");
        problem.constraint("A + B <= C");
        System.out.println(problem.generate());
        try {
            Solver solver = problem.solver();
            solver.solve();
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | SecurityException
                | ClassNotFoundException | InstantiationException | IllegalArgumentException | CompileError e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testSolver() throws
            IllegalAccessException, InvocationTargetException,
            NoSuchMethodException, SecurityException,
            ClassNotFoundException, CompileError,
            InstantiationException, IllegalArgumentException {
        Problem problem = new Problem();
        problem.className("SimpleProblem");
        problem.variable(1, 4, "A");
        problem.variable(3, 5, "B", "C");
        problem.constraint("A + B <= C");
        Solver solver = problem.solver();
        List<List<Integer>> result = new ArrayList<>();
        solver.solve(a -> result.add(IntStream.of(a).mapToObj(Integer::valueOf).toList()));
        assertArrayEquals(new String[] {"A", "B", "C"}, solver.variables());
        assertEquals(List.of(List.of(1, 3, 4), List.of(1, 3, 5), List.of(1, 4, 5), List.of(2, 3, 5)), result);
    }
}
