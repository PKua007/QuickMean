// QuickMean - ParsedSeriesInfo.java
//---------------------------------------------------------------------
// [opis pliku]
//---------------------------------------------------------------------
// Utworzono 21:54 21.09.2017 w IntelliJ IDEA
// (C)PKua, wszystkie prawa zastrzeżone
//---------------------------------------------------------------------

package pl.edu.uj.student.kubala.piotr.qm.input;

import pl.edu.uj.student.kubala.piotr.qm.utils.Range;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class SeriesInputInfo
{
    private ArrayList<MeasureInputInfo> measureInputInfos = new ArrayList<>();
    private Range measuresInSelection;
    private String text;

    public SeriesInputInfo(String text) {
        this.text = Objects.requireNonNull(text);
    }

    public void addMeasureInfo(MeasureInputInfo measureInputInfo)
    {
        if (measureInputInfos.contains(measureInputInfo))
            throw new IllegalArgumentException("measureInputInfo already in SeriesInputInfo");
        if (!Range.forText(text).contains(measureInputInfo.getTextRange()))
            throw new IllegalArgumentException("measureInputInfo text range exceeds stored text range");
        measureInputInfos.add(measureInputInfo);
    }

    public MeasureInputInfo getMeasureInfo(int index)
    {
        return measureInputInfos.get(index);
    }

    public int getNumberOfInfos()
    {
        return measureInputInfos.size();
    }

    public boolean empty() {
        return getNumberOfInfos() == 0;
    }

    public List<MeasureInputInfo> getAllInfos()
    {
        return Collections.unmodifiableList(measureInputInfos);
    }

    /**
     * Zwraca zakres informujący które pomiary leżały w zaznaczeniu
     * @return zakres informujący które pomiary leżały w zaznaczeniu
     */
    public Range getMeasuresInSelection() {
        return measuresInSelection;
    }

    public void setMeasuresInSelection(Range measuresInSelection) {
        Objects.requireNonNull(measuresInSelection);
        if (measureInputInfos.isEmpty())
            throw new IllegalArgumentException("MeasureInputInfo list empty; cannot set selection range");
        Range validRange = new Range(0, getNumberOfInfos() - 1);
        if (!validRange.contains(measuresInSelection))
            throw new IllegalArgumentException("measuresInSelection " + measuresInSelection + " exceeds valid range " + validRange);
        this.measuresInSelection = measuresInSelection;
    }

    public void appendText(String text)
    {
        this.text += text;
    }

    public String getText() {
        return text;
    }
}
