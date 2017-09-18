// QuickMean - MeasureInputModel.java
//---------------------------------------------------------------------
// [opis pliku]
//---------------------------------------------------------------------
// Utworzono 22:59 25.07.2017 w IntelliJ IDEA
// (C)PKua, wszystkie prawa zastrzeżone
//---------------------------------------------------------------------

package pl.edu.uj.student.kubala.piotr.qm;

import pl.edu.uj.student.kubala.piotr.qm.lab.Series;

import java.beans.PropertyChangeEvent;

public class MeasureInputModel extends PropagatingListModel<MeasureHolder>
{
    /* Etykiety właściwości */
    private static final String PREFIX = "mim";

    public static final String  NEW_MEASURE_HOLDER  = PREFIX + "." + NEW;
    public static final String  DEL_MEASURE_HOLDER  = PREFIX + "." + DEL;
    public static final String  BOUND_SERIES        = PREFIX + "bound_ser";
    public static final String  LAST_ACCEPTED       = PREFIX + "last_acc";

    private int         focusedIdx;
    private boolean     lastMeasureAccepted;
    private Series      boundSeries;

    public MeasureInputModel() {
        this.focusedIdx = -1;
    }

    public MeasureHolder [] holdersForTextIndex(int idx) {
        return null;
    }

    public void deleteHolderRange(int start, int end) {
        validateIdx(start);
        validateIdx(end - 1);
        if (start > end)
            throw new IllegalArgumentException("start > end");
        while (end-- >= start)
            this.deleteElement(start);
    }

    public Series getBoundSeries() {
        return boundSeries;
    }

    public void setBoundSeries(Series series)
    {
        Object oldSeries = this.boundSeries;

        // TODO tymczasowe dodawanie na chamca - przemyśleć, czy nie zmienić na usuwanie i dodawanie z powiadamianiem
        this.children.clear();
        this.children.ensureCapacity(series.getNumberOfElements());
        MeasureHolder holder;
        for (int i = 0; i < series.getNumberOfElements(); i++) {
            holder = new MeasureHolder(series.getElement(i));
            holder.setParent(this);
            this.children.add(holder);
        }

        this.boundSeries = series;
        PropertyChangeEvent evt = new PropertyChangeEvent(this, BOUND_SERIES, oldSeries, this.boundSeries);
        this.propertyFirer.firePropertyChange(evt);
    }

    public boolean isLastMeasureAccepted() {
        return lastMeasureAccepted;
    }

    public int getFocusedIdx() {
        return focusedIdx;
    }

    public void setFocusedIdx(int focusedIdx) {
        this.focusedIdx = focusedIdx;
    }
}
