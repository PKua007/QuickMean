// QuickMean - DoubleRangeException.java
//---------------------------------------------------------------------
// [opis pliku]
//---------------------------------------------------------------------
// Utworzono 16:55 22.09.2017 w IntelliJ IDEA
// (C)PKua, wszystkie prawa zastrzeżone
//---------------------------------------------------------------------

package pl.edu.uj.student.kubala.piotr.qm.input;

import pl.edu.uj.student.kubala.piotr.qm.utils.Range;

public class DoubleRangeException extends ParseException
{
    public DoubleRangeException(String message, String text, Range malformedRange) {
        super(message, text, malformedRange);
    }

    public DoubleRangeException(String text, Range malformedRange) {
        super("Double range exceeded in: " + text, text, malformedRange);
    }
}
