// QuickMean - NegativeErrorException.java
//---------------------------------------------------------------------
// [opis pliku]
//---------------------------------------------------------------------
// Utworzono 21:25 23.09.2017 w IntelliJ IDEA
// (C)PKua, wszystkie prawa zastrzeżone
//---------------------------------------------------------------------

package pl.edu.uj.student.kubala.piotr.qm.input;

import pl.edu.uj.student.kubala.piotr.qm.utils.Range;

public class NegativeErrorException extends ParseException {
    public NegativeErrorException(String message, String text, Range malformedRange) {
        super(message, text, malformedRange);
    }

    public NegativeErrorException(String text, Range malformedRange) {
        super("Error is negative in: " + text, text, malformedRange);
    }

    public NegativeErrorException(String message, String text) {
        super(message, text);
    }
}
