package pl.edu.uj.student.kubala.piotr.qm.tests;

import org.junit.jupiter.api.Test;
import pl.edu.uj.student.kubala.piotr.qm.input.MeasureInputInfo;
import pl.edu.uj.student.kubala.piotr.qm.input.SeriesInputInfo;
import pl.edu.uj.student.kubala.piotr.qm.lab.Measure;
import pl.edu.uj.student.kubala.piotr.qm.utils.Range;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
// QuickMean - SeriesInputInfoTest.java
//---------------------------------------------------------------------
// [opis pliku]
//---------------------------------------------------------------------
// Utworzono 10:36 04.10.2017 w IntelliJ IDEA
// (C)PKua, wszystkie prawa zastrzeżone
//---------------------------------------------------------------------

class SeriesInputInfoTest
{
    private SeriesInputInfo seriesInputInfo;
    private MeasureInputInfo[] someMeasureInputInfos = new MeasureInputInfo[3];

    private void prepareInfo() {
        seriesInputInfo = new SeriesInputInfo("123; 456; 789; ");
        someMeasureInputInfos = new MeasureInputInfo[] {
                MeasureInputInfo.createCorrect(
                        new Measure(123),
                        new Range(0, 2),
                        new Range(0, 2)),
                MeasureInputInfo.createCorrect(
                        new Measure(456),
                        new Range(5, 7),
                        new Range(5, 7)),
                MeasureInputInfo.createCorrect(
                        new Measure(789),
                        new Range(10, 12),
                        new Range(10, 12))};
        Arrays.stream(someMeasureInputInfos)
                .forEach(seriesInputInfo::addMeasureInfo);
    }

    @Test
    void caretInTheMiddle()
    {
        prepareInfo();
        assertEquals(0, seriesInputInfo.getMeasureInfoIdxForCaretPos(1));
        assertEquals(2, seriesInputInfo.getMeasureInfoIdxForCaretPos(12));
    }

    @Test
    void caretAtTheBeginning()
    {
        prepareInfo();
        assertEquals(1,  seriesInputInfo.getMeasureInfoIdxForCaretPos(5));
        assertEquals(2,  seriesInputInfo.getMeasureInfoIdxForCaretPos(10));
    }

    @Test
    void caretAtTheEnd()
    {
        prepareInfo();
        assertEquals(0,  seriesInputInfo.getMeasureInfoIdxForCaretPos(3));
        assertEquals(1,  seriesInputInfo.getMeasureInfoIdxForCaretPos(8));
    }

    @Test
    void homoSelection()
    {
        prepareInfo();
        assertEquals(new Range(1), seriesInputInfo.getMeasureInfosRangeForSelection(5, 2));
        assertEquals(new Range(1), seriesInputInfo.getMeasureInfosRangeForSelection(5, 3));
        assertEquals(new Range(1), seriesInputInfo.getMeasureInfosRangeForSelection(6, 2));
        assertEquals(new Range(1), seriesInputInfo.getMeasureInfosRangeForSelection(4, 5));
    }

    @Test
    void heteroSelection()
    {
        prepareInfo();
        assertEquals(new Range(0, 1), seriesInputInfo.getMeasureInfosRangeForSelection(0, 5));
        assertEquals(new Range(0, 2), seriesInputInfo.getMeasureInfosRangeForSelection(3, 7));
        assertEquals(new Range(0, 1), seriesInputInfo.getMeasureInfosRangeForSelection(3, 2));
        assertEquals(new Range(0, 1), seriesInputInfo.getMeasureInfosRangeForSelection(2, 5));
        assertEquals(new Range(0, 2), seriesInputInfo.getMeasureInfosRangeForSelection(2, 15));
    }

    @Test
    void caretSelection()
    {
        prepareInfo();
        assertEquals(new Range(0), seriesInputInfo.getMeasureInfosRangeForSelection(1, 0));
        assertEquals(new Range(1), seriesInputInfo.getMeasureInfosRangeForSelection(5, 0));
        assertEquals(new Range(0), seriesInputInfo.getMeasureInfosRangeForSelection(3, 0));
    }

    @Test
    void noSelection()
    {
        prepareInfo();
        assertEquals(null, seriesInputInfo.getMeasureInfosRangeForSelection(4, 0));
        assertEquals(null, seriesInputInfo.getMeasureInfosRangeForSelection(-1, 0));
        assertEquals(null, seriesInputInfo.getMeasureInfosRangeForSelection(400, 0));
    }

    @Test
    void selectionForEmpytInfo()
    {
        seriesInputInfo = new SeriesInputInfo("");
        assertEquals(null, seriesInputInfo.getMeasureInfosRangeForSelection(4, 0));
        seriesInputInfo = new SeriesInputInfo("; ");
        assertEquals(null, seriesInputInfo.getMeasureInfosRangeForSelection(0, 1));
    }
}