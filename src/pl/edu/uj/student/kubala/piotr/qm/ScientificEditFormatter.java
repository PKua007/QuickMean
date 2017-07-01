// QuickMean - ScientificDoubleFormatter.java
//---------------------------------------------------------------------
// [opis pliku]
//---------------------------------------------------------------------
// Utworzono 22:00 01.07.2017 w IntelliJ IDEA
// (C)PKua, wszystkie prawa zastrzeżone
//---------------------------------------------------------------------

package pl.edu.uj.student.kubala.piotr.qm;

import javax.swing.*;
import java.text.ParseException;

public class ScientificEditFormatter extends JFormattedTextField.AbstractFormatter
{
    @Override
    public Object stringToValue(String text) throws ParseException {
        if (Main.DEBUG)
            System.out.println("ScientificEditFormatter::stringToValue(" + text + ")");
        if (text == null)
            return null;
        else if (text.isEmpty())
            return 0d;
        try {
            return Double.parseDouble(text.replace(',', '.'));
        } catch (NumberFormatException e) {
            throw new ParseException("", 0);
        }
    }

    @Override
    public String valueToString(Object value) throws ParseException {
        if (Main.DEBUG)
            System.out.println("ScientificEditFormatter::valueToString(" + value + ")");
        if (value == null)
            return "";
        else
            return String.valueOf(value);
    }
}
