package test;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import root.worker.*;
import root.exception.*;
import root.RuntimeContext;

import java.lang.reflect.InvocationTargetException;
import java.security.spec.ECField;
import java.util.Stack;

public class CalculatorTest {
    RuntimeContext context;
    final double PI = 3.14;

    @BeforeEach
    public void initContext() {
        context = new RuntimeContext();
        context.stack.push(PI);
    }

    @DisplayName("PUSH can push numeric value to stack")
    @ParameterizedTest(name = "Numeric value = {0}")
    @ValueSource(doubles = {1.0, 2, 3.14})
    public void pushNumeric(double numericValue) {
        try {
            StackPusher worker = new StackPusher(new String[]{Double.toString(numericValue)});
            worker.work(context);
            assertEquals(numericValue, context.stack.pop());
        } catch (MyException e) {
            fail();
        }
    }

    @DisplayName("PUSH can push predefined values to stack")
    @ParameterizedTest(name = "Predefined name: {0}")
    @ValueSource(strings = {"a", "b", "abc"})
    public void pushPredefined(String name) {
        try {
            context.declaredValues.put(name, PI);
            StackPusher worker = new StackPusher(new String[]{name});
            worker.work(context);
            assertEquals(PI, context.stack.pop());
        }
        catch (MyException e) {
            fail();
        }
    }

    @Test
    @DisplayName("PUSH throws an exception if a required value is not defined")
    public void pushException() {
        String name = "PI";
        assertFalse(context.declaredValues.containsKey(name));
        try {
            StackPusher worker = new StackPusher(new String[]{name});
            assertThrows(CommandExecutingException.class, () -> worker.work(context));
        }
        catch (CommandParsingException e) {
            fail();
        }
    }

    @DisplayName("PUSH cannot be created with number of args != 1")
    @ParameterizedTest(name = "Throws an exception with {0} arguments")
    @ValueSource(ints = {0, 2, 3, 4})
    public void pushConstructorException(int argNumber) {
        assertThrows(CommandParsingException.class, () -> new StackPusher(new String[argNumber]));
    }

    @Test
    @DisplayName("POP removes value from the top of stack")
    public void popValue() {
        try {
            assertEquals(1, context.stack.size());
            StackPoper worker = new StackPoper(new String[0]);
            worker.work(context);
            assertEquals(0, context.stack.size());
        }
        catch (MyException e) {
            fail();
        }
    }

    @DisplayName("ValueDefiner must receive 2 arguments")
    @ParameterizedTest(name = "Throws an exception with {0} arguments")
    @ValueSource(ints = {0, 1, 3, 4})
    public void valueDefinerConstructorException(int argNumber) {
        assertThrows(CommandParsingException.class, () -> new ValueDefiner(new String[argNumber]));
    }

    @DisplayName("ValueDefiner's constructor throws an exception if a defined value cannot be converted to numeric")
    @ParameterizedTest(name = "Throws with value = {0}")
    @ValueSource(strings = {"a", "228asd", "012das"})
    public void valueDefinerInvalidArg(String value) {
        assertThrows(CommandParsingException.class, () -> new ValueDefiner(new String[]{"PI", value}));
    }

    @DisplayName("ValueDefiner throws an exception when works if constructed with numeric name")
    @ParameterizedTest(name = "Throws with value = {0}")
    @ValueSource(strings = {"-56", "NaN", "-Infinity"})
    public void valueDefinerExecException(String name) {
        try {
            ValueDefiner worker = new ValueDefiner(new String[]{name, Double.toString(PI)});
            assertThrows(CommandExecutingException.class, () -> worker.work(context) );
        }
        catch (CommandParsingException e) {
            fail();
        }
    }

    @DisplayName("ValueDefiner defines correct defines")
    @ParameterizedTest(name = "Works with {0}")
    @ValueSource(strings = {"abc", "X", "PI"})
    public void definerTest(String name) {
        try {
            ValueDefiner worker = new ValueDefiner(new String[]{name, Double.toString(PI)});
            worker.work(context);
            assertEquals(PI, context.declaredValues.get(name));
        }
        catch (MyException e) {
            fail();
        }
    }

    @DisplayName("Summator calculates the sum")
    @ParameterizedTest(name = "{0}+PI")
    @ValueSource(doubles = {1.0, 2, 3.14})
    public void sums(double value) {
        context.stack.push(PI);
        context.stack.push(value);
        try {
            Summator worker = new Summator(new String[0]);
            worker.work(context);
        }
        catch (MyException e) {
            fail();
        }
        assertEquals(value+PI, context.stack.pop());
    }

    @DisplayName("Subtractor calculates the difference")
    @ParameterizedTest(name = "{0}-PI")
    @ValueSource(doubles = {1.0, 2, 3.14})
    public void diff(double value) {
        context.stack.push(PI);
        context.stack.push(value);
        try {
            Subtractor worker = new Subtractor(new String[0]);
            worker.work(context);
        }
        catch (MyException e) {
            fail();
        }
        assertEquals(value-PI, context.stack.pop());
    }

    @DisplayName("Multiplier calculates the product")
    @ParameterizedTest(name = "{0}*PI")
    @ValueSource(doubles = {1.0, 2, 3.14})
    public void prod(double value) {
        context.stack.push(PI);
        context.stack.push(value);
        try {
            Multiplier worker = new Multiplier(new String[0]);
            worker.work(context);
        }
        catch (MyException e) {
            fail();
        }
        assertEquals(value*PI, context.stack.pop());
    }

    @DisplayName("Divider calculates the quotient")
    @ParameterizedTest(name = "{0}/PI")
    @ValueSource(doubles = {1.0, 2, 3.14})
    public void quotient(double value) {
        context.stack.push(PI);
        context.stack.push(value);
        try {
            Divider worker = new Divider(new String[0]);
            worker.work(context);
        }
        catch (MyException e) {
            fail();
        }
        assertEquals(value/PI, context.stack.pop());
    }

    @DisplayName("SQRT calculate the square root")
    @ParameterizedTest(name = "SQRT({0})")
    @ValueSource(doubles = {1.0, 2, 3.14})
    public void squareRoot(double value) {
        context.stack.push(value);
        try {
            SquareRootFinder worker = new SquareRootFinder(new String[0]);
            worker.work(context);
        }
        catch (MyException e) {
            fail();
        }
        assertEquals(Math.sqrt(value), context.stack.pop());
    }

    @Test
    @DisplayName("Printer doesn't change the stack")
    public void printerTest() {
        try {
            context.stack.push(PI);
            Printer worker = new Printer(new String[0]);
            System.out.println("Printer prints:");
            worker.work(context);
            assertEquals(PI, context.stack.pop());
        }
        catch (MyException e) {
            fail();
        }
    }

    @DisplayName("These workers cannot be created with number of args != 0")
    @ParameterizedTest(name = "{0}'s constructor throws an exception with arguments")
    @ValueSource(classes = {Divider.class, Multiplier.class, Printer.class, SquareRootFinder.class, StackPoper.class, Subtractor.class, Summator.class})
    public void workerConstructorException(Class workerWithNoArgs) {
        for(int i = 1; i < 3; ++i) {
            int argNum = i;
            assertThrows(CommandParsingException.class, () -> {
                try {
                    Worker newWorker = (Worker) workerWithNoArgs.getConstructor(String[].class).newInstance(new Object[]{new String[argNum]});
                } catch (InvocationTargetException e) {
                    throw e.getCause();
                }
            });
        }
    }

    @DisplayName("These workers throw an exception if the stack is empty")
    @ParameterizedTest(name = "{0} throws an exception with empty stack")
    @ValueSource(classes = {Printer.class, SquareRootFinder.class, StackPoper.class})
    public void emptyStackException(Class workerClass) {
        context.stack = new Stack<>();
        try {
            Worker worker = (Worker) workerClass.getConstructor(String[].class).newInstance(new Object[]{new String[0]});
            assertThrows(CommandExecutingException.class, () -> worker.work(context));
        }
        catch (Exception e) {
            fail();
        }
    }

    @DisplayName("Binary operators does nothing but throwing an exception if there are not enough elements in stack")
    @ParameterizedTest(name = "{0}")
    @ValueSource(classes = {Summator.class, Divider.class, Multiplier.class, Subtractor.class})
    public void binaryOpException(Class binWorkerClass) {
        int stackSize = context.stack.size();
        assertTrue(stackSize < 2);
        try {
            Worker binOpWorker = (Worker) binWorkerClass.getConstructor(String[].class).newInstance(new Object[]{new String[0]});
            assertThrows(CommandExecutingException.class, () -> binOpWorker.work(context));
        }
        catch (Exception e) {
            fail();
        }
        assertEquals(stackSize, context.stack.size());
    }
}
