import java.io.*;
import java.util.TreeMap;

public class Main {
    public static void encode(TreeMap<Character, String> encodeTable, Reader in, Writer out) throws IOException {
        StreamTokenizer parser = new StreamTokenizer(in);
        parser.resetSyntax();
        parser.eolIsSignificant(true);
        parser.lowerCaseMode(true);
        for(int nextToken = parser.nextToken(); nextToken != StreamTokenizer.TT_EOF; nextToken = parser.nextToken()) {
            if ((char)nextToken == ' ') {
                out.write("  ");
                continue;
            }
            if (nextToken == StreamTokenizer.TT_EOL) {
                out.write("\n");
                continue;
            }
            if (encodeTable.containsKey((char)nextToken)) {
                out.write(encodeTable.get((char) nextToken) + ' ');
            } else {
                System.err.println("Unknown symbol: " + (char)nextToken);
            }
        }
    }
    public static void decode(TreeMap<String, Character> decodeTable, Reader in, Writer out) throws IOException {
        StreamTokenizer parser = new StreamTokenizer(in);
        parser.resetSyntax();
        parser.wordChars('.', '.');
        parser.wordChars('-','-');
        parser.eolIsSignificant(true);
        for(parser.nextToken(); parser.ttype != StreamTokenizer.TT_EOF; parser.nextToken()) {
            if(parser.ttype == StreamTokenizer.TT_WORD) {
                if(decodeTable.containsKey(parser.sval)) {
                    out.write(decodeTable.get(parser.sval));
                } else {
                    System.err.println("Unknown sequence: " + parser.sval);
                }
            } else if(parser.ttype == ' ') {
                out.write(' ');
            } else if(parser.ttype == StreamTokenizer.TT_EOL) {
                out.write('\n');
                continue;
            }
            parser.nextToken();
            if(parser.ttype != ' ') {
                System.err.println("Unexpected non-whitespace symbol");
            }
        }
    }
    public static void main(String[] args) {
        if( args.length!=3 || !(args[2].equals("e") || args[2].equals("d")) || args[0].equals(args[1]) ) {
            System.out.println("Incorrect arguments");
            System.out.println("Arguments should be: { inputFile, outputFile, d|e }");
            return;
        }
        TreeMap<String, Character> M2E = new TreeMap<>();
        TreeMap<Character, String> E2M = new TreeMap<>();
        E2M.put('a',      ".-"); M2E.put(".-",      'a');
        E2M.put('b',    "-..."); M2E.put("-...",    'b');
        E2M.put('c',    "-.-."); M2E.put("-.-.",    'c');
        E2M.put('d',     "-.."); M2E.put("-..",     'd');
        E2M.put('e',       "."); M2E.put(".",       'e');
        E2M.put('f',    "..-."); M2E.put("..-.",    'f');
        E2M.put('g',     "--."); M2E.put("--.",     'g');
        E2M.put('h',    "...."); M2E.put("....",    'h');
        E2M.put('i',      ".."); M2E.put("..",      'i');
        E2M.put('j',    ".---"); M2E.put(".---",    'j');
        E2M.put('k',     "-.-"); M2E.put("-.-",     'k');
        E2M.put('l',    ".-.."); M2E.put(".-..",    'l');
        E2M.put('m',      "--"); M2E.put("--",      'm');
        E2M.put('n',      "-."); M2E.put("-.",      'n');
        E2M.put('o',     "---"); M2E.put("---",     'o');
        E2M.put('p',    ".--."); M2E.put(".--.",    'p');
        E2M.put('q',    "--.-"); M2E.put("--.-",    'q');
        E2M.put('r',     ".-."); M2E.put(".-.",     'r');
        E2M.put('s',     "..."); M2E.put("...",     's');
        E2M.put('t',       "-"); M2E.put("-",       't');
        E2M.put('u',     "..-"); M2E.put("..-",     'u');
        E2M.put('v',    "...-"); M2E.put("...-",    'v');
        E2M.put('w',     ".--"); M2E.put(".--",     'w');
        E2M.put('x',    "-..-"); M2E.put("-..-",    'x');
        E2M.put('y',    "-.--"); M2E.put("-.--",    'y');
        E2M.put('z',    "--.."); M2E.put("--..",    'z');
        E2M.put('1',   ".----"); M2E.put(".----",   '1');
        E2M.put('2',   "..---"); M2E.put("..---",   '2');
        E2M.put('3',   "...--"); M2E.put("...--",   '3');
        E2M.put('4',   "....-"); M2E.put("....-",   '4');
        E2M.put('5',   "....."); M2E.put(".....",   '5');
        E2M.put('6',   "-...."); M2E.put("-....",   '6');
        E2M.put('7',   "--..."); M2E.put("--...",   '7');
        E2M.put('8',   "---.."); M2E.put("---..",   '8');
        E2M.put('9',   "----."); M2E.put("----.",   '9');
        E2M.put('0',   "-----"); M2E.put("-----",   '0');
        E2M.put('.',  ".-.-.-"); M2E.put(".-.-.-",  '.');
        E2M.put(',',  "--..--"); M2E.put("--..--",  ',');
        E2M.put('?',  "..--.."); M2E.put("..--..",  '?');
        E2M.put('\'', ".----."); M2E.put(".----.", '\'');
        E2M.put('!',  "-.-.--"); M2E.put("-.-.--",  '!');
        E2M.put('/',   "-----"); M2E.put("-----",   '/');
        E2M.put('&',   ".-..."); M2E.put(".-...",   '&');
        E2M.put(':',  "---..."); M2E.put("---...",  ':');
        E2M.put(';',  "-.-.-."); M2E.put("-.-.-.",  ';');
        E2M.put('=',   "-...-"); M2E.put("-...-",   '=');
        E2M.put('+',   ".-.-."); M2E.put(".-.-.",   '+');
        E2M.put('-',  "-....-"); M2E.put("-....-",  '-');
        E2M.put('_',  "..--.-"); M2E.put("..--.-",  '_');
        E2M.put('"',  ".-..-."); M2E.put(".-..-.",  '"');
        E2M.put('$', "...-..-"); M2E.put("...-..-", '$');
        E2M.put('@',  ".--.-."); M2E.put(".--.-.",  '@');
        try( FileReader in = new FileReader(args[0]);
             FileWriter out = new FileWriter(args[1]); ) {
            if (args[2].equals("e")) {
                encode(E2M, in, out);
            } else {
                decode(M2E, in, out);
            }
        }
        catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}