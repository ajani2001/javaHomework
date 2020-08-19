package root;

import root.exception.FactoryConfigInvalidException;
import root.exception.MyException;
import root.factory.WorkerFactory;
import root.worker.Worker;

import java.io.*;

public class Main {
    public static void main(String[] args) throws Throwable {
        Reader commandSource;
        switch (args.length){
            case 0:
                commandSource = new InputStreamReader(System.in);
                break;
            case 1:
                try {
                    commandSource = new FileReader(args[0]);
                }
                catch (IOException e){
                    System.out.println(e.getMessage());
                    return;
                }
                break;
            default:
                System.out.println("Invalid input");
                return;
        }
        StreamTokenizer lineReader = new StreamTokenizer(commandSource);
        lineReader.resetSyntax();
        lineReader.wordChars(1, 255);
        lineReader.commentChar('#');
        lineReader.whitespaceChars('\r', '\r');
        lineReader.whitespaceChars('\n', '\n');
        RuntimeContext context = new RuntimeContext();
        WorkerFactory factory;
        try {
            factory = new WorkerFactory();
        }
        catch (FactoryConfigInvalidException | IOException e) {
            System.err.println(e.getMessage());
            commandSource.close();
            return;
        }
        for (int tokenType = lineReader.nextToken(); tokenType != StreamTokenizer.TT_EOF; tokenType = lineReader.nextToken() ){
            try {
                Worker currentWorker = factory.buildNewWorker(lineReader.sval);
                currentWorker.work(context);
            }
            catch (MyException e) {
                System.err.println(e.getMessage());
            }
        }
        commandSource.close();
    }
}
