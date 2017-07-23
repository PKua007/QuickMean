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
import java.awt.*;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Objects;

public class MeasuresInput implements View, PropertyChangeListener
{
    private static final String     INDIVIDUAL_ERRORS = "<html><center>Indywidualne<br/>niepewności</center></html>";
    private static final String     DELETE_MEASURE = "<html><center>Usuń<br/>pomiar</center></html>";
    private static final String     NEXT_SERIES = "<html><center>Następna<br/>seria</center></html>";
    private static final Font       EDITOR_FONT = new Font("DialogInput", Font.BOLD, 12);

    private static final int        INPUT_BUTTONS_GAP = 7;     // Odstęp między okienkiem i przyciskami
    private static final int        BUTTONS_GAP = 10;           // Odstęp między przyciskami
    private static final int        INPUT_WINDOW_HEIGHT = 100;  // Wysokość okna z pomiarami

    private LabProject  labProject;     // Projekt laboratorium
    private QuickFrame  parentFrame;    // Główka ramka

    private JPanel          panel;
    private JTextPane       inputPane;      // Okienko z pomiarami
    private JButton         individualErrorsButton;
    private JButton         deleteMeasureButton;
    private JButton         nextMeasureButton;

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
     * MEtoda zwraca główną ramkę, do której należy okno
     * @return główna ramka
     */
    public QuickFrame getParentFrame() {
        return parentFrame;
    }

    @Override
    public void init() {
        if (this.panel != null)
            throw new RuntimeException("MeasureInput::init wywołane drugi raz");

        // Utwórz pole edycji i przyciski
        this.inputPane = new JTextPane();
        //this.inputPane.setPreferredSize(new Dimension(0, 50));
        this.inputPane.setFont(EDITOR_FONT);
        this.individualErrorsButton = new JButton(INDIVIDUAL_ERRORS);
        this.individualErrorsButton.setMnemonic(KeyEvent.VK_I);
        this.deleteMeasureButton = new JButton(DELETE_MEASURE);
        this.deleteMeasureButton.setMnemonic(KeyEvent.VK_U);
        this.nextMeasureButton = new JButton(NEXT_SERIES);
        this.nextMeasureButton.setMnemonic(KeyEvent.VK_N);

        // Utwórz scroll panel na edytor i dodaj go do niego
        JScrollPane scrollPane = new JScrollPane(inputPane);
        scrollPane.setPreferredSize(new Dimension(0, INPUT_WINDOW_HEIGHT));

        // Utwórz panel na przyciski i je dodaj
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, BUTTONS_GAP, 0));
        //JPanel buttonsPanel = new JPanel(new GridLayout(1, 3, BUTTONS_GAP, 0));
        buttonsPanel.add(this.individualErrorsButton);
        buttonsPanel.add(this.deleteMeasureButton);
        buttonsPanel.add(this.nextMeasureButton);

        // Utwórz główny panel na wszystkie bajery i dodaj do niego pole z pomiarami i panel z przyciskami
        this.panel = new JPanel(new BorderLayout(0, INPUT_BUTTONS_GAP));
        this.panel.add(scrollPane, BorderLayout.CENTER);
        this.panel.add(buttonsPanel, BorderLayout.SOUTH);

        // Nasłuchuj projektu
        this.labProject.addPropertyChangeListener(this);
    }

    @Override
    public String getElementName() {
        return "MeasuresInput";
    }

    public JButton getIndividualErrorsButton() {
        return Objects.requireNonNull(individualErrorsButton);
    }

    public JButton getDeleteMeasureButton() {
        return Objects.requireNonNull(deleteMeasureButton);
    }

    public JButton getNextMeasureButton() {
        return Objects.requireNonNull(nextMeasureButton);
    }

    public JPanel getPanel() {
        return Objects.requireNonNull(panel);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {

    }
}
