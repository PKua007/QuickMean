package pl.edu.uj.student.kubala.piotr.qm.tests;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.edu.uj.student.kubala.piotr.qm.lab.Measure;
import pl.edu.uj.student.kubala.piotr.qm.lab.Series;


import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
// QuickMean - SeriesTest.java
//---------------------------------------------------------------------
// [opis pliku]
//---------------------------------------------------------------------
// Utworzono 02:08 12.05.2017 w IntelliJ IDEA
// (C)PKua, wszystkie prawa zastrzeżone
//---------------------------------------------------------------------

class SeriesTest {

    private Series series;
    private static Measure[] measure;

    @BeforeAll
    static void setupAll() {
        // Generuj 5 świeżutkich Measurków
        measure = Stream.generate(Measure::new).limit(5).toArray(Measure[]::new);
    }

    @BeforeEach
    void setupEach() {
        series = new Series();
    }

    @Test
    void emptySize() {
        assertEquals(0, series.getNumberOfMeasures());
    }

    @Test
    void nullSetters() {
        assertThrows(NullPointerException.class, () -> new Series(null));
        assertThrows(NullPointerException.class, () -> series.setLabel(null));
        assertThrows(NullPointerException.class, () -> series.setSelectedMeasures(null));
    }

    @Test
    void setBadSeriesErrors() {
        IllegalArgumentException e;
        e = assertThrows(IllegalArgumentException.class, () -> series.setCalibrationError(-3));
        assertEquals("błąd wzorcowania musi być nieujemny i skończony: -3.0", e.getMessage());
        e = assertThrows(IllegalArgumentException.class, () -> series.setHumanError(-2));
        assertEquals("błąd człowieka musi być nieujemny i skończony: -2.0", e.getMessage());
        assertThrows(IllegalArgumentException.class, () -> series.setCalibrationError(Double.POSITIVE_INFINITY));
        assertThrows(IllegalArgumentException.class, () -> series.setHumanError(Double.POSITIVE_INFINITY));
        assertThrows(IllegalArgumentException.class, () -> series.setCalibrationError(Double.NEGATIVE_INFINITY));
        assertThrows(IllegalArgumentException.class, () -> series.setHumanError(Double.NEGATIVE_INFINITY));
        assertThrows(IllegalArgumentException.class, () -> series.setCalibrationError(Double.NaN));
        assertThrows(IllegalArgumentException.class, () -> series.setHumanError(Double.NaN));
    }

    @Test
    void setBadSignificantDigits()
    {
        IllegalArgumentException e;
        e = assertThrows(IllegalArgumentException.class, () -> series.setSignificantDigits(-1));
        assertEquals("Liczba cyfr znaczących musi być z przedziału [1, 6]: -1", e.getMessage());
        assertThrows(IllegalArgumentException.class, () -> series.setSignificantDigits(0));
        assertThrows(IllegalArgumentException.class, () -> series.setSignificantDigits(7));
        series.setSignificantDigits(1);
        series.setSignificantDigits(6);
    }
    
    @Test
    void addLast()
    {
        series.addMeasure(measure[0]);
        series.addMeasure(measure[1]);
        series.addMeasure(measure[2]);
        series.addMeasure(measure[3]);

        assertEquals(measure[0], series.getMeasure(0));
        assertEquals(measure[3], series.getMeasure(3));
    }

    @Test
    void addNull()
    {
        assertThrows(NullPointerException.class, () -> series.addMeasure(null));
    }

    @Test
    void addDuplicate()
    {
        IllegalArgumentException e;
        series.addMeasure(measure[0]);
        e = assertThrows(IllegalArgumentException.class, () -> series.addMeasure(measure[0]));
        assertEquals("Pomiar już jest w serii", e.getMessage());
    }

    @Test
    void addLastByMinusOne()
    {
        series.addMeasure(measure[0], -1);
        series.addMeasure(measure[1], -1);
        series.addMeasure(measure[2], -1);
        series.addMeasure(measure[3], -1);

        assertEquals(measure[0], series.getMeasure(0));
        assertEquals(measure[3], series.getMeasure(3));
        assertEquals(4, series.getNumberOfMeasures());
    }

    @Test
    void addInMiddle()
    {
        series.addMeasure(measure[0]);
        series.addMeasure(measure[1]);
        series.addMeasure(measure[3]);
        series.addMeasure(measure[4]);

        series.addMeasure(measure[2], 2);
        assertEquals(measure[1], series.getMeasure(1));
        assertEquals(measure[2], series.getMeasure(2));
        assertEquals(measure[3], series.getMeasure(3));
        assertEquals(5, series.getNumberOfMeasures());
    }

    @Test
    void addLastByIndex()
    {
        series.addMeasure(measure[0]);
        series.addMeasure(measure[1], 1);

        assertEquals(measure[1], series.getMeasure(1));
        assertEquals(2, series.getNumberOfMeasures());
    }

    @Test
    void getBadIndices()
    {
        series.addMeasure(measure[0]);
        series.addMeasure(measure[1]);
        series.addMeasure(measure[2]);

        assertThrows(IndexOutOfBoundsException.class, () -> series.getMeasure(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> series.getMeasure(3));
        series.getMeasure(0);
        series.getMeasure(2);
    }

    @Test
    void addBadIndices()
    {
        series.addMeasure(measure[0]);
        series.addMeasure(measure[1]);
        series.addMeasure(measure[2]);

        assertThrows(IndexOutOfBoundsException.class, () -> series.addMeasure(new Measure(), -2));
        assertThrows(IndexOutOfBoundsException.class, () -> series.addMeasure(new Measure(), 4));
    }

    @Test
    void deleteByIndex()
    {
        series.addMeasure(measure[0]);
        series.addMeasure(measure[1]);
        series.addMeasure(measure[2]);

        int newsize = series.deleteMeasure(1);
        assertEquals(measure[0], series.getMeasure(0));
        assertEquals(measure[2], series.getMeasure(1));
        assertEquals(2, newsize);
        assertEquals(2, series.getNumberOfMeasures());
    }

    @Test
    void deleteByRef()
    {
        series.addMeasure(measure[0]);
        series.addMeasure(measure[1]);
        series.addMeasure(measure[2]);

        int newsize = series.deleteMeasure(measure[1]);
        assertEquals(measure[0], series.getMeasure(0));
        assertEquals(measure[2], series.getMeasure(1));
        assertEquals(2, newsize);
        assertEquals(2, series.getNumberOfMeasures());
    }

    @Test
    void deleteByBadRef()
    {
        series.addMeasure(measure[0]);
        series.addMeasure(measure[1]);
        series.addMeasure(measure[2]);

        int newsize = series.deleteMeasure(measure[3]);
        assertEquals(measure[0], series.getMeasure(0));
        assertEquals(measure[1], series.getMeasure(1));
        assertEquals(measure[2], series.getMeasure(2));
        assertEquals(3, newsize);
        assertEquals(3, series.getNumberOfMeasures());
    }

    @Test
    void deleteBadIndices()
    {
        series.addMeasure(measure[0]);
        series.addMeasure(measure[1]);
        series.addMeasure(measure[2]);

        assertThrows(IndexOutOfBoundsException.class, () -> series.deleteMeasure(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> series.deleteMeasure(3));
        series.deleteMeasure(0);
        assertThrows(IndexOutOfBoundsException.class, () -> series.deleteMeasure(2));
        series.deleteMeasure(1);
    }

    @Test
    void emptyMean()
    {
        series.setSeparateErrors(true);
        series.updateMean();
        assertEquals(0, series.getMean());
        assertEquals(0, series.getCalculatedStandardError());
        assertEquals(0, series.getCalculatedMaxError());

        series.setSeparateErrors(false);
        series.updateMean();
        assertEquals(0, series.getMean());
        assertEquals(0, series.getCalculatedStandardError());
        assertEquals(0, series.getCalculatedMaxError());
    }

    @Test
    void singleMeanAllErrors()
    {
        Measure measure = new Measure(30, 3, 2, 1);
        series.addMeasure(measure);

        series.setSeparateErrors(true);
        series.updateMean();
        assertEquals(30, series.getMean());
        assertEquals(1, series.getCalculatedStandardError());
        assertEquals(5, series.getCalculatedMaxError());

        series.setSeparateErrors(false);
        series.updateMean();
        assertEquals(30, series.getMean());
        assertEquals(2.3094010767585, series.getCalculatedStandardError(),0.00000000000001);
        assertEquals(0, series.getCalculatedMaxError());
    }

    @Test
    void singleMeanNoStdError()
    {
        Measure measure = new Measure(30, 3, 2, 0);
        series.addMeasure(measure);

        series.setSeparateErrors(true);
        series.updateMean();
        assertEquals(30, series.getMean());
        assertEquals(0, series.getCalculatedStandardError());
        assertEquals(5, series.getCalculatedMaxError());

        series.setSeparateErrors(false);
        series.updateMean();
        assertEquals(30, series.getMean());
        assertEquals(2.08166599946613, series.getCalculatedStandardError(), 0.00000000000001);
        assertEquals(0, series.getCalculatedMaxError());
    }

    @Test
    void singleMeanDefaultErrors()
    {
        Measure measure = new Measure(30, 0, 0, 0);
        series.addMeasure(measure);
        series.setCalibrationError(3);
        series.setHumanError(2);

        series.setSeparateErrors(true);
        series.updateMean();
        assertEquals(30, series.getMean());
        assertEquals(0, series.getCalculatedStandardError());
        assertEquals(5, series.getCalculatedMaxError());

        series.setSeparateErrors(false);
        series.updateMean();
        assertEquals(30, series.getMean());
        assertEquals(2.08166599946613, series.getCalculatedStandardError(), 0.00000000000001);
        assertEquals(0, series.getCalculatedMaxError());
    }

    @Test
    void standardErrorWithoutMax()
    {
        series.addMeasure(new Measure(30));
        series.addMeasure(new Measure(34));
        series.addMeasure(new Measure(35));
        series.addMeasure(new Measure(29));
        series.addMeasure(new Measure(30));
        series.addMeasure(new Measure(37));
        series.addMeasure(new Measure(38));
        series.setCalibrationError(0);
        series.setHumanError(0);

        series.setSeparateErrors(false);
        series.updateMean();
        assertEquals(33.2857142857143, series.getMean(), 0.0000000000001);
        assertEquals(1.37519324554225, series.getCalculatedStandardError(), 0.00000000000001);
        assertEquals(0, series.getCalculatedMaxError());

        series.setSeparateErrors(true);
        series.updateMean();
        assertEquals(33.2857142857143, series.getMean(), 0.0000000000001);
        assertEquals(1.37519324554225, series.getCalculatedStandardError(), 0.00000000000001);
        assertEquals(0, series.getCalculatedMaxError());
    }

    @Test
    void standardErrorWithMax()
    {
        series.addMeasure(new Measure(30));
        series.addMeasure(new Measure(34));
        series.addMeasure(new Measure(35));
        series.addMeasure(new Measure(29));
        series.addMeasure(new Measure(30));
        series.addMeasure(new Measure(37));
        series.addMeasure(new Measure(38));
        series.setCalibrationError(0.5);
        series.setHumanError(1.3);

        series.setSeparateErrors(false);
        series.updateMean();
        assertEquals(33.2857142857143, series.getMean(), 0.0000000000001);
        assertEquals(1.59305465356707, series.getCalculatedStandardError(), 0.00000000000001);
        assertEquals(0, series.getCalculatedMaxError());

        series.setSeparateErrors(true);
        series.updateMean();
        assertEquals(33.2857142857143, series.getMean(), 0.0000000000001);
        assertEquals(1.37519324554225, series.getCalculatedStandardError(), 0.00000000000001);
        assertEquals(1.8, series.getCalculatedMaxError());
    }

    @Test
    void specifiedStandardErrors()
    {
        series.addMeasure(new Measure(30, 0, 0, 1));
        series.addMeasure(new Measure(31, 0, 0, 1.5));
        series.addMeasure(new Measure(28, 0, 0, 1.3));
        series.addMeasure(new Measure(35, 0, 0, 0.7));
        series.addMeasure(new Measure(34, 0, 0, 1.1));
        series.addMeasure(new Measure(29, 0, 0, 1.3));
        series.addMeasure(new Measure(32, 0, 0, 1.2));
        series.setCalibrationError(2);
        series.setHumanError(1);

        series.setSeparateErrors(false);
        series.updateMean();
        assertEquals(31.2857142857143, series.getMean(), 0.0000000000001);
        assertEquals(1.36603602506256, series.getCalculatedStandardError(), 0.00000000000001);
        assertEquals(0, series.getCalculatedMaxError());

        series.setSeparateErrors(true);
        series.updateMean();
        assertEquals(31.2857142857143, series.getMean(), 0.0000000000001);
        assertEquals(0.44652856023108, series.getCalculatedStandardError(), 0.00000000000001);
        assertEquals(3, series.getCalculatedMaxError());
    }

    @Test
    void specifiedMaxErrors()
    {
        series.addMeasure(new Measure(30, 1, 0.2, 0));
        series.addMeasure(new Measure(31, 2, 0.5, 0));
        series.addMeasure(new Measure(28, 0.3, 0.5, 0));
        series.addMeasure(new Measure(35, 4, 1.3, 0));
        series.addMeasure(new Measure(34, 1.5, 1.3, 0));
        series.addMeasure(new Measure(29, 0.7, 2, 0));
        series.addMeasure(new Measure(32, 2, 2, 0));
        series.setCalibrationError(0);
        series.setHumanError(0);

        series.setSeparateErrors(false);
        series.updateMean();
        assertEquals(31.2857142857143, series.getMean(), 0.0000000000001);
        assertEquals(1.50077077702428, series.getCalculatedStandardError(), 0.00000000000001);
        assertEquals(0, series.getCalculatedMaxError());

        series.setSeparateErrors(true);
        series.updateMean();
        assertEquals(31.2857142857143, series.getMean(), 0.0000000000001);
        assertEquals(0.96890428330361, series.getCalculatedStandardError(), 0.00000000000001);
        assertEquals(2.75714285714286, series.getCalculatedMaxError(), 0.0000000000001);
    }

    @Test
    void mixedSpecifiedAndUnspecifiedErrors()
    {
        series.addMeasure(new Measure(30, 0, 0.2, 0));
        series.addMeasure(new Measure(31, 0, 0, 1.4));
        series.addMeasure(new Measure(28, 0.3, 0.5, 2));
        series.addMeasure(new Measure(35, 4, 0, 0));
        series.addMeasure(new Measure(34, 0, 1.3, 1));
        series.addMeasure(new Measure(29, 0.7, 2, 2));
        series.addMeasure(new Measure(32, 2, 2, 0));
        series.setCalibrationError(0.4);
        series.setHumanError(1.4);

        series.setSeparateErrors(false);
        series.updateMean();
        assertEquals(31.2857142857143, series.getMean(), 0.0000000000001);
        assertEquals(1.26894582941063, series.getCalculatedStandardError(), 0.00000000000001);
        assertEquals(0, series.getCalculatedMaxError());

        series.setSeparateErrors(true);
        series.updateMean();
        assertEquals(31.2857142857143, series.getMean(), 0.0000000000001);
        assertEquals(0.791205302626183, series.getCalculatedStandardError(), 0.00000000000001);
        assertEquals(2.42857142857143, series.getCalculatedMaxError(), 0.0000000000001);
    }
}