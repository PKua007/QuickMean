// QuickMean - ParsedMeasure.java
//---------------------------------------------------------------------
// [opis pliku]
//---------------------------------------------------------------------
// Utworzono 14:08 22.09.2017 w IntelliJ IDEA
// (C)PKua, wszystkie prawa zastrzeżone
//---------------------------------------------------------------------

package pl.edu.uj.student.kubala.piotr.qm.input;

import pl.edu.uj.student.kubala.piotr.qm.lab.Measure;
import pl.edu.uj.student.kubala.piotr.qm.utils.Range;

/**
 * Klasa opisująca pomiar sparsowany z tekstu. Obiekty zawierają parsowany tekst, sparsowany pomiar i informację w jakim
 * fragmencie tekstu znajduje się wartość pomiaru.
 */
public class ParsedMeasure
{
    private Measure measure;
    private String text;
    private Range valueRange;

    public ParsedMeasure(Measure measure, String text, Range valueRange) {
        if (!Range.forText(text).contains(valueRange))
            throw new IllegalArgumentException("valueRange " + valueRange + "not in text range");
        this.measure = measure;
        this.text = text;
        this.valueRange = valueRange;
    }

    public ParsedMeasure() {
    }

    public Measure getMeasure() {
        return measure;
    }

    public void setMeasure(Measure measure) {
        this.measure = measure;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Range getValueRange() {
        return valueRange;
    }

    public void setValueRange(Range valueRange) {
        this.valueRange = valueRange;
    }
}
