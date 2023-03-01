package common;

import java.util.Comparator;

public class ParsingExceptionComparer implements Comparator<ParsingException> {
    @Override
    public int compare(ParsingException o1, ParsingException o2) {
        if (o1.line != o2.line) {
            return Integer.compare(o1.line, o2.line);
        }
        return Integer.compare(o1.charPositionInLine, o2.charPositionInLine);
    }
}
