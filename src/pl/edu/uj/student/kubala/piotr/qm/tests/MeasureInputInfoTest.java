
// QuickMean - MeasureInputInfoTest.java
//---------------------------------------------------------------------
// [opis pliku]
//---------------------------------------------------------------------
// Utworzono 19:31 24.09.2017 w IntelliJ IDEA
// (C)PKua, wszystkie prawa zastrzeżone
//---------------------------------------------------------------------

package pl.edu.uj.student.kubala.piotr.qm.tests;

import org.junit.jupiter.api.Test;
import pl.edu.uj.student.kubala.piotr.qm.input.MeasureInputInfo;
import pl.edu.uj.student.kubala.piotr.qm.input.ParsedMeasure;
import pl.edu.uj.student.kubala.piotr.qm.lab.Measure;
import pl.edu.uj.student.kubala.piotr.qm.utils.Range;

import static org.junit.jupiter.api.Assertions.*;

class MeasureInputInfoTest
{
    private final Measure measure = new Measure(5.4, 1.3, 0, 0);
    private final ParsedMeasure parsedMeasure = new ParsedMeasure(measure, "5.4±1.3", new Range(0, 2));

    @Test
    void createCorrectFromMeasure()
    {
        MeasureInputInfo measureInputInfo
                = MeasureInputInfo.createCorrect(measure, new Range(0, 6), new Range(0, 2));
        assertEquals(new Range(0, 6), measureInputInfo.getTextRange());
        assertEquals(new Range(0, 2), measureInputInfo.getValueRange());
        assertEquals(null, measureInputInfo.getErrorRange());
        assertEquals(measureInputInfo.getMeasure(), measure);
        assertEquals(true, measureInputInfo.isCorrect());
    }

    @Test
    void nullMeasureShouldThrowInCreateCorrectFromMeasure()
    {
        Range range = new Range(0);
        assertThrows(NullPointerException.class, () -> MeasureInputInfo.createCorrect(null, range, range));
    }

    @Test
    void nullRangeShouldThrowInCreateCorrectFromMeasure()
    {
        Range range = new Range(0);
        assertThrows(NullPointerException.class, () -> MeasureInputInfo.createCorrect(measure, null, range));
        assertThrows(NullPointerException.class, () -> MeasureInputInfo.createCorrect(measure, range, null));
    }

    @Test
    void negativeRangeShouldThrowInCreateCorrectFromMeasure()
    {
        assertThrows(StringIndexOutOfBoundsException.class,
                () -> MeasureInputInfo.createCorrect(measure, new Range(-1, 1), new Range(0, 1)));
    }

    @Test
    void valueRangeNotInsideTextRangeShouldThrow()
    {
        assertThrows(IllegalArgumentException.class,
                () -> MeasureInputInfo.createCorrect(measure, new Range(15, 18), new Range(14, 16)));
    }

    @Test
    void createCorrectFromFromParsedMeasure()
    {
        MeasureInputInfo measureInputInfo = MeasureInputInfo.createCorrect(parsedMeasure, 34);
        assertEquals(measure, measureInputInfo.getMeasure());
        assertEquals(new Range(34, 40), measureInputInfo.getTextRange());
        assertEquals(new Range(34, 36), measureInputInfo.getValueRange());
        assertEquals(null, measureInputInfo.getErrorRange());
        assertEquals(true, measureInputInfo.isCorrect());
    }

    @Test
    void nullParsedMeasureShouldThrowInCreateCorrectFromParsedMeasure()
    {
        assertThrows(NullPointerException.class, () -> MeasureInputInfo.createCorrect(null, 2));
    }

    @Test
    void negativeTextIdxShouldThrowInCreateCorrectFromParsedMeasure()
    {
        assertThrows(StringIndexOutOfBoundsException.class, () -> MeasureInputInfo.createCorrect(parsedMeasure, -2));
    }

    @Test
    void testCreateIncorrectAbsolutePosition()
    {
        MeasureInputInfo measureInputInfo = MeasureInputInfo.createIncorrect(new Range(4, 10), new Range(5, 6));
        assertEquals(null, measureInputInfo.getMeasure());
        assertEquals(new Range(4, 10), measureInputInfo.getTextRange());
        assertEquals(null, measureInputInfo.getValueRange());
        assertEquals(new Range(5, 6), measureInputInfo.getErrorRange());
        assertEquals(false, measureInputInfo.isCorrect());
    }

    @Test
    void nullRangeShouldThrowInCreateIncorrectAbsolutePosision()
    {
        Range range = new Range(3, 5);
        assertThrows(NullPointerException.class, () -> MeasureInputInfo.createIncorrect(null, range));
        assertThrows(NullPointerException.class, () -> MeasureInputInfo.createIncorrect(range, null));
    }

    @Test
    void negativeRangeShouldThrowInCreateIncorrectAbsolutePosition()
    {
        assertThrows(StringIndexOutOfBoundsException.class,
                () -> MeasureInputInfo.createIncorrect(new Range(-2, 7), new Range(0, 5)));
    }

    @Test
    void errorRangeNotInTextRangeShouldThrowInCreateIncorrectAbsolutePosition()
    {
        assertThrows(IllegalArgumentException.class,
                () -> MeasureInputInfo.createIncorrect(new Range(23, 29), new Range(27, 30)));
    }

    @Test
    void createIncorrectRelativePosition1()
    {
        MeasureInputInfo measureInputInfo = MeasureInputInfo.createIncorrect(4, 16, new Range(10, 15));
        assertEquals(null, measureInputInfo.getMeasure());
        assertEquals(new Range(4, 19), measureInputInfo.getTextRange());
        assertEquals(null, measureInputInfo.getValueRange());
        assertEquals(new Range(14, 19), measureInputInfo.getErrorRange());
        assertEquals(false, measureInputInfo.isCorrect());
    }

    @Test
    void createIncorrectRelativePosition2()
    {
        MeasureInputInfo measureInputInfo = MeasureInputInfo.createIncorrect(10, 7, new Range(0, 4));
        assertEquals(null, measureInputInfo.getMeasure());
        assertEquals(new Range(10, 16), measureInputInfo.getTextRange());
        assertEquals(null, measureInputInfo.getValueRange());
        assertEquals(new Range(10, 14), measureInputInfo.getErrorRange());
        assertEquals(false, measureInputInfo.isCorrect());
    }

    @Test
    void negativeTextIdxShouldThrowInCreateIncorrectRelativePosition()
    {
        assertThrows(StringIndexOutOfBoundsException.class,
                () -> MeasureInputInfo.createIncorrect(-2, 5, new Range(1, 3)));
    }

    @Test
    void zeroOrNegativeTextLengthShouldThrowInCreateIncorrectRelativePosition()
    {
        assertThrows(IllegalArgumentException.class,
                () -> MeasureInputInfo.createIncorrect(5, 0, new Range(3, 4)));
        assertThrows(IllegalArgumentException.class,
                () -> MeasureInputInfo.createIncorrect(5, -2, new Range(3, 4)));
    }

    @Test
    void badRelativeRangeShouldThrowInCreateIncorrectRelativePosition()
    {
        assertThrows(IllegalArgumentException.class,
                () -> MeasureInputInfo.createIncorrect(34, 12, new Range(-1, 10)));
        assertThrows(IllegalArgumentException.class,
                () -> MeasureInputInfo.createIncorrect(34, 12, new Range(4, 12)));
    }
}