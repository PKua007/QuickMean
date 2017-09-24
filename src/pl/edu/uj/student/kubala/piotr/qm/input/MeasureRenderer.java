// QuickMean - MeasureRenderer.java
//---------------------------------------------------------------------
// [opis pliku]
//---------------------------------------------------------------------
// Utworzono 21:53 21.09.2017 w IntelliJ IDEA
// (C)PKua, wszystkie prawa zastrzeżone
//---------------------------------------------------------------------

package pl.edu.uj.student.kubala.piotr.qm.input;

import pl.edu.uj.student.kubala.piotr.qm.lab.Series;
import pl.edu.uj.student.kubala.piotr.qm.utils.Range;

import javax.swing.*;

public class MeasureRenderer
{
    private JTextPane pane;

    public void renderSeries(Series series)
    {

    }

    public void highlightMeasureRange(SeriesInputInfo seriesInputInfo, Range range)
    {

    }

    public void highlightAllMeasures(SeriesInputInfo seriesInputInfo)
    {
        if (seriesInputInfo.empty())
            return;
        highlightMeasureRange(seriesInputInfo,
                new Range(0, seriesInputInfo.getNumberOfInfos() - 1));
    }

}
