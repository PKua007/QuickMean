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
        seriesGroup.addChild(series[0]);
        seriesGroup.addChild(series[1]);
        seriesGroup.addChild(series[2]);
        seriesGroup.addChild(series[3]);

        assertEquals(series[0], seriesGroup.getChild(0));
        assertEquals(series[3], seriesGroup.getChild(3));
    }

    @Test
    void addNull()
    {
        assertThrows(NullPointerException.class, () -> seriesGroup.addChild(null));
    }

    @Test
    void addDuplicate()
    {
        IllegalArgumentException e;
        seriesGroup.addChild(series[0]);
        assertThrows(IllegalArgumentException.class, () -> seriesGroup.addChild(series[0]));
    }

    @Test
    void addLastByMinusOne()
    {
        seriesGroup.addChild(series[0], -1);
        seriesGroup.addChild(series[1], -1);
        seriesGroup.addChild(series[2], -1);
        seriesGroup.addChild(series[3], -1);

        assertEquals(series[0], seriesGroup.getChild(0));
        assertEquals(series[3], seriesGroup.getChild(3));
        assertEquals(4, seriesGroup.getNumberOfChildren());
    }

    @Test
    void addInMiddle()
    {
        seriesGroup.addChild(series[0]);
        seriesGroup.addChild(series[1]);
        seriesGroup.addChild(series[3]);
        seriesGroup.addChild(series[4]);

        seriesGroup.addChild(series[2], 2);
        assertEquals(series[1], seriesGroup.getChild(1));
        assertEquals(series[2], seriesGroup.getChild(2));
        assertEquals(series[3], seriesGroup.getChild(3));
        assertEquals(5, seriesGroup.getNumberOfChildren());
    }

    @Test
    void addLastByIndex()
    {
        seriesGroup.addChild(series[0]);
        seriesGroup.addChild(series[1], 1);

        assertEquals(series[1], seriesGroup.getChild(1));
        assertEquals(2, seriesGroup.getNumberOfChildren());
    }

    @Test
    void getBadIndices()
    {
        seriesGroup.addChild(series[0]);
        seriesGroup.addChild(series[1]);
        seriesGroup.addChild(series[2]);

        assertThrows(IndexOutOfBoundsException.class, () -> seriesGroup.getChild(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> seriesGroup.getChild(3));
        seriesGroup.getChild(0);
        seriesGroup.getChild(2);
    }

    @Test
    void addBadIndices()
    {
        seriesGroup.addChild(series[0]);
        seriesGroup.addChild(series[1]);
        seriesGroup.addChild(series[2]);

        assertThrows(IndexOutOfBoundsException.class, () -> seriesGroup.addChild(new Series(), -2));
        assertThrows(IndexOutOfBoundsException.class, () -> seriesGroup.addChild(new Series(), 4));
    }

    @Test
    void deleteByIndex()
    {
        seriesGroup.addChild(series[0]);
        seriesGroup.addChild(series[1]);
        seriesGroup.addChild(series[2]);

        int newsize = seriesGroup.deleteChild(1);
        assertEquals(series[0], seriesGroup.getChild(0));
        assertEquals(series[2], seriesGroup.getChild(1));
        assertEquals(2, newsize);
        assertEquals(2, seriesGroup.getNumberOfChildren());
    }

    @Test
    void deleteByRef()
    {
        seriesGroup.addChild(series[0]);
        seriesGroup.addChild(series[1]);
        seriesGroup.addChild(series[2]);

        int newsize = seriesGroup.deleteChild(series[1]);
        assertEquals(series[0], seriesGroup.getChild(0));
        assertEquals(series[2], seriesGroup.getChild(1));
        assertEquals(2, newsize);
        assertEquals(2, seriesGroup.getNumberOfChildren());
    }

    @Test
    void deleteByBadRef()
    {
        seriesGroup.addChild(series[0]);
        seriesGroup.addChild(series[1]);
        seriesGroup.addChild(series[2]);

        int newsize = seriesGroup.deleteChild(series[3]);
        assertEquals(series[0], seriesGroup.getChild(0));
        assertEquals(series[1], seriesGroup.getChild(1));
        assertEquals(series[2], seriesGroup.getChild(2));
        assertEquals(3, newsize);
        assertEquals(3, seriesGroup.getNumberOfChildren());
    }

    @Test
    void deleteBadIndices()
    {
        seriesGroup.addChild(series[0]);
        seriesGroup.addChild(series[1]);
        seriesGroup.addChild(series[2]);

        assertThrows(IndexOutOfBoundsException.class, () -> seriesGroup.deleteChild(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> seriesGroup.getChild(3));
        seriesGroup.deleteChild(0);
        assertThrows(IndexOutOfBoundsException.class, () -> seriesGroup.deleteChild(2));
        seriesGroup.deleteChild(1);
    }

    @Test
    void setBadSelectedSeries() {
        Arrays.stream(series).
                forEach(seriesGroup::addChild);

        assertThrows(NullPointerException.class, () -> seriesGroup.setSelectedSeries(null));
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> seriesGroup.setSelectedSeries(new int[]{-1, 6}));
        seriesGroup.setSelectedSeries(new int[0]);
        seriesGroup.setSelectedSeries(new int[]{0, 1, 2, 3, 4, 5});
    }

    @Test
    void deleteSelectedMeasures() {
        Arrays.stream(series).
                forEach(seriesGroup::addChild);

        seriesGroup.setSelectedSeries(new int[]{1, 2, 4, 5});
        seriesGroup.deleteChild(2);
        seriesGroup.deleteChild(series[4]);
        seriesGroup.deleteChild(series[0]);
        assertArrayEquals(new int[]{0, 2}, seriesGroup.getSelectedSeries(), Arrays.toString(seriesGroup.getSelectedSeries()));

        seriesGroup.deleteChild(series[1]);
        seriesGroup.deleteChild(series[5]);
        assertArrayEquals(new int[0], seriesGroup.getSelectedSeries());
    }

    @Test
    void setBadHighlightedSeries() {
        Arrays.stream(series).
                forEach(seriesGroup::addChild);

        assertThrows(IndexOutOfBoundsException.class, () -> seriesGroup.setHighlightedSeries(-2));
        assertThrows(IndexOutOfBoundsException.class, () -> seriesGroup.setHighlightedSeries(6));
        seriesGroup.setHighlightedSeries(-1);
        seriesGroup.setHighlightedSeries(5);
    }

    @Test
    void deletionHighlightedSeriesShift() {
        Arrays.stream(series).
                forEach(seriesGroup::addChild);

        seriesGroup.setHighlightedSeries(3);
        seriesGroup.deleteChild(4);
        assertEquals(3, seriesGroup.getHighlightedSeriesIdx());
        seriesGroup.deleteChild(2);
        assertEquals(2, seriesGroup.getHighlightedSeriesIdx());
        seriesGroup.deleteChild(2);
        assertEquals(-1, seriesGroup.getHighlightedSeriesIdx());
    }
}