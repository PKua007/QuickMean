// QuickMean - MeasuresInput.java
//---------------------------------------------------------------------
// Widok okna z wprowadzonymi pomiarami do średniej i przycisków
// kontrolujących to okno.
//---------------------------------------------------------------------
// Utworzono 20:39 02.05.2017 w IntelliJ IDEA
// (C)PKua, wszystkie prawa zastrzeżone
//---------------------------------------------------------------------

package pl.edu.uj.student.kubala.piotr.qm;

import pl.edu.uj.student.kubala.piotr.qm.lab.LabProject;

import javax.swing.*;

public class MeasuresInput implements View
{
    private JTextPane   inputPane;      // Okienko z pomiarami
    private LabProject  labProject;     // Projekt laboratorium
    private QuickFrame  parentFrame;    // Główka ramka

    /**
     * Konstruktor okna z pomiarami. Tworzy JTextPane z zawartością i ustawia jego format.
     * @param parentFrame główna ramka, do której należy okno z pomiarami
     * @param labProject
     */
    public MeasuresInput(QuickFrame parentFrame, LabProject labProject)
    {
        this.parentFrame = parentFrame;
        this.labProject = labProject;
    }

    /**
     * Metoda zwraca panel tekstowy z pomiarami
     * @return panel tekstowy z pomiarami
     */
    public JTextPane getInputPane() {
        return inputPane;
    }

    /**
     * MEtoda zwraca główną ramkę, do której należy okno
     * @return główna ramka
     */
    public QuickFrame getParentFrame() {
        return parentFrame;
    }

    @Override
    public void init() {

    }
}
