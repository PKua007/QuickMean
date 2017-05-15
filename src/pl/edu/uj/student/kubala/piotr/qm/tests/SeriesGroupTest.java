package pl.edu.uj.student.kubala.piotr.qm.tests;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.edu.uj.student.kubala.piotr.qm.lab.Measure;
import pl.edu.uj.student.kubala.piotr.qm.lab.Series;
import pl.edu.uj.student.kubala.piotr.qm.lab.SeriesGroup;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
// QuickMean - SeriesGroupTest.java
//---------------------------------------------------------------------
// [opis pliku]
//---------------------------------------------------------------------
// Utworzono 08:37 15.05.2017 w IntelliJ IDEA
// (C)PKua, wszystkie prawa zastrzeżone
//---------------------------------------------------------------------

class SeriesGroupTest
{
    private SeriesGroup seriesGroup;
    private static Series[] series;

    @BeforeAll
    static void setupAll() {
        // Generuj 5 świeżutkich Seriesków
        series = Stream.generate(Series::new).limit(5).toArray(Series[]::new);
    }

    @BeforeEach
    void setupEach() {
        seriesGroup = new SeriesGroup();
    }

    @Test
    void nullSetters() {
        assertThrows(NullPointerException.class, () -> new SeriesGroup(null));
        assertThrows(NullPointerException.class, () -> seriesGroup.setLabelHeader(null));
        assertThrows(NullPointerException.class, () -> seriesGroup.setName(null));
        assertThrows(NullPointerException.class, () -> seriesGroup.setSelectedSeries(null));
    }


    @Test
    void addLast()
    {
        seriesGroup.addSeries(series[0]);
        seriesGroup.addSeries(series[1]);
        seriesGroup.addSeries(series[2]);
        seriesGroup.addSeries(series[3]);

        assertEquals(series[0], seriesGroup.getSeries(0));
        assertEquals(series[3], seriesGroup.getSeries(3));
    }

    @Test
    void addNull()
    {
        assertThrows(NullPointerException.class, () -> seriesGroup.addSeries(null));
    }

    @Test
    void addDuplicate()
    {
        IllegalArgumentException e;
        seriesGroup.addSeries(series[0]);
        e = assertThrows(IllegalArgumentException.class, () -> seriesGroup.addSeries(series[0]));
        assertEquals("Seria jest już w grupie", e.getMessage());
    }

    @Test
    void addLastByMinusOne()
    {
        seriesGroup.addSeries(series[0], -1);
        seriesGroup.addSeries(series[1], -1);
        seriesGroup.addSeries(series[2], -1);
        seriesGroup.addSeries(series[3], -1);

        assertEquals(series[0], seriesGroup.getSeries(0));
        assertEquals(series[3], seriesGroup.getSeries(3));
        assertEquals(4, seriesGroup.getNumberOfSeries());
    }

    @Test
    void addInMiddle()
    {
        seriesGroup.addSeries(series[0]);
        seriesGroup.addSeries(series[1]);
        seriesGroup.addSeries(series[3]);
        seriesGroup.addSeries(series[4]);

        seriesGroup.addSeries(series[2], 2);
        assertEquals(series[1], seriesGroup.getSeries(1));
        assertEquals(series[2], seriesGroup.getSeries(2));
        assertEquals(series[3], seriesGroup.getSeries(3));
        assertEquals(5, seriesGroup.getNumberOfSeries());
    }

    @Test
    void addLastByIndex()
    {
        seriesGroup.addSeries(series[0]);
        seriesGroup.addSeries(series[1], 1);

        assertEquals(series[1], seriesGroup.getSeries(1));
        assertEquals(2, seriesGroup.getNumberOfSeries());
    }

    @Test
    void getBadIndices()
    {
        seriesGroup.addSeries(series[0]);
        seriesGroup.addSeries(series[1]);
        seriesGroup.addSeries(series[2]);

        assertThrows(IndexOutOfBoundsException.class, () -> seriesGroup.getSeries(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> seriesGroup.getSeries(3));
        seriesGroup.getSeries(0);
        seriesGroup.getSeries(2);
    }

    @Test
    void addBadIndices()
    {
        seriesGroup.addSeries(series[0]);
        seriesGroup.addSeries(series[1]);
        seriesGroup.addSeries(series[2]);

        assertThrows(IndexOutOfBoundsException.class, () -> seriesGroup.addSeries(new Series(), -2));
        assertThrows(IndexOutOfBoundsException.class, () -> seriesGroup.addSeries(new Series(), 4));
    }

    @Test
    void deleteByIndex()
    {
        seriesGroup.addSeries(series[0]);
        seriesGroup.addSeries(series[1]);
        seriesGroup.addSeries(series[2]);

        int newsize = seriesGroup.deleteSeries(1);
        assertEquals(series[0], seriesGroup.getSeries(0));
        assertEquals(series[2], seriesGroup.getSeries(1));
        assertEquals(2, newsize);
        assertEquals(2, seriesGroup.getNumberOfSeries());
    }

    @Test
    void deleteByRef()
    {
        seriesGroup.addSeries(series[0]);
        seriesGroup.addSeries(series[1]);
        seriesGroup.addSeries(series[2]);

        int newsize = seriesGroup.deleteSeries(series[1]);
        assertEquals(series[0], seriesGroup.getSeries(0));
        assertEquals(series[2], seriesGroup.getSeries(1));
        assertEquals(2, newsize);
        assertEquals(2, seriesGroup.getNumberOfSeries());
    }

    @Test
    void deleteByBadRef()
    {
        seriesGroup.addSeries(series[0]);
        seriesGroup.addSeries(series[1]);
        seriesGroup.addSeries(series[2]);

        int newsize = seriesGroup.deleteSeries(series[3]);
        assertEquals(series[0], seriesGroup.getSeries(0));
        assertEquals(series[1], seriesGroup.getSeries(1));
        assertEquals(series[2], seriesGroup.getSeries(2));
        assertEquals(3, newsize);
        assertEquals(3, seriesGroup.getNumberOfSeries());
    }

    @Test
    void deleteBadIndices()
    {
        seriesGroup.addSeries(series[0]);
        seriesGroup.addSeries(series[1]);
        seriesGroup.addSeries(series[2]);

        assertThrows(IndexOutOfBoundsException.class, () -> seriesGroup.deleteSeries(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> seriesGroup.getSeries(3));
        seriesGroup.deleteSeries(0);
        assertThrows(IndexOutOfBoundsException.class, () -> seriesGroup.deleteSeries(2));
        seriesGroup.deleteSeries(1);
    }
}