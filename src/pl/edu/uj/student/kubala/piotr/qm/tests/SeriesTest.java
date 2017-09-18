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
        assertEquals(0, series.getNumberOfElements());
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
        series.addElement(measure[0]);
        series.addElement(measure[1]);
        series.addElement(measure[2]);
        series.addElement(measure[3]);

        assertEquals(measure[0], series.getElement(0));
        assertEquals(measure[3], series.getElement(3));
    }

    @Test
    void addNull() {
        assertThrows(NullPointerException.class, () -> series.addElement(null));
    }

    @Test
    void addDuplicate() {
        IllegalArgumentException e;
        series.addElement(measure[0]);
        assertThrows(IllegalArgumentException.class, () -> series.addElement(measure[0]));
    }

    @Test
    void addLastByMinusOne() {
        series.addElement(measure[0], -1);
        series.addElement(measure[1], -1);
        series.addElement(measure[2], -1);
        series.addElement(measure[3], -1);

        assertEquals(measure[0], series.getElement(0));
        assertEquals(measure[3], series.getElement(3));
        assertEquals(4, series.getNumberOfElements());
    }

    @Test
    void addInMiddle() {
        series.addElement(measure[0]);
        series.addElement(measure[1]);
        series.addElement(measure[3]);
        series.addElement(measure[4]);

        series.addElement(measure[2], 2);
        assertEquals(measure[1], series.getElement(1));
        assertEquals(measure[2], series.getElement(2));
        assertEquals(measure[3], series.getElement(3));
        assertEquals(5, series.getNumberOfElements());
    }

    @Test
    void addLastByIndex() {
        series.addElement(measure[0]);
        series.addElement(measure[1], 1);

        assertEquals(measure[1], series.getElement(1));
        assertEquals(2, series.getNumberOfElements());
    }

    @Test
    void getBadIndices() {
        series.addElement(measure[0]);
        series.addElement(measure[1]);
        series.addElement(measure[2]);

        assertThrows(IndexOutOfBoundsException.class, () -> series.getElement(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> series.getElement(3));
        series.getElement(0);
        series.getElement(2);
    }

    @Test
    void addBadIndices() {
        series.addElement(measure[0]);
        series.addElement(measure[1]);
        series.addElement(measure[2]);

        assertThrows(IndexOutOfBoundsException.class, () -> series.addElement(new Measure(), -2));
        assertThrows(IndexOutOfBoundsException.class, () -> series.addElement(new Measure(), 4));
    }

    @Test
    void deleteByIndex() {
        series.addElement(measure[0]);
        series.addElement(measure[1]);
        series.addElement(measure[2]);

        int newsize = series.deleteElement(1);
        assertEquals(measure[0], series.getElement(0));
        assertEquals(measure[2], series.getElement(1));
        assertEquals(2, newsize);
        assertEquals(2, series.getNumberOfElements());
    }

    @Test
    void deleteByRef() {
        series.addElement(measure[0]);
        series.addElement(measure[1]);
        series.addElement(measure[2]);

        int newsize = series.deleteElement(measure[1]);
        assertEquals(measure[0], series.getElement(0));
        assertEquals(measure[2], series.getElement(1));
        assertEquals(2, newsize);
        assertEquals(2, series.getNumberOfElements());
    }

    @Test
    void deleteByBadRef() {
        series.addElement(measure[0]);
        series.addElement(measure[1]);
        series.addElement(measure[2]);

        int newsize = series.deleteElement(measure[3]);
        assertEquals(measure[0], series.getElement(0));
        assertEquals(measure[1], series.getElement(1));
        assertEquals(measure[2], series.getElement(2));
        assertEquals(3, newsize);
        assertEquals(3, series.getNumberOfElements());
    }

    @Test
    void deleteBadIndices() {
        series.addElement(measure[0]);
        series.addElement(measure[1]);
        series.addElement(measure[2]);

        assertThrows(IndexOutOfBoundsException.class, () -> series.deleteElement(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> series.deleteElement(3));
        series.deleteElement(0);
        assertThrows(IndexOutOfBoundsException.class, () -> series.deleteElement(2));
        series.deleteElement(1);
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
        series.addElement(measure);

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
        series.addElement(measure);

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
        series.addElement(measure);
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
        series.addElement(new Measure(30));
        series.addElement(new Measure(34));
        series.addElement(new Measure(35));
        series.addElement(new Measure(29));
        series.addElement(new Measure(30));
        series.addElement(new Measure(37));
        series.addElement(new Measure(38));
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
        series.addElement(new Measure(30));
        series.addElement(new Measure(34));
        series.addElement(new Measure(35));
        series.addElement(new Measure(29));
        series.addElement(new Measure(30));
        series.addElement(new Measure(37));
        series.addElement(new Measure(38));
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
        series.addElement(new Measure(30));
        series.addElement(new Measure(34));
        series.addElement(new Measure(35));
        series.addElement(new Measure(29));
        series.addElement(new Measure(30));
        series.addElement(new Measure(37));
        series.addElement(new Measure(38));
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
        series.addElement(new Measure(30, 0, 0, 1));
        series.addElement(new Measure(31, 0, 0, 1.5));
        series.addElement(new Measure(28, 0, 0, 1.3));
        series.addElement(new Measure(35, 0, 0, 0.7));
        series.addElement(new Measure(34, 0, 0, 1.1));
        series.addElement(new Measure(29, 0, 0, 1.3));
        series.addElement(new Measure(32, 0, 0, 1.2));
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
        series.addElement(new Measure(30, 1, 0.2, 0));
        series.addElement(new Measure(31, 2, 0.5, 0));
        series.addElement(new Measure(28, 0.3, 0.5, 0));
        series.addElement(new Measure(35, 4, 1.3, 0));
        series.addElement(new Measure(34, 1.5, 1.3, 0));
        series.addElement(new Measure(29, 0.7, 2, 0));
        series.addElement(new Measure(32, 2, 2, 0));
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
        series.addElement(new Measure(30, 0, 0.2, 0));
        series.addElement(new Measure(31, 0, 0, 1.4));
        series.addElement(new Measure(28, 0.3, 0.5, 2));
        series.addElement(new Measure(35, 4, 0, 0));
        series.addElement(new Measure(34, 0, 1.3, 1));
        series.addElement(new Measure(29, 0.7, 2, 2));
        series.addElement(new Measure(32, 2, 2, 0));
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
                forEach(series::addElement);

        assertThrows(NullPointerException.class, () -> series.setSelectedMeasures(null));
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> series.setSelectedMeasures(new int[]{-1, 6}));
        series.setSelectedMeasures(new int[0]);
        series.setSelectedMeasures(new int[]{0, 1, 2, 3, 4, 5});
    }

    @Test
    void deleteSelectedMeasures() {
        Arrays.stream(measure).
                forEach(series::addElement);

        series.setSelectedMeasures(new int[]{1, 2, 4, 5});
        series.deleteElement(2);
        series.deleteElement(measure[4]);
        series.deleteElement(measure[0]);
        assertArrayEquals(new int[]{0, 2}, series.getSelectedMeasures(), Arrays.toString(series.getSelectedMeasures()));

        series.deleteElement(measure[1]);
        series.deleteElement(measure[5]);
        assertArrayEquals(new int[0], series.getSelectedMeasures());
    }

    @Test
    void checkSelectedMeasuresAfterAddition() {
        Arrays.stream(measure).
                forEach(series::addElement);

        series.setSelectedMeasures(new int[]{1, 2, 4, 5});
        series.addElement(new Measure());
        assertArrayEquals(new int[]{1, 2, 4, 5}, series.getSelectedMeasures());
        series.addElement(new Measure(), 2);
        assertArrayEquals(new int[]{1, 3, 5, 6}, series.getSelectedMeasures());
    }
}