package root;

import java.util.Map;
import java.util.Stack;
import java.util.TreeMap;

public class RuntimeContext {
    public Map<String, Double> declaredValues;
    public Stack<Double> stack;
    public RuntimeContext(){
        declaredValues = new TreeMap<>();
        stack = new Stack<>();
    }
}
