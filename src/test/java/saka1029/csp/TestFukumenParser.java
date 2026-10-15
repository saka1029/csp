package saka1029.csp;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import org.junit.Test;

import saka1029.csp.JavaCompilerInMemory.CompileError;

public class TestFukumenParser {

    @Test
    public void testParser() {
        String question = "SEND + MORE = MONEY";
        FukumenParser.parse(question);
        // System.out.println(problem.generate());
    }

    @Test
    public void testParser2() {
        String question = "SEND + MORE == MONEY";
        FukumenParser.parse(question);
        // System.out.println(problem.generate());
    }

    @Test 
    public void testSolve() throws IllegalAccessException,
            InvocationTargetException, NoSuchMethodException,
            SecurityException, ClassNotFoundException,
            InstantiationException, IllegalArgumentException, CompileError {
        Problem problem = FukumenParser.parse("ABC + BAC = CACA");
        List<List<Integer>> solutions = new ArrayList<>();
        problem.solver().solve(a -> solutions.add(IntStream.of(a)
            .mapToObj(Integer::valueOf)
            .toList()));
        assertEquals(List.of(List.of(2, 9, 1)), solutions);
        // System.out.println(problem.generate());
    }

}
