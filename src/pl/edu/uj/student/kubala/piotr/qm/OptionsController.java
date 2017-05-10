// QuickMean - OptionsController.java
//---------------------------------------------------------------------
// Kontroler panelu z opcjami serii pomiarowej na dole ekranu.
//---------------------------------------------------------------------
// Utworzono 20:40 02.05.2017 w IntelliJ IDEA
// (C)PKua, wszystkie prawa zastrzeżone
//---------------------------------------------------------------------

package pl.edu.uj.student.kubala.piotr.qm;

import pl.edu.uj.student.kubala.piotr.qm.lab.LabProject;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class OptionsController implements Controller
{
    private LabProject      labProject;
    private OptionsPane     optionsPane;

    /**
     * Konstruktor kontroleta panelu z opcjami przyjmujący model i widok
     * @param labProject projekt laboratorium (model)
     * @param optionsPane widok panelu z opcjami
     */
    public OptionsController(LabProject labProject, OptionsPane optionsPane) {
        this.labProject = labProject;
        this.optionsPane = optionsPane;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {

    }
}
