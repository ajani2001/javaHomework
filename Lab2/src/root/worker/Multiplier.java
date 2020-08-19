package root.worker;

import root.RuntimeContext;
import root.exception.CommandExecutingException;
import root.exception.CommandParsingException;

public class Multiplier implements Worker {
    public Multiplier(String[] args) throws CommandParsingException {
        if(args.length != 0){
            throw new CommandParsingException("\"*\" command must have no arguments");
        }
    }
    public void work(RuntimeContext context) throws CommandExecutingException {
        if(context.stack.size() < 2) {
            throw new CommandExecutingException("Not enough elements in stack to multiply");
        }
        context.stack.push(context.stack.pop() * context.stack.pop() );
    }
}
