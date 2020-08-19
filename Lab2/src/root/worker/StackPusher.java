package root.worker;

import root.RuntimeContext;
import root.exception.CommandExecutingException;
import root.exception.CommandParsingException;

public class StackPusher implements Worker {
    String arg;
    public StackPusher(String[] args) throws CommandParsingException {
        if(args.length != 1) {
            throw new CommandParsingException("PUSH command must have 1 argument");
        }
        arg = args[0];
    }

    public void work(RuntimeContext context) throws CommandExecutingException {
        try {
            Double valueToPush = Double.parseDouble(arg);
            context.stack.push(valueToPush);
        }
        catch (NumberFormatException e){
            if(context.declaredValues.containsKey(arg)){
                context.stack.push(context.declaredValues.get(arg));
            } else {
                throw new CommandExecutingException("Name \"" + arg + "\" is not defined");
            }
        }
    }
}
