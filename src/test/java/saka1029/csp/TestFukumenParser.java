package saka1029.csp;

import org.junit.Test;

public class TestFukumenParser {

    @Test
    public void testParser() {
        String question = "SEND + MORE = MONEY";
        Problem problem = FukumenParser.parse(question);
        System.out.println(problem);
    }

}
