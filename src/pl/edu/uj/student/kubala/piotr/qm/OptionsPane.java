// QuickMean - OptionsPane.java
//---------------------------------------------------------------------
// Widok panelu z opcjami serii na dole głównego panelu.
//---------------------------------------------------------------------
// Utworzono 20:39 02.05.2017 w IntelliJ IDEA
// (C)PKua, wszystkie prawa zastrzeżone
//---------------------------------------------------------------------

package pl.edu.uj.student.kubala.piotr.qm;

import pl.edu.uj.student.kubala.piotr.qm.lab.LabProject;

public class OptionsPane implements View
{
    private QuickFrame      parentFrame;
    private LabProject      labProject;

    /**
     * Konstruktor panelu opcjami
     * @param parentFrame główna ramka
     * @param labProject projekt laboratorium (model)
     */
    public OptionsPane(QuickFrame parentFrame, LabProject labProject) {
        this.parentFrame = parentFrame;
        this.labProject = labProject;
    }

    @Override
    public void init() {

    }
}
