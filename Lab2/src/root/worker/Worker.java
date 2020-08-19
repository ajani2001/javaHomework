package root.worker;

import root.RuntimeContext;
import root.exception.CommandExecutingException;

public interface Worker {
    public abstract void work(RuntimeContext context) throws CommandExecutingException;
}