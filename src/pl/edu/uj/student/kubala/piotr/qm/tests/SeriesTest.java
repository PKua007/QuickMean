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
    private static Measure [] measure;

    @BeforeAll
    static void setupAll()
    {
        // Generuj 5 świeżutkich Measurków
        measure = Stream.generate(Measure::new).limit(5).toArray(Measure[]::new);
    }

    @BeforeEach
    void setupEach()
    {
        series = new Series(null, "test_series");
    }

    @Test
    void emptySizeTest()
    {
        assertEquals(0, series.getNumberOfMeasures());
    }

    @Test
    void nullLabelTest()
    {
        assertThrows(NullPointerException.class, () -> new Series(null, null));
    }

    @Test
    void addLastTest()
    {
        Measure measure1 = new Measure();
        Measure measure4 = new Measure();
        series.addMeasure(measure1);
        series.addMeasure(new Measure());
        series.addMeasure(new Measure());
        series.addMeasure(measure4);

        assertEquals(measure1, series.getMeasure(0));
        assertEquals(measure4, series.getMeasure(3));
    }

    @Test
    void addNullTest()
    {
        assertThrows(NullPointerException.class, () -> series.addMeasure(null));
    }

    @Test
    void addLastByMinusOneTest()
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
    void addInMiddleTest()
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
    void addLastByIndexTest()
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
    void deleteByRefTest()
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
    void deleteByBadRefTest()
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
    void emptyMeanTest()
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
    void singleMeanAllErrorsTest()
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
    void singleMeanNoStdErrorTest()
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
}