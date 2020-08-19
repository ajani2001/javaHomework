package root.worker;

import root.RuntimeContext;
import root.exception.CommandExecutingException;
import root.exception.CommandParsingException;

public class StackPoper implements Worker {
    public StackPoper(String[] args) throws CommandParsingException {
        if(args.length != 0) {
            throw new CommandParsingException("POP command must have no arguments");
        }
    }

    public void work(RuntimeContext context) throws CommandExecutingException {
        if(context.stack.size() == 0) {
            throw new CommandExecutingException("Stack is empty");
        }
        context.stack.pop();
    }
}