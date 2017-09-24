// QuickMean - ParseException.java
//---------------------------------------------------------------------
// [opis pliku]
//---------------------------------------------------------------------
// Utworzono 16:52 22.09.2017 w IntelliJ IDEA
// (C)PKua, wszystkie prawa zastrzeżone
//---------------------------------------------------------------------

package pl.edu.uj.student.kubala.piotr.qm.input;

import pl.edu.uj.student.kubala.piotr.qm.utils.Range;

public class ParseException extends Exception
{
    private String text;
    private Range malformedRange;

    public ParseException(String message, String text, Range malformedRange) {
        super(message);
        if (text.isEmpty())
            throw new IllegalArgumentException("text empty");
        Range validRange = Range.forText(text);
        if (!validRange.contains(malformedRange))
            throw new IllegalArgumentException("malformedRange not in valid range");
        this.text = text;
        this.malformedRange = malformedRange;
    }

    public ParseException(String message, String text) {
        this(message, text, Range.forText(text));
    }

    public String getText() {
        return text;
    }

    public Range getMalformedRange() {
        return malformedRange;
    }
}
