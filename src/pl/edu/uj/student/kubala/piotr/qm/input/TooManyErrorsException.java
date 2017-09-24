// QuickMean - TooManyErrorsException.java
//---------------------------------------------------------------------
// [opis pliku]
//---------------------------------------------------------------------
// Utworzono 21:16 23.09.2017 w IntelliJ IDEA
// (C)PKua, wszystkie prawa zastrzeżone
//---------------------------------------------------------------------

package pl.edu.uj.student.kubala.piotr.qm.input;

import pl.edu.uj.student.kubala.piotr.qm.utils.Range;

public class TooManyErrorsException extends ParseException
{
    public TooManyErrorsException(String text, Range malformedRange) {
        super("Too many errors in: " + text, text, malformedRange);
    }

    public TooManyErrorsException(String message, String text, Range malformedRange) {
        super(message, text, malformedRange);
    }

    public TooManyErrorsException(String message, String text) {
        super(message, text);
    }
}
