package saka1029.csp;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import saka1029.csp.JavaCompilerInMemory.CompileError;

public class Problem {

    static final Pattern VARIABLE_PATTERN = Pattern.compile(
        "[a-z_\\p{IsHan}\\p{IsHiragana}\\p{IsKatakana}]"
        + "[a-z_\\p{IsHan}\\p{IsHiragana}\\p{IsKatakana}\\d]*",
        Pattern.CASE_INSENSITIVE);

    static class Variable {
        final String name;
        final Set<Integer> values;
        final Set<Constraint> constraints = new HashSet<>();
        Variable(String name, int min, int max) {
            this(name, IntStream.rangeClosed(min, max)
                .mapToObj(Integer::valueOf)
                .collect(Collectors.toSet()));
        }
        Variable(String name, Set<Integer> values) {
            this.name = name;
            this.values = new HashSet<>(values);
        }
    }

    public class Constraint {
        final String predicate;
        final Set<Variable> variables = new HashSet<>();
        Constraint(String predicate) {
            this.predicate = predicate;
        }
    }

    String className;
    final Map<String, Variable> variables = new LinkedHashMap<>();
    final List<Constraint> constraints = new ArrayList<>();
    final List<String> anyCodes = new ArrayList<>();

    public void className(String className) {
        this.className = className;
    }

    public void variable(Set<Integer> values, String... names) {
        if (constraints.size() > 0)
            throw new RuntimeException("define all variables before define constraint");
        for (String name : names)
            this.variables.put(name, new Variable(name, values));
    }

    public void variable(int min, int max, String... names) {
        if (constraints.size() > 0)
            throw new RuntimeException("define all variables before define constraint");
        for (String name : names)
            this.variables.put(name, new Variable(name, min, max));
    }

    public void constraint(String predicate) {
        Constraint constraint = new Constraint(predicate);
        Matcher m = VARIABLE_PATTERN.matcher(predicate);
        while (m.find()) {
            String variableName = m.group();
            Variable variable = variables.get(variableName);
            if (variable != null) {
                constraint.variables.add(variable);
                variable.constraints.add(constraint);
            }
        }
        constraints.add(constraint);
    }

    public void allDifferent(String... names) {
        int size = names.length;
        for (int i = 0; i < size; ++i)
            for (int j = i + 1; j < size; ++j)
                constraint("%s != %s".formatted(names[i], names[j]));
    }

    public void anyCode(String line) {
        anyCodes.add(line);
    }

    public String generate() {
        StringWriter sw = new StringWriter();
        try (PrintWriter w = new PrintWriter(sw)) {
            boolean outImport = false;
            for (String s : anyCodes)
                if (s.trim().startsWith("import ")) {
                    w.printf("%s%n", s);
                    outImport = true;
                }
            if (className == null)
                className = "A%d".formatted(new Random().nextInt(1000000));
            if (outImport)
                w.println();
            w.printf("%n");
            w.printf("public class %s implements saka1029.csp.Solver {%n", className);
            w.printf("%n");
            w.printf("    @Override%n");
            w.printf("    public int solve() {%n");
            w.printf("        System.out.println(%s);%n", 
                variables.keySet().stream().collect(Collectors.joining(",", "\"", "\"")));
            w.printf("        return solve(a -> System.out.println(%n");
            w.printf("            java.util.stream.IntStream.of(a)%n");
            w.printf("                .mapToObj(i -> Integer.toString(i))%n");
            w.printf("                .collect(java.util.stream.Collectors.joining(\",\"))));%n");
            w.printf("    }%n");
            w.printf("%n");
            w.printf("    @Override%n");
            w.printf("    public String[] variables() {%n");
            w.printf("        return new String[] {%s};%n",
                variables.keySet().stream().map(s -> "\"" + s + "\"").collect(Collectors.joining(",")));
            w.printf("    }%n");
            w.printf("%n");
            w.printf("    @Override%n");
            w.printf("    public int solve(java.util.function.Consumer<int[]> callback) {%n");
            w.printf("        int count = 0;%n");
            Set<Constraint> remainConstraints = new HashSet<>(constraints);
            List<Variable> generatedVariables = new ArrayList<>();
            for (Variable v : variables.values()) {
                w.printf("        for (int %s : new int[] {%s})%n",
                                    v.name,
                                    v.values.stream().map(i -> "" + i).collect(Collectors.joining(", ")));
                generatedVariables.add(v);
                List<Constraint> generatedConstraints = remainConstraints.stream()
                    .filter(c -> generatedVariables.containsAll(c.variables)).toList();
                if (!generatedConstraints.isEmpty()) {
                    w.printf("        if (%s)%n",
                        generatedConstraints.stream()
                            .map(c -> c.predicate)
                            .sorted(Comparator.comparing(String::length))
                            .collect(Collectors.joining(" && ")));
                    remainConstraints.removeAll(generatedConstraints);
                }
            }
            if (!remainConstraints.isEmpty())
                throw new RuntimeException("constraints does not generated: "
                    + remainConstraints.stream().map(c -> c.predicate).collect(Collectors.joining(", ")));
//            w.printf("        callback.accept(new int[] {%s});%n",
            w.printf("        {%n");
            w.printf("            ++count;%n");
            w.printf("            callback.accept(new int[] {%s});%n",
                variables.keySet().stream().collect(Collectors.joining(", ")));
            w.printf("        }%n");
            w.printf("        return count;%n");
            w.printf("    }%n");
            w.printf("%n");
            w.printf("    static int number(int... digits) {%n");
            w.printf("        int result = 0;%n");
            w.printf("        for (int d : digits)%n");
            w.printf("            result = result * 10 + d;%n");
            w.printf("        return result;%n");
            w.printf("    }%n");
            w.printf("%n");
            boolean outAnyCode = false;
            for (String s : anyCodes)
                if (!s.trim().startsWith("import ")) {
                    w.printf("%s%n", s);
                    outAnyCode = true;
                }
            if (outAnyCode)
                w.printf("%n");
            w.printf("    public static void main(String[] args) {%n");
            w.printf("        long start = System.currentTimeMillis();%n");
            w.printf("        int count = new %s().solve();%n", className);
            w.printf("        System.err.printf(\"solutions: \" + count + \", elapse: %%d msec.%%n\", System.currentTimeMillis() - start);%n");
            w.printf("    }%n");
            w.printf("}%n");
        }
        return sw.toString();
    }
    
    static final String NL = System.lineSeparator();

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("problem ").append(className).append(NL);
        for (Variable v : variables.values()) {
            sb.append("  variable ")
                .append(v.name)
                .append(v.values.stream()
                    .map(i -> "" + i)
                    .collect(Collectors.joining(",", "[", "]"))).append(NL);
            for (Constraint c : v.constraints)
                sb.append("    ").append(c.predicate).append(NL);
        }
        for (Constraint c : constraints) {
            sb.append("  constraint ")
                .append(c.predicate)
                .append(" : ")
                .append(c.variables.stream().map(v -> v.name).collect(Collectors.joining(" "))).append(NL);
        }
        sb.append("  any codes:").append(NL);
        for (String line : anyCodes)
            sb.append("    ").append(line).append(NL);
        return sb.toString();
    }

    static final List<String> OPTIONS = List.of("-g:none");

    public void solve(boolean displaySource) throws
            IllegalAccessException, InvocationTargetException,
            NoSuchMethodException, SecurityException,
            ClassNotFoundException, CompileError {
        String generatedSource = generate();
        if (displaySource)
            System.out.println(generatedSource);
        JavaCompilerInMemory.compile(className, generatedSource, OPTIONS)
            .getMethod("main", String[].class).invoke(null, new Object[] {new String[0]});
    }

    public Solver solver() throws
            IllegalAccessException, InvocationTargetException,
            NoSuchMethodException, SecurityException,
            ClassNotFoundException, CompileError,
            InstantiationException, IllegalArgumentException {
        String generatedSource = generate();
        return (Solver)JavaCompilerInMemory.compile(className, generatedSource, OPTIONS)
            .getDeclaredConstructor()
            .newInstance();
    }
}
