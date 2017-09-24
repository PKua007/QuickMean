// QuickMean - MalformedException.java
//---------------------------------------------------------------------
// [opis pliku]
//---------------------------------------------------------------------
// Utworzono 21:10 23.09.2017 w IntelliJ IDEA
// (C)PKua, wszystkie prawa zastrzeżone
//---------------------------------------------------------------------

package pl.edu.uj.student.kubala.piotr.qm.input;

import pl.edu.uj.student.kubala.piotr.qm.utils.Range;

public class MalformedDoubleException extends ParseException
{
    public MalformedDoubleException(String message, String text, Range malformedRange) {
        super(message, text, malformedRange);
    }

    public MalformedDoubleException(String message, String text) {
        super(message, text);
    }

    public MalformedDoubleException(String text, Range malformedRange) {
        super("Malformed double in: " + text, text, malformedRange);
    }
}
