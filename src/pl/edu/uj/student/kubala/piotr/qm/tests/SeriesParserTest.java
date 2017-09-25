// QuickMean - SeriesParserTest.java
//---------------------------------------------------------------------
// [opis pliku]
//---------------------------------------------------------------------
// Utworzono 19:30 25.09.2017 w IntelliJ IDEA
// (C)PKua, wszystkie prawa zastrzeżone
//---------------------------------------------------------------------

package pl.edu.uj.student.kubala.piotr.qm.tests;

import org.junit.jupiter.api.Test;
import pl.edu.uj.student.kubala.piotr.qm.input.MeasureInputInfo;
import pl.edu.uj.student.kubala.piotr.qm.input.SeriesInputInfo;
import pl.edu.uj.student.kubala.piotr.qm.input.SeriesParser;
import pl.edu.uj.student.kubala.piotr.qm.lab.Measure;
import pl.edu.uj.student.kubala.piotr.qm.utils.Range;

import static org.junit.jupiter.api.Assertions.*;


class SeriesParserTest
{
    private final SeriesParser parser = new SeriesParser();
    private SeriesInputInfo seriesInputInfo;

    private void parse(String text) {
        seriesInputInfo = parser.parseSeries(text);
    }

    private void parse(String text, int selectionStart, int selectionLength)
    {
        seriesInputInfo = parser.parseSeries(text, selectionStart, selectionLength);
    }

    private static void assertMeasure(Measure measure, double value, double calibrationError, double humanError) {
        assertMeasure(measure, value, calibrationError, humanError, 0);
    }

    private static void assertMeasure(Measure measure, double value, double humanError) {
        assertMeasure(measure, value, humanError, 0, 0);
    }

    private static void assertMeasure(Measure measure, double value) {
        assertMeasure(measure, value, 0, 0, 0);
    }

    private static void assertMeasure(Measure measure, double value, double calibrationError, double humanError, double standardError) {
        assertEquals(value, measure.getValue());
        assertEquals(calibrationError, measure.getCalibrationError());
        assertEquals(humanError, measure.getHumanError());
        assertEquals(standardError, measure.getStandardError());
    }

    @Test
    void nullTextShouldThrow()
    {
        assertThrows(NullPointerException.class, () -> parse(null));
    }

    @Test
    void emptyTextShouldReturnEmptyInfo()
    {
        parse("");
        assertEquals(true, seriesInputInfo.empty());
    }

    @Test
    void semicolonsAndSpacesShouldReturnEmptyInfo()
    {
        parse(" ");
        assertEquals(true, seriesInputInfo.empty());
        parse(";");
        assertEquals(true, seriesInputInfo.empty());
        parse("; ");
        assertEquals(true, seriesInputInfo.empty());
    }

    @Test
    void singleMeasure()
    {
        parse("5.4±0.3");
        assertEquals(1, seriesInputInfo.getNumberOfInfos());
        MeasureInputInfo measureInputInfo = seriesInputInfo.getMeasureInfo(0);
        Measure measure = measureInputInfo.getMeasure();
        assertMeasure(measure, 5.4, 0.3);
        assertEquals(new Range(0, 6), measureInputInfo.getTextRange());
    }

    @Test
    void singleMeasureWithSemicolonAndSpace()
    {
        parse("5.4±0.3; ");
        assertEquals(1, seriesInputInfo.getNumberOfInfos());
        MeasureInputInfo measureInputInfo = seriesInputInfo.getMeasureInfo(0);
        Measure measure = measureInputInfo.getMeasure();
        assertMeasure(measure, 5.4, 0.3);
        assertEquals(new Range(0, 6), measureInputInfo.getTextRange());
    }

    @Test
    void coupleOfMeasuresOrder()
    {
        parse("23e50; 5.4±0.3; 15±1±2±3; ");
        assertEquals(3, seriesInputInfo.getNumberOfInfos());
        assertMeasure(seriesInputInfo.getMeasureInfo(0).getMeasure(), 23e50);
        assertMeasure(seriesInputInfo.getMeasureInfo(1).getMeasure(), 5.4, 0.3);
        assertMeasure(seriesInputInfo.getMeasureInfo(2).getMeasure(), 15, 1, 2, 3);
    }

    @Test
    void coupleOfMeasuresRanges()
    {
        parse("23e50; 5.4±0.3; 15±1±2±3; ");
        assertEquals(3, seriesInputInfo.getNumberOfInfos());
        assertEquals(new Range(0, 4), seriesInputInfo.getMeasureInfo(0).getTextRange());
        assertEquals(new Range(7, 13), seriesInputInfo.getMeasureInfo(1).getTextRange());
        assertEquals(new Range(16, 23), seriesInputInfo.getMeasureInfo(2).getTextRange());
    }

    @Test
    void coupleOfMeasuresWithMessedSpacingOrder()
    {
        parse(";; 1;;2;  3;   ;");
        assertEquals(3, seriesInputInfo.getNumberOfInfos());
        assertMeasure(seriesInputInfo.getMeasureInfo(0).getMeasure(), 1);
        assertMeasure(seriesInputInfo.getMeasureInfo(1).getMeasure(), 2);
        assertMeasure(seriesInputInfo.getMeasureInfo(2).getMeasure(), 3);
    }

    @Test
    void coupleOfMeasuresWithMessedSpacingRanges()
    {
        parse(";; 1;;2;  3;   ;");
        assertEquals(3, seriesInputInfo.getNumberOfInfos());
        assertEquals(new Range(3), seriesInputInfo.getMeasureInfo(0).getTextRange());
        assertEquals(new Range(6), seriesInputInfo.getMeasureInfo(1).getTextRange());
        assertEquals(new Range(10), seriesInputInfo.getMeasureInfo(2).getTextRange());
    }

    @Test
    void badMeasuresShouldReturnIncorrectInfo()
    {
        parse("15±foo±3; 5e800±3; 12±; ");
        assertEquals(3, seriesInputInfo.getNumberOfInfos());
        seriesInputInfo.getAllInfos()
                .forEach((i) -> assertEquals(false, i.isCorrect()));
    }

    @Test
    void badMeasuresErrorRanges()
    {
        parse("15±foo±3; 5e800±3; 12±; ");
        assertEquals(3, seriesInputInfo.getNumberOfInfos());
        assertEquals(new Range(3, 5), seriesInputInfo.getMeasureInfo(0).getErrorRange());
        assertEquals(new Range(10, 14), seriesInputInfo.getMeasureInfo(1).getErrorRange());
        assertEquals(new Range(21), seriesInputInfo.getMeasureInfo(2).getErrorRange());
    }

    @Test
    void selectionInSingleMeasure()
    {
        parse("100; 200; 300; 400; 500", 5, 3);
        assertEquals(new Range(1), seriesInputInfo.getMeasuresInSelection());
        parse("100; 200; 300; 400; 500", 6, 1);
        assertEquals(new Range(1), seriesInputInfo.getMeasuresInSelection());
    }

    @Test
    void selectionInSingleMeasureWithSpaces()
    {
        parse("100; 200; 300; 400; 500", 3, 7);
        assertEquals(new Range(1), seriesInputInfo.getMeasuresInSelection());
    }

    @Test
    void selectionInSpaces()
    {
        parse("100; 200; 300; 400; 500", 8, 2);
        assertEquals(null, seriesInputInfo.getMeasuresInSelection());
    }

    @Test
    void selectionInMultipleMeasures()
    {
        parse("100; 200; 300; 400; 500", 11, 9);
        assertEquals(new Range(2, 3), seriesInputInfo.getMeasuresInSelection());
    }

    @Test
    void zeroLengthSelection()
    {
        parse("100; 200; 300; 400; 500", 1, 0);
        assertEquals(null, seriesInputInfo.getMeasuresInSelection());
    }

    @Test
    void minusOneSelectionStart()
    {
        parse("100; 200; 300; 400; 500", -1, 0);
        assertEquals(null, seriesInputInfo.getMeasuresInSelection());
        parse("100; 200; 300; 400; 500", -1, 5);
        assertEquals(null, seriesInputInfo.getMeasuresInSelection());
    }

    @Test
    void negativeSelectionStartBelowMinusOneShouldThrow()
    {
        assertThrows(StringIndexOutOfBoundsException.class, () -> parse("4", -2, 2));
    }

    @Test
    void negativeSelectionLengthShouldThrow()
    {
        assertThrows(IllegalArgumentException.class, () -> parse("4", 5, -1));
        assertThrows(IllegalArgumentException.class, () -> parse("4", 5, 0));
    }

    @Test
    void invalidSelectionRangeShouldThrow()
    {
        assertThrows(IllegalArgumentException.class, () -> parse("123456789", 3, 7));
        assertThrows(IllegalArgumentException.class, () -> parse("123456789", 9, 1));
    }
}