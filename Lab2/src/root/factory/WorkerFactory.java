package root.factory;

import root.exception.CommandParsingException;
import root.exception.FactoryConfigInvalidException;
import root.exception.MyException;
import root.worker.*;

import java.io.InputStreamReader;
import java.io.StreamTokenizer;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TreeMap;

public class WorkerFactory {
    private Map<String, String> factoryConfig;
    public WorkerFactory() throws Exception {
        factoryConfig = new TreeMap<>();
        InputStreamReader factoryConfigReader = new InputStreamReader(WorkerFactory.class.getResourceAsStream("FactoryConfig.txt"));
        StreamTokenizer factoryConfigParser = new StreamTokenizer(factoryConfigReader);
        factoryConfigParser.resetSyntax();
        factoryConfigParser.wordChars(1, 255);
        factoryConfigParser.whitespaceChars(' ', ' ');
        factoryConfigParser.whitespaceChars('\r', '\r');
        factoryConfigParser.whitespaceChars('\n', '\n');
        factoryConfigParser.whitespaceChars('\t', '\t');
        factoryConfigParser.commentChar('#');
        factoryConfigParser.eolIsSignificant(true);
        int TT_EOF = StreamTokenizer.TT_EOF, TT_EOL = StreamTokenizer.TT_EOL, TT_WORD = StreamTokenizer.TT_WORD;
        for(int tokenType = factoryConfigParser.nextToken(); tokenType!=TT_EOF; tokenType = factoryConfigParser.nextToken()) {
            if(tokenType == TT_EOL){
                continue;
            }
            String commandName, workerClassName;
            commandName = factoryConfigParser.sval;
            tokenType = factoryConfigParser.nextToken();
            if(tokenType != TT_WORD) {
                factoryConfigReader.close();
                throw new FactoryConfigInvalidException("Syntax error at FactoryConfig at line " + factoryConfigParser.lineno());
            }
            workerClassName = factoryConfigParser.sval;
            factoryConfig.put(commandName, workerClassName);
            tokenType = factoryConfigParser.nextToken();
            if( !(tokenType == TT_EOL || tokenType == TT_EOF) ) {
                factoryConfigReader.close();
                throw new FactoryConfigInvalidException("Syntax error at FactoryConfig at line " + factoryConfigParser.lineno());
            }
        }
        factoryConfigReader.close();
    }
    public Worker buildNewWorker(String workerConfig) throws MyException {
        StringTokenizer workerConfigParser = new StringTokenizer(workerConfig);
        String commandName = workerConfigParser.nextToken();
        if(!factoryConfig.containsKey(commandName)){
            throw new CommandParsingException("Unknown command: " + commandName);
        }
        ArrayList<String> argsList = new ArrayList<>();
        while(workerConfigParser.hasMoreTokens()) {
            argsList.add(workerConfigParser.nextToken());
        }
        String[] args = argsList.toArray(new String[0]);
        try {
            return (Worker) Class.forName(factoryConfig.get(commandName)).getConstructor(args.getClass()).newInstance(new Object[]{args});
        }
        catch (InvocationTargetException e) {
            throw (CommandParsingException) e.getCause();
        }
        catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException e) {
            throw new FactoryConfigInvalidException("Unable to create worker for command " + commandName);
        }
    }
}
