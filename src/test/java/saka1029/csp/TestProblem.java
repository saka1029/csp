package saka1029.csp;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
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
        Problem.Variable s = problem.variables.get("S");
        Problem.Variable e = problem.variables.get("E");
        Problem.Variable n = problem.variables.get("N");
        Problem.Variable d = problem.variables.get("D");
        Problem.Variable m = problem.variables.get("M");
        Problem.Variable o = problem.variables.get("O");
        Problem.Variable r = problem.variables.get("R");
        Problem.Variable y = problem.variables.get("Y");
        Problem.Constraint se = problem.constraints.stream().filter(c -> c.predicate.equals("S != E")).findFirst().get();
        Problem.Constraint diff = problem.constraints.stream().filter(
            c -> c.predicate.equals("number(S, E, N, D) + number(M, O, R, E) == number(M, O, N, E, Y)")).findFirst().get();
        assertEquals(Set.of(1, 2, 3, 4, 5, 6, 7, 8, 9), s.values);
        assertEquals(Set.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9), e.values);
        assertEquals(8, s.constraints.size());
        assertTrue(s.constraints.contains(se));
        assertTrue(s.constraints.contains(diff));
        assertEquals(8, e.constraints.size());
        assertTrue(e.constraints.contains(se));
        assertTrue(e.constraints.contains(diff));
        assertEquals(Set.of(s, e), se.variables);
        assertEquals(Set.of(s, e , n, d , m, o, r, y), diff.variables);
    }

    // @Test
    // public void testGenerate() {
    //     Problem problem = new Problem();
    //     problem.className("SendMoreMoney");
    //     problem.variable(1, 9, "S");
    //     problem.variable(0, 9, "E", "N", "D");
    //     problem.variable(1, 9, "M");
    //     problem.variable(0, 9, "O", "R", "Y");
    //     problem.constraint("number(S, E, N, D) + number(M, O, R, E) == number(M, O, N, E, Y)");
    //     problem.allDifferent("S", "E", "N", "D", "M", "O", "R", "Y");
    //     String generated = problem.generate();
    //     System.out.println(generated);
    // }

    @Test
    public void testSolve() throws
            IllegalAccessException, InvocationTargetException,
            NoSuchMethodException, SecurityException,
            ClassNotFoundException, CompileError,
            InstantiationException, IllegalArgumentException {
        Problem problem = new Problem();
        problem.className("SimpleProblem");
        problem.variable(1, 4, "A");
        problem.variable(3, 4, "B", "C");
        problem.constraint("A + B <= C");
        // System.out.println(problem.generate());
        Solver solver = problem.solver();
        List<String> out = new ArrayList<>();
        solver.out(s -> out.add(s));
        solver.solve();
        assertEquals(List.of("A,B,C", "1,3,4"), out);
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
