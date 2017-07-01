// QuickMean - ScientificDisplayFormatter.java
//---------------------------------------------------------------------
// [opis pliku]
//---------------------------------------------------------------------
// Utworzono 22:36 01.07.2017 w IntelliJ IDEA
// (C)PKua, wszystkie prawa zastrzeżone
//---------------------------------------------------------------------

package pl.edu.uj.student.kubala.piotr.qm;

import javax.swing.*;
import java.text.ParseException;

public class ScientificDisplayFormatter extends JFormattedTextField.AbstractFormatter
{
    private static final String EXPONENT = "×10^";

    @Override
    public Object stringToValue(String text) throws ParseException {
        if (Main.DEBUG)
            System.out.println("ScientificDisplayFormatter::stringToValue(" + text + ")");
        if (text == null)
            return null;
        else if (text.isEmpty())
            return 0d;
        try {
            return Double.parseDouble(text.replace(',', '.').replace(EXPONENT, "E"));
        } catch (NumberFormatException e) {
            throw new ParseException("", 0);
        }
    }

    @Override
    public String valueToString(Object value) throws ParseException {
        if (Main.DEBUG)
            System.out.println("ScientificDisplayFormatter::valueToString(" + value + ")");
        if (value == null)
            return null;
        else
            return String.valueOf(value).replace("E", EXPONENT);
    }
}
