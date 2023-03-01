package common;

import org.antlr.v4.runtime.Token;

import java.util.Map;
import java.util.TreeMap;

public class ParsingException extends Exception {

    public static TreeMap<Integer, String[]> fileOffset = new TreeMap<>();


    public int line;


    public int charPositionInLine;


    public String text;


    public ParsingException containingDefinition;


    public String fileName;


    public String nodeName;

    public ParsingException(String message, int line, int position, String text) {
        super(message);
        this.nodeName = "";
        this.line = line;
        this.charPositionInLine = position;
        this.text = text;
        int num = 0;
        for (Map.Entry<Integer, String[]> keyValuePair : fileOffset.entrySet()) {
            if (this.line <= keyValuePair.getKey()) {
                this.line -= num;
                this.fileName = keyValuePair.getValue()[0];
                if (keyValuePair.getValue().length > 1) {
                    this.nodeName = keyValuePair.getValue()[1];
                }
                break;
            }
            num = keyValuePair.getKey();
        }
    }

    public ParsingException(String message, Token token) {
        super(message);
        this.nodeName = "";
//        base..ctor(message);
        this.line = token.getLine();
        this.charPositionInLine = token.getCharPositionInLine();
        this.text = token.getText();
        int num = 0;
        for (Map.Entry<Integer, String[]> keyValuePair : ParsingException.fileOffset.entrySet()) {
            if (this.line <= keyValuePair.getKey()) {
                this.line -= num;
                this.fileName = keyValuePair.getValue()[0];
                if (keyValuePair.getValue().length > 1) {
                    this.nodeName = keyValuePair.getValue()[1];
                }
                break;
            }
            num = keyValuePair.getKey();
        }
    }

    public static int countLinesInFile(String s) {
        int num = 1;
        int num2 = 0;
        while ((num2 = s.indexOf('\n', num2)) != -1) {
            num++;
            num2++;
        }
        return num;
    }
}
