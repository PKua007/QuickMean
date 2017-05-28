// QuickMean - LabProjectTest.java
//---------------------------------------------------------------------
// [opis pliku]
//---------------------------------------------------------------------
// Utworzono 12:01 28.05.2017 w IntelliJ IDEA
// (C)PKua, wszystkie prawa zastrzeżone
//---------------------------------------------------------------------

package pl.edu.uj.student.kubala.piotr.qm.tests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.edu.uj.student.kubala.piotr.qm.lab.*;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

class LabProjectTest
{
    private Measure measure1;
    private Series series1;
    private SeriesGroup seriesGroup1;
    private LabProject labProject;

    private volatile int he = 0;

    @Test
    void propertyChangeEventPropagation()
    {
        measure1 = new Measure(1, 2, 3, 4);
        series1 = new Series();
        seriesGroup1 = new SeriesGroup();
        labProject = new LabProject(null);

        series1.addMeasure(measure1);
        seriesGroup1.addSeries(series1);
        labProject.addSeriesGroup(seriesGroup1);

        final ArrayList<PropertyChangeEvent> fired = new ArrayList<>();
        labProject.addPropertyChangeListener(fired::add);

        measure1.setValue(4);
        assertEquals(1, fired.size());
        assertEquals(measure1, fired.get(0).getSource());
    }
}