// QuickMean - MeanDisplay.java
//---------------------------------------------------------------------
// Widok okienka z ładnie wyświetloną, dużą średnią.
//---------------------------------------------------------------------
// Utworzono 20:39 02.05.2017 w IntelliJ IDEA
// (C)PKua, wszystkie prawa zastrzeżone
//---------------------------------------------------------------------

package pl.edu.uj.student.kubala.piotr.qm;

import pl.edu.uj.student.kubala.piotr.qm.lab.LabProject;

public class MeanDisplay implements View
{
    private LabProject      labProject;
    private QuickFrame      parentFrame;

    /**
     * Konstruktor przyjmujący projekt laboratorium
     * @param labProject projekt laboratorium (model)
     * @param parentFrame główna ramka
     */
    public MeanDisplay(QuickFrame parentFrame, LabProject labProject) {
        this.labProject = labProject;
        this.parentFrame = parentFrame;
    }

    /**
     * Konstruktor przyjmujący projekt laboratorium
     * @param labProject - projekt laboratorium (model)
     */
    public MeanDisplay(LabProject labProject) {
        this.labProject = labProject;
    }

    @Override
    public void init() {

    }
}
