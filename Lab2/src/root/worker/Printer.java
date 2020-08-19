package root.worker;

import root.RuntimeContext;
import root.exception.CommandExecutingException;
import root.exception.CommandParsingException;

public class Printer implements Worker {
    public Printer(String[] args) throws CommandParsingException {
        if(args.length != 0) {
            throw new CommandParsingException("PRINT command must have no arguments");
        }
    }
    public void work(RuntimeContext context) throws CommandExecutingException {
        if(context.stack.size() == 0) {
            throw new CommandExecutingException("Stack is empty");
        }
        System.out.println(context.stack.peek());
    }
}