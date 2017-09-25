package pl.edu.uj.student.kubala.piotr.qm.tests;

import org.junit.jupiter.api.Test;
import pl.edu.uj.student.kubala.piotr.qm.input.*;
import pl.edu.uj.student.kubala.piotr.qm.utils.Range;

import static org.junit.jupiter.api.Assertions.*;
// QuickMean - MeasureParserTest.java
//---------------------------------------------------------------------
// [opis pliku]
//---------------------------------------------------------------------
// Utworzono 17:03 22.09.2017 w IntelliJ IDEA
// (C)PKua, wszystkie prawa zastrzeżone
//---------------------------------------------------------------------

class MeasureParserTest {
    private MeasureParser measureParser = new MeasureParser();

    @Test
    void parserShouldSaveGivenText() throws ParseException {
        ParsedMeasure parsedMeasure = measureParser.parseMeasure("0.00");
        assertEquals("0.00", parsedMeasure.getText());
    }

    @Test
    void parseDouble() throws ParseException {
        ParsedMeasure parsedMeasure = measureParser.parseMeasure("23.15");
        assertEquals(23.15, parsedMeasure.getMeasure().getValue());
        assertEquals(0, parsedMeasure.getMeasure().getCalibrationError());
        assertEquals(0, parsedMeasure.getMeasure().getHumanError());
        assertEquals(0, parsedMeasure.getMeasure().getStandardError());
        assertEquals(new Range(0, 4), parsedMeasure.getValueRange());
    }

    @Test
    void parseNegativeDouble() throws ParseException {
        ParsedMeasure parsedMeasure = measureParser.parseMeasure("-23.15");
        assertEquals(-23.15, parsedMeasure.getMeasure().getValue());
        assertEquals(0, parsedMeasure.getMeasure().getCalibrationError());
        assertEquals(0, parsedMeasure.getMeasure().getHumanError());
        assertEquals(0, parsedMeasure.getMeasure().getStandardError());
        assertEquals(new Range(0, 5), parsedMeasure.getValueRange());
    }

    @Test
    void parseScientificDouble() throws ParseException {
        ParsedMeasure parsedMeasure = measureParser.parseMeasure("32.132e50");
        assertEquals(32.132e50, parsedMeasure.getMeasure().getValue());
        assertEquals(0, parsedMeasure.getMeasure().getCalibrationError());
        assertEquals(0, parsedMeasure.getMeasure().getHumanError());
        assertEquals(0, parsedMeasure.getMeasure().getStandardError());
        assertEquals(new Range(0, 8), parsedMeasure.getValueRange());
    }

    @Test
    void parseScientificDoubleExplicitPositiveExponent() throws ParseException {
        ParsedMeasure parsedMeasure = measureParser.parseMeasure("32.132e+50");
        assertEquals(32.132e+50, parsedMeasure.getMeasure().getValue());
        assertEquals(0, parsedMeasure.getMeasure().getCalibrationError());
        assertEquals(0, parsedMeasure.getMeasure().getHumanError());
        assertEquals(0, parsedMeasure.getMeasure().getStandardError());
        assertEquals(new Range(0, 9), parsedMeasure.getValueRange());
    }

    @Test
    void parseScientificDoubleNegativeExponent() throws ParseException {
        ParsedMeasure parsedMeasure = measureParser.parseMeasure("32.132e-50");
        assertEquals(32.132e-50, parsedMeasure.getMeasure().getValue());
        assertEquals(0, parsedMeasure.getMeasure().getCalibrationError());
        assertEquals(0, parsedMeasure.getMeasure().getHumanError());
        assertEquals(0, parsedMeasure.getMeasure().getStandardError());
        assertEquals(new Range(0, 9), parsedMeasure.getValueRange());
    }

    @Test
    void parseSomethingDotDouble() throws ParseException {
        ParsedMeasure parsedMeasure = measureParser.parseMeasure("32.");
        assertEquals(32., parsedMeasure.getMeasure().getValue());
        assertEquals(0, parsedMeasure.getMeasure().getCalibrationError());
        assertEquals(0, parsedMeasure.getMeasure().getHumanError());
        assertEquals(0, parsedMeasure.getMeasure().getStandardError());
        assertEquals(new Range(0, 2), parsedMeasure.getValueRange());
    }

    @Test
    void parseDotSomethingDouble() throws ParseException {
        ParsedMeasure parsedMeasure = measureParser.parseMeasure(".123");
        assertEquals(.123, parsedMeasure.getMeasure().getValue());
        assertEquals(0, parsedMeasure.getMeasure().getCalibrationError());
        assertEquals(0, parsedMeasure.getMeasure().getHumanError());
        assertEquals(0, parsedMeasure.getMeasure().getStandardError());
        assertEquals(new Range(0, 3), parsedMeasure.getValueRange());
    }

    @Test
    void parseDotSomethingDoubleScientific() throws ParseException {
        ParsedMeasure parsedMeasure = measureParser.parseMeasure("33.e4");
        assertEquals(33.e4, parsedMeasure.getMeasure().getValue());
        assertEquals(0, parsedMeasure.getMeasure().getCalibrationError());
        assertEquals(0, parsedMeasure.getMeasure().getHumanError());
        assertEquals(0, parsedMeasure.getMeasure().getStandardError());
        assertEquals(new Range(0, 4), parsedMeasure.getValueRange());
    }

    @Test
    void dotAloneShouldThrow()
    {
        assertThrows(MalformedDoubleException.class, () -> measureParser.parseMeasure("."));
    }

    @Test
    void unexpectedSignShouldThrow()
    {
        MalformedDoubleException exception = assertThrows(MalformedDoubleException.class,
                () -> measureParser.parseMeasure("3.e+5lmao"));
        assertEquals(new Range(0, 8), exception.getMalformedRange());
    }

    @Test
    void messedUpExponentShouldThrow()
    {
        MalformedDoubleException exception = assertThrows(MalformedDoubleException.class,
                () -> measureParser.parseMeasure(".23e-freak"));
        assertEquals(new Range(0, 9), exception.getMalformedRange());
    }

    @Test
    void bigNumberShouldThrowDoubleRangeException()
    {
        assertThrows(DoubleRangeException.class, () -> measureParser.parseMeasure("2.3e309"));
    }

    /*@Test
    void smallNumberShouldThrowDoubleRangeException()
    {
        assertThrows(DoubleRangeException.class, () -> measureParser.parseMeasure("2.3e-309"));
    }*/

    @Test
    void doubleRangeEndpointsShouldNotThrow() throws ParseException {
        String min = String.valueOf(Double.MIN_VALUE);
        String max = String.valueOf(Double.MAX_VALUE);

        assertEquals(Double.MIN_VALUE, measureParser.parseMeasure(min).getMeasure().getValue());
        assertEquals(Double.MAX_VALUE, measureParser.parseMeasure(max).getMeasure().getValue());
    }

    @Test
    void parseMeasureWithCalibrationError() throws ParseException {
        ParsedMeasure parsedMeasure = measureParser.parseMeasure("5.34±0.89");
        assertEquals(5.34, parsedMeasure.getMeasure().getValue());
        assertEquals(0.89, parsedMeasure.getMeasure().getCalibrationError());
        assertEquals(0, parsedMeasure.getMeasure().getHumanError());
        assertEquals(0, parsedMeasure.getMeasure().getStandardError());
        assertEquals(new Range(0, 3), parsedMeasure.getValueRange());
    }

    @Test
    void parseMeasureWithScientificCalibrationError() throws ParseException {
        ParsedMeasure parsedMeasure = measureParser.parseMeasure("5.34±8.9e-1");
        assertEquals(5.34, parsedMeasure.getMeasure().getValue());
        assertEquals(8.9e-1, parsedMeasure.getMeasure().getCalibrationError());
        assertEquals(0, parsedMeasure.getMeasure().getHumanError());
        assertEquals(0, parsedMeasure.getMeasure().getStandardError());
        assertEquals(new Range(0, 3), parsedMeasure.getValueRange());
    }

    @Test
    void parseMeasureWithHumanError() throws ParseException {
        ParsedMeasure parsedMeasure = measureParser.parseMeasure("5.34±0.89±0.18");
        assertEquals(5.34, parsedMeasure.getMeasure().getValue());
        assertEquals(0.89, parsedMeasure.getMeasure().getCalibrationError());
        assertEquals(0.18, parsedMeasure.getMeasure().getHumanError());
        assertEquals(0, parsedMeasure.getMeasure().getStandardError());
        assertEquals(new Range(0, 3), parsedMeasure.getValueRange());
    }

    @Test
    void parseMeasureWithStandardError() throws ParseException {
        ParsedMeasure parsedMeasure = measureParser.parseMeasure("5.34±0.89±0.18±0.02");
        assertEquals(5.34, parsedMeasure.getMeasure().getValue());
        assertEquals(0.89, parsedMeasure.getMeasure().getCalibrationError());
        assertEquals(0.18, parsedMeasure.getMeasure().getHumanError());
        assertEquals(0.02, parsedMeasure.getMeasure().getStandardError());
        assertEquals(new Range(0, 3), parsedMeasure.getValueRange());
    }

    @Test
    void minusInErrorShouldThrow()
    {
        NegativeErrorException exception = assertThrows(NegativeErrorException.class,
                () -> measureParser.parseMeasure("5.34±-0.89"));
        assertEquals(new Range(5, 9), exception.getMalformedRange());
    }

    @Test
    void negativeErrorWithOverflowShouldThrowDoubleRange()
    {
        DoubleRangeException exception = assertThrows(DoubleRangeException.class,
                () -> measureParser.parseMeasure("5.34±-0.89e500"));
        assertEquals(new Range(5, 13), exception.getMalformedRange());
    }

    @Test
    void tooManyErrorsShouldThrow()
    {
        TooManyErrorsException exception = assertThrows(TooManyErrorsException.class,
                () -> measureParser.parseMeasure("5.34±0±0±1±2"));
        assertEquals(new Range(10, 11), exception.getMalformedRange());
    }

    @Test
    void emptyTextShouldThrowIllegalArgument()
    {
        assertThrows(IllegalArgumentException.class, () -> measureParser.parseMeasure(""));
    }

    @Test
    void measureStartingWithPlusMinusShouldThrow()
    {
        UnexpectedEndException exception = assertThrows(UnexpectedEndException.class,
                () -> measureParser.parseMeasure("±20±0"));
        assertEquals(new Range(0), exception.getMalformedRange());
    }

    @Test
    void doublePlusMinusShouldThrow()
    {
        UnexpectedEndException exception = assertThrows(UnexpectedEndException.class,
                () -> measureParser.parseMeasure("50±20±0±±60"));
        assertEquals(new Range(8), exception.getMalformedRange());
    }

    @Test
    void errorWithStandalonePlusMinusShouldThrow()
    {
        UnexpectedEndException exception = assertThrows(UnexpectedEndException.class,
                () -> measureParser.parseMeasure("50±"));
        assertEquals(new Range(2), exception.getMalformedRange());
    }
}