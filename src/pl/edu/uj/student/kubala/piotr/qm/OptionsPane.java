// QuickMean - OptionsPane.java
//---------------------------------------------------------------------
// Widok panelu z opcjami serii na dole głównego panelu.
//---------------------------------------------------------------------
// Utworzono 20:39 02.05.2017 w IntelliJ IDEA
// (C)PKua, wszystkie prawa zastrzeżone
//---------------------------------------------------------------------

package pl.edu.uj.student.kubala.piotr.qm;

import pl.edu.uj.student.kubala.piotr.qm.lab.LabProject;
import pl.edu.uj.student.kubala.piotr.qm.lab.Measure;
import pl.edu.uj.student.kubala.piotr.qm.lab.Series;
import pl.edu.uj.student.kubala.piotr.qm.lab.SeriesGroup;
import pl.edu.uj.student.kubala.piotr.qm.utils.RoundedBorder;
import pl.edu.uj.student.kubala.piotr.qm.utils.SpringUtilities;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static pl.edu.uj.student.kubala.piotr.qm.QuickFrame.TITLE_COLOR;
import static pl.edu.uj.student.kubala.piotr.qm.QuickFrame.BORDER_COLOR;
import static pl.edu.uj.student.kubala.piotr.qm.QuickFrame.BORDER_RADIUS;

public class OptionsPane implements View, PropertyChangeListener
{
    private static final String     CALIBRATION_ERROR = "Błąd wzorcowania";
    private static final String     HUMAN_ERROR = "Błąd człowieka";
    private static final String     USE_FISHER = "Poprawka Studenta-Fishera";
    private static final String     SIGNIFICANT_DIGITS = "Cyfry znaczące";
    private static final String     SEPARATE_ERRORS = "Rozdzielaj niepewności";
    private static final String     SERIES_OPTIONS = "Opcje serii";

    private static final int        GRID_X_PADDING = 5;
    private static final int        GRID_Y_PADDING = 4;
    private static final int        SIGNIFICANT_DIGITS_COMBO_BOX_HEIGHT = 18;
    private static final int        PANEL_TOP_PADDING = 3;
    private static final int        PANEL_LEFT_PADDING = 2;
    private static final int        PANEL_BOTTOM_PADDING = 0;
    private static final int        PANEL_RIGHT_PADDING = 0;
    private static final int        MIN_DIGITS = 1;
    private static final int        MAX_DIGITS = 6;

    private QuickFrame      parentFrame;
    private LabProject      labProject;
    private JPanel          panel;
    private JTextField      calibrationErrorField;
    private JTextField      humanErrorField;
    private JCheckBox       fisherCheckBox;
    private JCheckBox       separateErrorsCheckBox;
    private JComboBox<Integer>  significantDigitsComboBox;

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
    public void init()
    {
        if (this.panel != null)
            throw new RuntimeException("MeanDisplay::init wywołane drugi raz");

        // Utwórz labale z tekstem
        JLabel calErrorLabel = new JLabel(CALIBRATION_ERROR, SwingConstants.LEADING);
        JLabel humErrorLabel = new JLabel(HUMAN_ERROR, SwingConstants.LEADING);
        JLabel studLabel = new JLabel(USE_FISHER, SwingConstants.LEADING);
        JLabel signifLabel = new JLabel(SIGNIFICANT_DIGITS, SwingConstants.LEADING);
        JLabel sepLabel = new JLabel(SEPARATE_ERRORS, SwingConstants.LEADING);

        // Utwórz kontrolki i "zresetuj"
        this.calibrationErrorField = new JTextField();
        this.calibrationErrorField.setEnabled(false);
        this.humanErrorField = new JTextField();
        this.humanErrorField.setEnabled(false);
        this.fisherCheckBox = new JCheckBox();
        this.fisherCheckBox.setEnabled(false);
        this.separateErrorsCheckBox = new JCheckBox();
        this.separateErrorsCheckBox.setEnabled(false);
        this.significantDigitsComboBox = new JComboBox<>(
                IntStream.rangeClosed(MIN_DIGITS, MAX_DIGITS)
                .boxed()
                .toArray(Integer[]::new)
        );
        this.significantDigitsComboBox.setPreferredSize(new Dimension(0, SIGNIFICANT_DIGITS_COMBO_BOX_HEIGHT));
        this.significantDigitsComboBox.setSelectedIndex(-1);
        this.significantDigitsComboBox.setEnabled(false);

        // Utwórz panel
        this.panel = new JPanel(new SpringLayout());
        Border roundBorder = new RoundedBorder(BORDER_RADIUS, BORDER_COLOR);
        Border compoundBorder = BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(roundBorder, SERIES_OPTIONS, TitledBorder.CENTER, TitledBorder.TOP, null, TITLE_COLOR),
                BorderFactory.createEmptyBorder(PANEL_TOP_PADDING, PANEL_LEFT_PADDING, PANEL_BOTTOM_PADDING, PANEL_RIGHT_PADDING)
        );
        this.panel.setBorder(compoundBorder);

        // Umieść elementy w panelu na CompactGridzie
        this.panel.add(Box.createRigidArea(new Dimension(0,0)));
        this.panel.add(calErrorLabel);
        this.panel.add(this.calibrationErrorField);

        this.panel.add(Box.createRigidArea(new Dimension(0,0)));
        this.panel.add(humErrorLabel);
        this.panel.add(this.humanErrorField);

        this.panel.add(this.fisherCheckBox);
        this.panel.add(studLabel);
        this.panel.add(Box.createRigidArea(new Dimension(0,0)));

        this.panel.add(Box.createRigidArea(new Dimension(0,0)));
        this.panel.add(signifLabel);
        this.panel.add(this.significantDigitsComboBox);

        this.panel.add(this.separateErrorsCheckBox);
        this.panel.add(sepLabel);
        this.panel.add(Box.createRigidArea(new Dimension(0,0)));

        SpringUtilities.makeCompactGrid(this.panel, 5, 3, 0,0, GRID_X_PADDING, GRID_Y_PADDING);

        // Nasłuchuj projektu
        this.labProject.addPropertyChangeListener(this);
    }

    public JPanel getPanel() {
        return Objects.requireNonNull(panel);
    }

    public JTextField getCalibrationErrorField() {
        return Objects.requireNonNull(calibrationErrorField);
    }

    public JTextField getHumanErrorField() {
        return Objects.requireNonNull(humanErrorField);
    }

    public JCheckBox getFisherCheckBox() {
        return Objects.requireNonNull(fisherCheckBox);
    }

    public JCheckBox getSeparateErrorsCheckBox() {
        return Objects.requireNonNull(separateErrorsCheckBox);
    }

    public JComboBox<Integer> getSignificantDigitsComboBox() {
        return Objects.requireNonNull(significantDigitsComboBox);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt)
    {
        // Zmiana podświetlonej serii (lub w ogóle zmiana grupy)
        switch (evt.getPropertyName()) {
            case LabProject.SELECTED_GROUP:
            case SeriesGroup.HIGHLIGHTED_SERIES:
                // Pobierz podświetloną serię i zaktualizuj
                Series series = this.labProject.getHighlightedSeries();
                // Brak podświetlenia
                if (series == null) {
                    this.separateErrorsCheckBox.setSelected(false);
                    this.separateErrorsCheckBox.setEnabled(false);
                    this.significantDigitsComboBox.setSelectedIndex(-1);
                    this.significantDigitsComboBox.setEnabled(false);
                } else {
                    this.separateErrorsCheckBox.setSelected(series.isSeparateErrors());
                    this.separateErrorsCheckBox.setEnabled(true);

                    int digits = series.getSignificantDigits();
                    // Nieprawidłowa liczba cyfr znaczących - nie wykrzacz, skasuj zaznaczenie
                    if (digits < MIN_DIGITS || digits > MAX_DIGITS)
                        this.significantDigitsComboBox.setSelectedIndex(-1);
                    else
                        this.significantDigitsComboBox.setSelectedIndex(digits - MIN_DIGITS);

                    this.significantDigitsComboBox.setEnabled(true);
                }

                break;
        }
    }
}
