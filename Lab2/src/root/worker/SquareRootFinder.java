package root.worker;

import root.RuntimeContext;
import root.exception.CommandExecutingException;
import root.exception.CommandParsingException;

public class SquareRootFinder implements Worker {
    public SquareRootFinder(String[] args) throws CommandParsingException {
        if(args.length != 0) {
            throw new CommandParsingException("SQRT command must have no arguments");
        }
    }

    public void work(RuntimeContext context) throws CommandExecutingException {
        if(context.stack.size() == 0) {
            throw new CommandExecutingException("Stack is empty");
        }
        context.stack.push(Math.sqrt(context.stack.pop()));
    }
}