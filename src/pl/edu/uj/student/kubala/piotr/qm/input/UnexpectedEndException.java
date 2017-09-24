// QuickMean - UnexpectedEndException.java
//---------------------------------------------------------------------
// [opis pliku]
//---------------------------------------------------------------------
// Utworzono 22:22 23.09.2017 w IntelliJ IDEA
// (C)PKua, wszystkie prawa zastrzeżone
//---------------------------------------------------------------------

package pl.edu.uj.student.kubala.piotr.qm.input;

import pl.edu.uj.student.kubala.piotr.qm.utils.Range;

public class UnexpectedEndException extends ParseException {
    public UnexpectedEndException(String message, String text, Range malformedRange) {
        super(message, text, malformedRange);
    }

    public UnexpectedEndException(String text, Range malformedRange) {
        super("Unexpected end in: " + text, text, malformedRange);
    }

    public UnexpectedEndException(String message, String text) {
        super(message, text);
    }
}