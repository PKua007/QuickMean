// QuickMean - MeasuresInput.java
//---------------------------------------------------------------------
// Widok okna z wprowadzonymi pomiarami do średniej i przycisków
// kontrolujących to okno.
//---------------------------------------------------------------------
// Utworzono 20:39 02.05.2017 w IntelliJ IDEA
// (C)PKua, wszystkie prawa zastrzeżone
//---------------------------------------------------------------------

package pl.edu.uj.student.kubala.piotr.qm.input;

import pl.edu.uj.student.kubala.piotr.qm.QuickFrame;
import pl.edu.uj.student.kubala.piotr.qm.View;
import pl.edu.uj.student.kubala.piotr.qm.lab.LabProject;
import pl.edu.uj.student.kubala.piotr.qm.lab.Series;
import pl.edu.uj.student.kubala.piotr.qm.lab.SeriesGroup;
import pl.edu.uj.student.kubala.piotr.qm.utils.Range;

import javax.swing.*;
import javax.swing.text.*;
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
    private static final MutableAttributeSet    FOCUS_ATTR_SET = new SimpleAttributeSet();
    private static final MutableAttributeSet    VALUE_ATTR_SET = new SimpleAttributeSet();
    private static final MutableAttributeSet    ERROR_ATTR_SET = new SimpleAttributeSet();
    private static final Color                  FOCUS_BG_COLOR = new Color(0xB9E8EE);
    private static final Color                  VALUE_BG_COLOR = new Color(0xF0EDD7);
    private static final Color                  ERROR_BG_COLOR = new Color(0xECC6C0);

    private static final int        INPUT_BUTTONS_GAP = 7;     // Odstęp między okienkiem i przyciskami
    private static final int        BUTTONS_GAP = 10;           // Odstęp między przyciskami
    private static final int        INPUT_WINDOW_HEIGHT = 100;  // Wysokość okna z pomiarami

    private LabProject  labProject;     // Projekt laboratorium
    private QuickFrame parentFrame;    // Główka ramka
    private SeriesInputInfo seriesInputInfo;    // Aktualna informacja o serii

    private JPanel          panel;
    private JTextPane       inputPane;      // Okienko z pomiarami
    private JButton         individualErrorsButton;
    private JButton         deleteMeasureButton;
    private JButton         nextMeasureButton;

    static {
        // Ustaw style dla odpowiednich części pomarów
        StyleConstants.setBackground(FOCUS_ATTR_SET, FOCUS_BG_COLOR);
        StyleConstants.setBackground(VALUE_ATTR_SET, VALUE_BG_COLOR);
        StyleConstants.setBackground(ERROR_ATTR_SET, ERROR_BG_COLOR);
    }

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

        // Nasłuchuj projektu i swojego modelu
        this.labProject.addPropertyChangeListener(this);
    }

    @Override
    public String getEDTInitializableName() {
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

    public JTextPane getInputPane() {
        return inputPane;
    }

    /* Metoda ustawia nowy tekst w oknie. Chwilowo odinstalowuje filtr, żeby nie łapał */
    private void setInputText(String text)
    {
        AbstractDocument doc = (AbstractDocument) this.inputPane.getDocument();
        DocumentFilter filter = doc.getDocumentFilter();
        doc.setDocumentFilter(null);
        inputPane.setText(text);
        doc.setDocumentFilter(filter);
    }

    public void highlightInputPane()
    {
        if (seriesInputInfo == null)
            return;
        if (!seriesInputInfo.getText().equals(inputPane.getText()))
            return;//throw new RuntimeException("Measure input pane text desynchronization");

        MeasureInputInfo caretInfo = seriesInputInfo.getMeasureInfoForCaretPos(inputPane.getCaretPosition());
        StyledDocument document = inputPane.getStyledDocument();
        document.setCharacterAttributes(0, document.getLength(), new SimpleAttributeSet(), true);
        for (MeasureInputInfo measureInputInfo : seriesInputInfo.getAllInfos()) {
            if (measureInputInfo == caretInfo) {
                Range textRange = measureInputInfo.getTextRange();
                document.setCharacterAttributes(textRange.getMin(), textRange.getLength(), FOCUS_ATTR_SET, true);
            }
            else if (measureInputInfo.isCorrect()) {
                Range valueRange = measureInputInfo.getValueRange();
                document.setCharacterAttributes(valueRange.getMin(), valueRange.getLength(), VALUE_ATTR_SET, true);
            }

            if (!measureInputInfo.isCorrect())
            {
                Range errorRange = measureInputInfo.getErrorRange();
                document.setCharacterAttributes(errorRange.getMin(), errorRange.getLength(), ERROR_ATTR_SET, true);
            }
        }
    }

    public void setSeriesInputInfo(SeriesInputInfo seriesInputInfo) {
        this.seriesInputInfo = seriesInputInfo;
        if (seriesInputInfo == null) {
            setInputText("");
        } else {
            if (!inputPane.getText().equals(seriesInputInfo.getText()))
                setInputText(seriesInputInfo.getText());
            highlightInputPane();
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        switch (evt.getPropertyName())
        {
            case SeriesGroup.HIGHLIGHTED_SERIES:
            case LabProject.SELECTED_GROUP:
                Series highlightedSeries = labProject.getHighlightedSeries();
                if (highlightedSeries == null)
                    break;
                SeriesParser seriesParser = new SeriesParser();
                SeriesInputInfo inputInfo = seriesParser.printSeries(highlightedSeries);
                setSeriesInputInfo(inputInfo);
                break;
        }
    }
}
