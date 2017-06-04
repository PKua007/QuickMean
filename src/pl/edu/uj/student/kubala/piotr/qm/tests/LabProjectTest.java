// QuickMean - LabProjectTest.java
//---------------------------------------------------------------------
// [opis pliku]
//---------------------------------------------------------------------
// Utworzono 12:01 28.05.2017 w IntelliJ IDEA
// (C)PKua, wszystkie prawa zastrzeżone
//---------------------------------------------------------------------

package pl.edu.uj.student.kubala.piotr.qm.tests;

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

    private volatile int he = 0;

    /*@Test
    void propertyChangeEventPropagation()
    {
        Measure measure1 = new Measure(1, 2, 3, 4);
        Series series1 = new Series();
        SeriesGroup seriesGroup1 = new SeriesGroup();
        LabProject labProject = new LabProject(null);

        series1.addMeasure(measure1);
        seriesGroup1.addSeries(series1);
        labProject.addSeriesGroup(seriesGroup1);

        final ArrayList<PropertyChangeEvent> fired = new ArrayList<>();
        labProject.addPropertyChangeListener(fired::add);

        measure1.setValue(4);
        series1.setHumanError(0.5);
        seriesGroup1.setName("grupa");
        //labProject.setSelectedSeriesGroup(0);

        System.out.println(fired.stream()
                .map(PropertyChangeEvent::toString)
                .reduce("",
                        (str, x) -> str + "\n" + x));

        assertEquals(3, fired.size());

        assertEquals(measure1, fired.get(0).getSource());
        assertEquals(4.0, fired.get(0).getNewValue());

        assertEquals(series1, fired.get(1).getSource());
        assertEquals(0.5, fired.get(1).getNewValue());

        assertEquals(seriesGroup1, fired.get(2).getSource());
        assertEquals("grupa", fired.get(2).getNewValue());
        //assertEquals(labProject, fired.get(3).getSource());
        //assertEquals(0, fired.get(0).getNewValue());
    }*/

    /*@Test
    void propertyChangeEventShouldntPropagate()
    {
        Measure measu
    }*/
}