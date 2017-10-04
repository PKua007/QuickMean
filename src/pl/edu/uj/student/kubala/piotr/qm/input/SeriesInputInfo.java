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

    public SeriesInputInfo()
    {
        text = "";
    }

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
        if (measuresInSelection != null) {
            if (measureInputInfos.isEmpty())
                throw new IllegalArgumentException("MeasureInputInfo list empty; cannot set selection range");
            Range validRange = new Range(0, getNumberOfInfos() - 1);
            if (!validRange.contains(measuresInSelection))
                throw new IllegalArgumentException("measuresInSelection " + measuresInSelection + " exceeds valid range " + validRange);
        }
        this.measuresInSelection = measuresInSelection;
    }

    public void appendText(String text)
    {
        this.text += text;
    }

    public int getMeasureInfoIdxForCaretPos(int caretPos)
    {
        Range caretTouchRange = new Range(caretPos - 1, caretPos);
        for (int i = 0; i < measureInputInfos.size(); i++)
            if (measureInputInfos.get(i).getTextRange().overlaps(caretTouchRange))
                return i;
        return -1;
    }

    public MeasureInputInfo getMeasureInfoForCaretPos(int caretPos)
    {
        int idx = getMeasureInfoIdxForCaretPos(caretPos);
        if (idx == -1)
            return null;
        else
            return measureInputInfos.get(idx);
    }

    public Range getMeasureInfosRangeForSelection(int beg, int length)
    {
        if (length < 0)
            throw new IllegalArgumentException("length < 0");
        Range selectionTouchRange = new Range(beg - 1, beg + length);
        int selBeg = -1, selEnd = -1;
        for (int i = 0; i < measureInputInfos.size(); i++) {
            if (measureInputInfos.get(i).getTextRange().overlaps(selectionTouchRange)) {
                if (selBeg == -1)
                    selBeg = i;
            } else if (selBeg != -1 && selEnd == -1) {
                selEnd = i - 1;
            }
        }

        if (selBeg == -1)
            return null;
        else if (selEnd == -1)
            return new Range(selBeg, getNumberOfInfos() - 1);
        else
            return new Range(selBeg, selEnd);
    }

    public String getText() {
        return text;
    }
}
