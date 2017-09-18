package pl.edu.uj.student.kubala.piotr.qm.tests;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.edu.uj.student.kubala.piotr.qm.lab.Series;
import pl.edu.uj.student.kubala.piotr.qm.lab.SeriesGroup;

import java.util.Arrays;
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
        // Generuj 6 świeżutkich Seriesków
        series = Stream.generate(Series::new).limit(6).toArray(Series[]::new);
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
        seriesGroup.addElement(series[0]);
        seriesGroup.addElement(series[1]);
        seriesGroup.addElement(series[2]);
        seriesGroup.addElement(series[3]);

        assertEquals(series[0], seriesGroup.getElement(0));
        assertEquals(series[3], seriesGroup.getElement(3));
    }

    @Test
    void addNull()
    {
        assertThrows(NullPointerException.class, () -> seriesGroup.addElement(null));
    }

    @Test
    void addDuplicate()
    {
        IllegalArgumentException e;
        seriesGroup.addElement(series[0]);
        assertThrows(IllegalArgumentException.class, () -> seriesGroup.addElement(series[0]));
    }

    @Test
    void addLastByMinusOne()
    {
        seriesGroup.addElement(series[0], -1);
        seriesGroup.addElement(series[1], -1);
        seriesGroup.addElement(series[2], -1);
        seriesGroup.addElement(series[3], -1);

        assertEquals(series[0], seriesGroup.getElement(0));
        assertEquals(series[3], seriesGroup.getElement(3));
        assertEquals(4, seriesGroup.getNumberOfElements());
    }

    @Test
    void addInMiddle()
    {
        seriesGroup.addElement(series[0]);
        seriesGroup.addElement(series[1]);
        seriesGroup.addElement(series[3]);
        seriesGroup.addElement(series[4]);

        seriesGroup.addElement(series[2], 2);
        assertEquals(series[1], seriesGroup.getElement(1));
        assertEquals(series[2], seriesGroup.getElement(2));
        assertEquals(series[3], seriesGroup.getElement(3));
        assertEquals(5, seriesGroup.getNumberOfElements());
    }

    @Test
    void addLastByIndex()
    {
        seriesGroup.addElement(series[0]);
        seriesGroup.addElement(series[1], 1);

        assertEquals(series[1], seriesGroup.getElement(1));
        assertEquals(2, seriesGroup.getNumberOfElements());
    }

    @Test
    void getBadIndices()
    {
        seriesGroup.addElement(series[0]);
        seriesGroup.addElement(series[1]);
        seriesGroup.addElement(series[2]);

        assertThrows(IndexOutOfBoundsException.class, () -> seriesGroup.getElement(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> seriesGroup.getElement(3));
        seriesGroup.getElement(0);
        seriesGroup.getElement(2);
    }

    @Test
    void addBadIndices()
    {
        seriesGroup.addElement(series[0]);
        seriesGroup.addElement(series[1]);
        seriesGroup.addElement(series[2]);

        assertThrows(IndexOutOfBoundsException.class, () -> seriesGroup.addElement(new Series(), -2));
        assertThrows(IndexOutOfBoundsException.class, () -> seriesGroup.addElement(new Series(), 4));
    }

    @Test
    void deleteByIndex()
    {
        seriesGroup.addElement(series[0]);
        seriesGroup.addElement(series[1]);
        seriesGroup.addElement(series[2]);

        int newsize = seriesGroup.deleteElement(1);
        assertEquals(series[0], seriesGroup.getElement(0));
        assertEquals(series[2], seriesGroup.getElement(1));
        assertEquals(2, newsize);
        assertEquals(2, seriesGroup.getNumberOfElements());
    }

    @Test
    void deleteByRef()
    {
        seriesGroup.addElement(series[0]);
        seriesGroup.addElement(series[1]);
        seriesGroup.addElement(series[2]);

        int newsize = seriesGroup.deleteElement(series[1]);
        assertEquals(series[0], seriesGroup.getElement(0));
        assertEquals(series[2], seriesGroup.getElement(1));
        assertEquals(2, newsize);
        assertEquals(2, seriesGroup.getNumberOfElements());
    }

    @Test
    void deleteByBadRef()
    {
        seriesGroup.addElement(series[0]);
        seriesGroup.addElement(series[1]);
        seriesGroup.addElement(series[2]);

        int newsize = seriesGroup.deleteElement(series[3]);
        assertEquals(series[0], seriesGroup.getElement(0));
        assertEquals(series[1], seriesGroup.getElement(1));
        assertEquals(series[2], seriesGroup.getElement(2));
        assertEquals(3, newsize);
        assertEquals(3, seriesGroup.getNumberOfElements());
    }

    @Test
    void deleteBadIndices()
    {
        seriesGroup.addElement(series[0]);
        seriesGroup.addElement(series[1]);
        seriesGroup.addElement(series[2]);

        assertThrows(IndexOutOfBoundsException.class, () -> seriesGroup.deleteElement(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> seriesGroup.getElement(3));
        seriesGroup.deleteElement(0);
        assertThrows(IndexOutOfBoundsException.class, () -> seriesGroup.deleteElement(2));
        seriesGroup.deleteElement(1);
    }

    @Test
    void setBadSelectedSeries() {
        Arrays.stream(series).
                forEach(seriesGroup::addElement);

        assertThrows(NullPointerException.class, () -> seriesGroup.setSelectedSeries(null));
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> seriesGroup.setSelectedSeries(new int[]{-1, 6}));
        seriesGroup.setSelectedSeries(new int[0]);
        seriesGroup.setSelectedSeries(new int[]{0, 1, 2, 3, 4, 5});
    }

    @Test
    void deleteSelectedMeasures() {
        Arrays.stream(series).
                forEach(seriesGroup::addElement);

        seriesGroup.setSelectedSeries(new int[]{1, 2, 4, 5});
        seriesGroup.deleteElement(2);
        seriesGroup.deleteElement(series[4]);
        seriesGroup.deleteElement(series[0]);
        assertArrayEquals(new int[]{0, 2}, seriesGroup.getSelectedSeries(), Arrays.toString(seriesGroup.getSelectedSeries()));

        seriesGroup.deleteElement(series[1]);
        seriesGroup.deleteElement(series[5]);
        assertArrayEquals(new int[0], seriesGroup.getSelectedSeries());
    }

    @Test
    void setBadHighlightedSeries() {
        Arrays.stream(series).
                forEach(seriesGroup::addElement);

        assertThrows(IndexOutOfBoundsException.class, () -> seriesGroup.setHighlightedSeries(-2));
        assertThrows(IndexOutOfBoundsException.class, () -> seriesGroup.setHighlightedSeries(6));
        seriesGroup.setHighlightedSeries(-1);
        seriesGroup.setHighlightedSeries(5);
    }

    @Test
    void deletionHighlightedSeriesShift() {
        Arrays.stream(series).
                forEach(seriesGroup::addElement);

        seriesGroup.setHighlightedSeries(3);
        seriesGroup.deleteElement(4);
        assertEquals(3, seriesGroup.getHighlightedSeriesIdx());
        seriesGroup.deleteElement(2);
        assertEquals(2, seriesGroup.getHighlightedSeriesIdx());
        seriesGroup.deleteElement(2);
        assertEquals(-1, seriesGroup.getHighlightedSeriesIdx());
    }
}