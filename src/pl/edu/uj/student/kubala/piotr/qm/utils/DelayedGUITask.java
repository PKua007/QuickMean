// QuickMean - DelayedTask.java
//---------------------------------------------------------------------
// [opis pliku]
//---------------------------------------------------------------------
// Utworzono 00:21 01.07.2017 w IntelliJ IDEA
// (C)PKua, wszystkie prawa zastrzeżone
//---------------------------------------------------------------------

package pl.edu.uj.student.kubala.piotr.qm.utils;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Prosta klasa wykonująca przekazane zadanie po jakimś czasie
 */
public class DelayedGUITask
{
    /**
     * Przygotowuje zdarzenie do wywołania - po użyciu konstruktora zegar już tyka złowieszczo
     * @param delay opóźnienie wykonania zadania
     * @param task zadanie do wykonania
     */
    public DelayedGUITask(int delay, Runnable task)
    {
        Timer timer = new Timer(delay, e -> task.run());
        timer.setRepeats(false);
        timer.start();
    }
}
