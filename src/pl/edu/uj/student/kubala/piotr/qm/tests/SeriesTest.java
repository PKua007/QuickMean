package pl.edu.uj.student.kubala.piotr.qm.tests;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.edu.uj.student.kubala.piotr.qm.lab.Measure;
import pl.edu.uj.student.kubala.piotr.qm.lab.Series;


import java.util.Arrays;
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
        // Generuj 6 świeżutkich Measurków
        measure = Stream.generate(Measure::new).limit(6).toArray(Measure[]::new);
    }

    @BeforeEach
    void setupEach() {
        series = new Series();
    }

    @Test
    void emptySize() {
        assertEquals(0, series.getNumberOfChildren());
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
    void setBadSignificantDigits() {
        IllegalArgumentException e;
        e = assertThrows(IllegalArgumentException.class, () -> series.setSignificantDigits(-1));
        assertEquals("Liczba cyfr znaczących musi być z przedziału [1, 6]: -1", e.getMessage());
        assertThrows(IllegalArgumentException.class, () -> series.setSignificantDigits(0));
        assertThrows(IllegalArgumentException.class, () -> series.setSignificantDigits(7));
        series.setSignificantDigits(1);
        series.setSignificantDigits(6);
    }

    @Test
    void addLast() {
        series.addChild(measure[0]);
        series.addChild(measure[1]);
        series.addChild(measure[2]);
        series.addChild(measure[3]);

        assertEquals(measure[0], series.getChild(0));
        assertEquals(measure[3], series.getChild(3));
    }

    @Test
    void addNull() {
        assertThrows(NullPointerException.class, () -> series.addChild(null));
    }

    @Test
    void addDuplicate() {
        IllegalArgumentException e;
        series.addChild(measure[0]);
        assertThrows(IllegalArgumentException.class, () -> series.addChild(measure[0]));
    }

    @Test
    void addLastByMinusOne() {
        series.addChild(measure[0], -1);
        series.addChild(measure[1], -1);
        series.addChild(measure[2], -1);
        series.addChild(measure[3], -1);

        assertEquals(measure[0], series.getChild(0));
        assertEquals(measure[3], series.getChild(3));
        assertEquals(4, series.getNumberOfChildren());
    }

    @Test
    void addInMiddle() {
        series.addChild(measure[0]);
        series.addChild(measure[1]);
        series.addChild(measure[3]);
        series.addChild(measure[4]);

        series.addChild(measure[2], 2);
        assertEquals(measure[1], series.getChild(1));
        assertEquals(measure[2], series.getChild(2));
        assertEquals(measure[3], series.getChild(3));
        assertEquals(5, series.getNumberOfChildren());
    }

    @Test
    void addLastByIndex() {
        series.addChild(measure[0]);
        series.addChild(measure[1], 1);

        assertEquals(measure[1], series.getChild(1));
        assertEquals(2, series.getNumberOfChildren());
    }

    @Test
    void getBadIndices() {
        series.addChild(measure[0]);
        series.addChild(measure[1]);
        series.addChild(measure[2]);

        assertThrows(IndexOutOfBoundsException.class, () -> series.getChild(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> series.getChild(3));
        series.getChild(0);
        series.getChild(2);
    }

    @Test
    void addBadIndices() {
        series.addChild(measure[0]);
        series.addChild(measure[1]);
        series.addChild(measure[2]);

        assertThrows(IndexOutOfBoundsException.class, () -> series.addChild(new Measure(), -2));
        assertThrows(IndexOutOfBoundsException.class, () -> series.addChild(new Measure(), 4));
    }

    @Test
    void deleteByIndex() {
        series.addChild(measure[0]);
        series.addChild(measure[1]);
        series.addChild(measure[2]);

        int newsize = series.deleteChild(1);
        assertEquals(measure[0], series.getChild(0));
        assertEquals(measure[2], series.getChild(1));
        assertEquals(2, newsize);
        assertEquals(2, series.getNumberOfChildren());
    }

    @Test
    void deleteByRef() {
        series.addChild(measure[0]);
        series.addChild(measure[1]);
        series.addChild(measure[2]);

        int newsize = series.deleteChild(measure[1]);
        assertEquals(measure[0], series.getChild(0));
        assertEquals(measure[2], series.getChild(1));
        assertEquals(2, newsize);
        assertEquals(2, series.getNumberOfChildren());
    }

    @Test
    void deleteByBadRef() {
        series.addChild(measure[0]);
        series.addChild(measure[1]);
        series.addChild(measure[2]);

        int newsize = series.deleteChild(measure[3]);
        assertEquals(measure[0], series.getChild(0));
        assertEquals(measure[1], series.getChild(1));
        assertEquals(measure[2], series.getChild(2));
        assertEquals(3, newsize);
        assertEquals(3, series.getNumberOfChildren());
    }

    @Test
    void deleteBadIndices() {
        series.addChild(measure[0]);
        series.addChild(measure[1]);
        series.addChild(measure[2]);

        assertThrows(IndexOutOfBoundsException.class, () -> series.deleteChild(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> series.deleteChild(3));
        series.deleteChild(0);
        assertThrows(IndexOutOfBoundsException.class, () -> series.deleteChild(2));
        series.deleteChild(1);
    }

    @Test
    void emptyMean() {
        series.setSeparateErrors(true);
        series.updateMean();
        assertEquals(0, series.getMeanQuantity().getValue());
        assertEquals(0, series.getMeanQuantity().getStandardError());
        assertEquals(0, series.getMeanQuantity().getMaxError());

        series.setSeparateErrors(false);
        series.updateMean();
        assertEquals(0, series.getMeanQuantity().getValue());
        assertEquals(0, series.getMeanQuantity().getStandardError());
        assertEquals(0, series.getMeanQuantity().getMaxError());
    }

    @Test
    void singleMeanAllErrors() {
        Measure measure = new Measure(30, 3, 2, 1);
        series.addChild(measure);

        series.setSeparateErrors(true);
        series.updateMean();
        assertEquals(30, series.getMeanQuantity().getValue());
        assertEquals(1, series.getMeanQuantity().getStandardError());
        assertEquals(5, series.getMeanQuantity().getMaxError());

        series.setSeparateErrors(false);
        series.updateMean();
        assertEquals(30, series.getMeanQuantity().getValue());
        assertEquals(2.3094010767585, series.getMeanQuantity().getStandardError(), 0.00000000000001);
        assertEquals(0, series.getMeanQuantity().getMaxError());
    }

    @Test
    void singleMeanNoStdError() {
        Measure measure = new Measure(30, 3, 2, 0);
        series.addChild(measure);

        series.setSeparateErrors(true);
        series.updateMean();
        assertEquals(30, series.getMeanQuantity().getValue());
        assertEquals(0, series.getMeanQuantity().getStandardError());
        assertEquals(5, series.getMeanQuantity().getMaxError());

        series.setSeparateErrors(false);
        series.updateMean();
        assertEquals(30, series.getMeanQuantity().getValue());
        assertEquals(2.08166599946613, series.getMeanQuantity().getStandardError(), 0.00000000000001);
        assertEquals(0, series.getMeanQuantity().getMaxError());
    }

    @Test
    void singleMeanDefaultErrors() {
        Measure measure = new Measure(30, 0, 0, 0);
        series.addChild(measure);
        series.setCalibrationError(3);
        series.setHumanError(2);

        series.setSeparateErrors(true);
        series.updateMean();
        assertEquals(30, series.getMeanQuantity().getValue());
        assertEquals(0, series.getMeanQuantity().getStandardError());
        assertEquals(5, series.getMeanQuantity().getMaxError());

        series.setSeparateErrors(false);
        series.updateMean();
        assertEquals(30, series.getMeanQuantity().getValue());
        assertEquals(2.08166599946613, series.getMeanQuantity().getStandardError(), 0.00000000000001);
        assertEquals(0, series.getMeanQuantity().getMaxError());
    }

    @Test
    void standardErrorWithoutMax() {
        series.addChild(new Measure(30));
        series.addChild(new Measure(34));
        series.addChild(new Measure(35));
        series.addChild(new Measure(29));
        series.addChild(new Measure(30));
        series.addChild(new Measure(37));
        series.addChild(new Measure(38));
        series.setCalibrationError(0);
        series.setHumanError(0);

        series.setSeparateErrors(false);
        series.updateMean();
        assertEquals(33.2857142857143, series.getMeanQuantity().getValue(), 0.0000000000001);
        assertEquals(1.37519324554225, series.getMeanQuantity().getStandardError(), 0.00000000000001);
        assertEquals(0, series.getMeanQuantity().getMaxError());

        series.setSeparateErrors(true);
        series.updateMean();
        assertEquals(33.2857142857143, series.getMeanQuantity().getValue(), 0.0000000000001);
        assertEquals(1.37519324554225, series.getMeanQuantity().getStandardError(), 0.00000000000001);
        assertEquals(0, series.getMeanQuantity().getMaxError());
    }

    @Test
    void standardErrorFisherWithoutMax() {
        series.addChild(new Measure(30));
        series.addChild(new Measure(34));
        series.addChild(new Measure(35));
        series.addChild(new Measure(29));
        series.addChild(new Measure(30));
        series.addChild(new Measure(37));
        series.addChild(new Measure(38));
        series.setCalibrationError(0);
        series.setHumanError(0);
        series.setUseStudentFisher(true);

        series.setSeparateErrors(false);
        series.updateMean();
        assertEquals(33.2857142857143, series.getMeanQuantity().getValue(), 0.0000000000001);
        assertEquals(1.49974322135263, series.getMeanQuantity().getStandardError(), 0.00000000000001);
        assertEquals(0, series.getMeanQuantity().getMaxError());

        series.setSeparateErrors(true);
        series.updateMean();
        assertEquals(33.2857142857143, series.getMeanQuantity().getValue(), 0.0000000000001);
        assertEquals(1.49974322135263, series.getMeanQuantity().getStandardError(), 0.00000000000001);
        assertEquals(0, series.getMeanQuantity().getMaxError());
    }

    @Test
    void standardErrorWithMax() {
        series.addChild(new Measure(30));
        series.addChild(new Measure(34));
        series.addChild(new Measure(35));
        series.addChild(new Measure(29));
        series.addChild(new Measure(30));
        series.addChild(new Measure(37));
        series.addChild(new Measure(38));
        series.setCalibrationError(0.5);
        series.setHumanError(1.3);

        series.setSeparateErrors(false);
        series.updateMean();
        assertEquals(33.2857142857143, series.getMeanQuantity().getValue(), 0.0000000000001);
        assertEquals(1.59305465356707, series.getMeanQuantity().getStandardError(), 0.00000000000001);
        assertEquals(0, series.getMeanQuantity().getMaxError());

        series.setSeparateErrors(true);
        series.updateMean();
        assertEquals(33.2857142857143, series.getMeanQuantity().getValue(), 0.0000000000001);
        assertEquals(1.37519324554225, series.getMeanQuantity().getStandardError(), 0.00000000000001);
        assertEquals(1.8, series.getMeanQuantity().getMaxError());
    }

    @Test
    void specifiedStandardErrors() {
        series.addChild(new Measure(30, 0, 0, 1));
        series.addChild(new Measure(31, 0, 0, 1.5));
        series.addChild(new Measure(28, 0, 0, 1.3));
        series.addChild(new Measure(35, 0, 0, 0.7));
        series.addChild(new Measure(34, 0, 0, 1.1));
        series.addChild(new Measure(29, 0, 0, 1.3));
        series.addChild(new Measure(32, 0, 0, 1.2));
        series.setCalibrationError(2);
        series.setHumanError(1);

        series.setSeparateErrors(false);
        series.updateMean();
        assertEquals(31.2857142857143, series.getMeanQuantity().getValue(), 0.0000000000001);
        assertEquals(1.36603602506256, series.getMeanQuantity().getStandardError(), 0.00000000000001);
        assertEquals(0, series.getMeanQuantity().getMaxError());

        series.setSeparateErrors(true);
        series.updateMean();
        assertEquals(31.2857142857143, series.getMeanQuantity().getValue(), 0.0000000000001);
        assertEquals(0.44652856023108, series.getMeanQuantity().getStandardError(), 0.00000000000001);
        assertEquals(3, series.getMeanQuantity().getMaxError());
    }

    @Test
    void specifiedMaxErrors() {
        series.addChild(new Measure(30, 1, 0.2, 0));
        series.addChild(new Measure(31, 2, 0.5, 0));
        series.addChild(new Measure(28, 0.3, 0.5, 0));
        series.addChild(new Measure(35, 4, 1.3, 0));
        series.addChild(new Measure(34, 1.5, 1.3, 0));
        series.addChild(new Measure(29, 0.7, 2, 0));
        series.addChild(new Measure(32, 2, 2, 0));
        series.setCalibrationError(0);
        series.setHumanError(0);

        series.setSeparateErrors(false);
        series.updateMean();
        assertEquals(31.2857142857143, series.getMeanQuantity().getValue(), 0.0000000000001);
        assertEquals(1.50077077702428, series.getMeanQuantity().getStandardError(), 0.00000000000001);
        assertEquals(0, series.getMeanQuantity().getMaxError());

        series.setSeparateErrors(true);
        series.updateMean();
        assertEquals(31.2857142857143, series.getMeanQuantity().getValue(), 0.0000000000001);
        assertEquals(0.96890428330361, series.getMeanQuantity().getStandardError(), 0.00000000000001);
        assertEquals(2.75714285714286, series.getMeanQuantity().getMaxError(), 0.0000000000001);
    }

    @Test
    void mixedSpecifiedAndUnspecifiedErrors() {
        series.addChild(new Measure(30, 0, 0.2, 0));
        series.addChild(new Measure(31, 0, 0, 1.4));
        series.addChild(new Measure(28, 0.3, 0.5, 2));
        series.addChild(new Measure(35, 4, 0, 0));
        series.addChild(new Measure(34, 0, 1.3, 1));
        series.addChild(new Measure(29, 0.7, 2, 2));
        series.addChild(new Measure(32, 2, 2, 0));
        series.setCalibrationError(0.4);
        series.setHumanError(1.4);

        series.setSeparateErrors(false);
        series.updateMean();
        assertEquals(31.2857142857143, series.getMeanQuantity().getValue(), 0.0000000000001);
        assertEquals(1.26894582941063, series.getMeanQuantity().getStandardError(), 0.00000000000001);
        assertEquals(0, series.getMeanQuantity().getMaxError());

        series.setSeparateErrors(true);
        series.updateMean();
        assertEquals(31.2857142857143, series.getMeanQuantity().getValue(), 0.0000000000001);
        assertEquals(0.791205302626183, series.getMeanQuantity().getStandardError(), 0.00000000000001);
        assertEquals(2.42857142857143, series.getMeanQuantity().getMaxError(), 0.0000000000001);
    }

    @Test
    void setBadSelectedMeasures() {
        Arrays.stream(measure).
                forEach(series::addChild);

        assertThrows(NullPointerException.class, () -> series.setSelectedMeasures(null));
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> series.setSelectedMeasures(new int[]{-1, 6}));
        series.setSelectedMeasures(new int[0]);
        series.setSelectedMeasures(new int[]{0, 1, 2, 3, 4, 5});
    }

    @Test
    void deleteSelectedMeasures() {
        Arrays.stream(measure).
                forEach(series::addChild);

        series.setSelectedMeasures(new int[]{1, 2, 4, 5});
        series.deleteChild(2);
        series.deleteChild(measure[4]);
        series.deleteChild(measure[0]);
        assertArrayEquals(new int[]{0, 2}, series.getSelectedMeasures(), Arrays.toString(series.getSelectedMeasures()));

        series.deleteChild(measure[1]);
        series.deleteChild(measure[5]);
        assertArrayEquals(new int[0], series.getSelectedMeasures());
    }

    @Test
    void checkSelectedMeasuresAfterAddition() {
        Arrays.stream(measure).
                forEach(series::addChild);

        series.setSelectedMeasures(new int[]{1, 2, 4, 5});
        series.addChild(new Measure());
        assertArrayEquals(new int[]{1, 2, 4, 5}, series.getSelectedMeasures());
        series.addChild(new Measure(), 2);
        assertArrayEquals(new int[]{1, 3, 5, 6}, series.getSelectedMeasures());
    }
}