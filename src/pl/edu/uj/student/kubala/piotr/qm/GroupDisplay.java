// QuickMean - GroupDisplay.java
//---------------------------------------------------------------------
// Widok części panelu głównego z tabelką grupy pomiarów oraz listą
// rozwijaną. Tworzy i zarządza prezentacją danych grupy
//---------------------------------------------------------------------
// Utworzono 20:39 02.05.2017 w IntelliJ IDEA
// (C)PKua, wszystkie prawa zastrzeżone
//---------------------------------------------------------------------

package pl.edu.uj.student.kubala.piotr.qm;

import pl.edu.uj.student.kubala.piotr.qm.lab.LabProject;

public class GroupDisplay implements View
{
    private QuickFrame      parentFrame;
    private LabProject      labProject;

    /**
     * Konstruktor okna z grupami
     * @param parentFrame główna ramka z modułami
     * @param labProject projekt laboratorium (widok)
     */
    public GroupDisplay(QuickFrame parentFrame, LabProject labProject) {
        this.parentFrame = parentFrame;
        this.labProject = labProject;
    }

    @Override
    public void init() {

    }
}
