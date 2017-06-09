// QuickMean - Main.java
//---------------------------------------------------------------------
// Klasa wejściowa programu. Inicjuje poszczególne moduły.
//---------------------------------------------------------------------
// Utworzono 20:38 02.05.2017 w IntelliJ IDEA
// (C)PKua, wszystkie prawa zastrzeżone
//---------------------------------------------------------------------

package pl.edu.uj.student.kubala.piotr.qm;

import pl.edu.uj.student.kubala.piotr.qm.lab.LabProject;

import javax.swing.*;

public class Main
{
    public static final String  TITLE = "QuickMean 1.0";
    public static final boolean DEBUG = true;

    private static LabProject   labProject;
    private static QuickFrame   quickFrame;

    private static MeasuresInputController  measuresInputController;
    private static GroupController          groupController;
    private static OptionsController        optionsController;

    public static void main(String [] args)
    {
        labProject = new LabProject(null);
        quickFrame = new QuickFrame(TITLE, labProject);

        measuresInputController = new MeasuresInputController(labProject, quickFrame.getMeasuresInput());
        groupController = new GroupController(labProject, quickFrame.getGroupDisplay());
        optionsController = new OptionsController(labProject, quickFrame.getOptionsPane());

        // Umieść inicjację okna i kontolerów na EDT
        SwingUtilities.invokeLater(quickFrame::init);
        SwingUtilities.invokeLater(measuresInputController::setup);
        SwingUtilities.invokeLater(groupController::setup);
        SwingUtilities.invokeLater(optionsController::setup);


    }
}
