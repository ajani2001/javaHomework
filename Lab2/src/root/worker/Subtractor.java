package root.worker;

import root.RuntimeContext;
import root.exception.CommandExecutingException;
import root.exception.CommandParsingException;

public class Subtractor implements Worker {
    public Subtractor(String[] args) throws CommandParsingException {
        if(args.length != 0){
            throw new CommandParsingException("\"-\" command must have no arguments");
        }
    }
    public void work(RuntimeContext context) throws CommandExecutingException {
        if(context.stack.size() < 2) {
            throw new CommandExecutingException("Not enough elements in stack to make subtraction");
        }
        Double a = context.stack.pop();
        Double b = context.stack.pop();
        context.stack.push(a - b);
    }
}
