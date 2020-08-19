package root.worker;

import root.RuntimeContext;
import root.exception.CommandExecutingException;
import root.exception.CommandParsingException;

public class ValueDefiner implements Worker {
    String valueName;
    Double value;
    public ValueDefiner(String[] args) throws CommandParsingException {
        if(args.length != 2){
            throw new CommandParsingException("DEFINE command must receive 2 arguments");
        }
        valueName = args[0];
        try {
            value = Double.parseDouble(args[1]);
        }
        catch (NumberFormatException e) {
            throw new CommandParsingException("Value \"" + args[1] + "\" cannot be represented as numeric");
        }
    }
    public void work(RuntimeContext context) throws CommandExecutingException {
        try {
            Double.parseDouble(valueName);
        }
        catch (NumberFormatException e) {
            context.declaredValues.put(valueName, value);
            return;
        }
        throw new CommandExecutingException("Defined name must not start with number: " + valueName);
    }
}
