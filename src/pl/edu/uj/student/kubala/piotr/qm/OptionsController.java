// QuickMean - OptionsController.java
//---------------------------------------------------------------------
// Kontroler panelu z opcjami serii pomiarowej na dole ekranu.
//---------------------------------------------------------------------
// Utworzono 20:40 02.05.2017 w IntelliJ IDEA
// (C)PKua, wszystkie prawa zastrzeżone
//---------------------------------------------------------------------

package pl.edu.uj.student.kubala.piotr.qm;

import pl.edu.uj.student.kubala.piotr.qm.lab.LabProject;
import pl.edu.uj.student.kubala.piotr.qm.lab.Series;
import pl.edu.uj.student.kubala.piotr.qm.utils.DelayedGUITask;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class OptionsController implements Controller, ActionListener, ItemListener
{
    private static final String     INVALID_ERROR_VALUE_MESSAGE = "Niepoprawna wartość błędu - musi to być nieujemna, "
            + "skończona liczba.\n\nPrzykładowe wartości:\n\t2,3   2.8   1.23e4";
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

        EDTInitializationManager manager = EDTInitializationManager.getInstance();
        manager.registerElement(this);
        manager.addDependency(this, optionsPane);
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {

    }

    @Override
    public void init() {
        // Nasłuchuj check boxa osobnych niepewności
        this.optionsPane.getSeparateErrorsCheckBox().setAction(new SeparateErrorsAction());
        // Nasłuchuj check boxa współczynników t-Studenta
        this.optionsPane.getFisherCheckBox().setAction(new UseFisherAction());
        // Nasłuchuj listy z ilością cyfr znacących
        this.optionsPane.getSignificantDigitsComboBox().addItemListener(this);
        // Nasłuchuj na zmianę wartości błędu wzorcowania (i sprawdź poprawność)
        this.optionsPane.getCalibrationErrorField().setInputVerifier(new ErrorFieldVeryfier(ErrorFieldVeryfier.CALIBRATION_ERROR));
        // Nasłuchuj na zmianę wartości błędu człowieka (i sprawdź poprawność)
        this.optionsPane.getHumanErrorField().setInputVerifier(new ErrorFieldVeryfier(ErrorFieldVeryfier.HUMAN_ERROR));
    }

    @Override
    public String getElementName() {
        return "OptionsController";
    }

    @Override
    public void itemStateChanged(ItemEvent e)
    {
        // Zmieniono liczbę cyfr znaczących
        if (e.getStateChange() == ItemEvent.SELECTED) {
            Series [] sel_series = this.labProject.getSelectedSeries();
            if (sel_series.length != 0)
                for (Series s : sel_series)
                    s.setSignificantDigits((int)e.getItem());
        }
    }

    /* Akcja zmiany zaznaczenia czy używać współczynnika t-Student */
    private class UseFisherAction extends AbstractAction
    {
        public UseFisherAction()
        {
            this.enabled = false;
        }

        @Override
        public void actionPerformed(ActionEvent e)
        {
            Series [] sel_series = labProject.getSelectedSeries();
            if (sel_series.length == 0)
                return;
            for (Series s : sel_series) {
                s.setUseStudentFisher(optionsPane.getFisherCheckBox().isSelected());
                s.updateMean();
            }
        }
    }

    /* Zkcja zmiany zaznaczenia czy rozdzielać niepewności */
    private class SeparateErrorsAction extends AbstractAction
    {
        public SeparateErrorsAction()
        {
            this.enabled = false;
        }

        @Override
        public void actionPerformed(ActionEvent e)
        {
            Series [] sel_series = labProject.getSelectedSeries();
            if (sel_series.length == 0)
                return;

            for (Series s : sel_series) {
                s.setSeparateErrors(optionsPane.getSeparateErrorsCheckBox().isSelected());
                s.updateMean();
            }
        }
    }

    /* Prywatna klasa odpowiedzialna za sprawdzanie poprawności wpisanej wartości błędu przy zmianie focusu. Oprócz
     * tego służy za listenera zmian - jeśli wartość jest poprawna, zostanie ustawiona. Zostanie to zrobione zaraz
     * przed zmianą focusu, więc także wtedy, gdy zmieniana jest grupa. Jest więc pewne, że nowa wartość zostanie
     * zapisana. */
    private class ErrorFieldVeryfier extends InputVerifier
    {
        public static final int   HUMAN_ERROR = 0;
        public static final int   CALIBRATION_ERROR = 1;

        private int type;

        public ErrorFieldVeryfier(int type) {
            this.type = type;
        }

        @Override
        public boolean verify(JComponent input)
        {
            JTextField source = (JTextField)input;
            if ("".equals(source.getText()))
                return true;

            double value;
            try {
                value = Double.parseDouble(source.getText().replace(',', '.'));
            } catch (NumberFormatException e) {
                return false;
            }
            return Double.isFinite(value) && value >= 0;
        }

        @Override
        public boolean shouldYieldFocus(JComponent input)
        {
            JFormattedTextField source = (JFormattedTextField)input;
            if (Main.DEBUG)
                System.out.println("ErrorFieldVeryfier::shouldYieldFocus (typ " + type + "): dostałem: " + source.getText());
            if (verify(input)) {   // Zwalidowano wejście - uaktualnij
                Series [] sel = labProject.getSelectedSeries();
                if (sel.length == 0)
                    return true;

                double value;
                if ("".equals(source.getText())) {
                    value = 0;
                    if (sel.length > 1) {     // Jeśli zaznaczono kilka i puste pole, ustaw wartość na null i pomiń
                        source.setValue(null);
                        return true;
                    }
                } else {
                    value = Double.parseDouble(source.getText().replace(',', '.'));
                }

                if (this.type == HUMAN_ERROR) {
                    for (Series s : sel) {
                        s.setHumanError(value);
                        s.updateMean();
                    }
                } else if (this.type == CALIBRATION_ERROR) {
                    for (Series s : sel) {
                        s.setCalibrationError(value);
                        s.updateMean();
                    }
                }
                return true;
            } else {        // Nieudana walidacja - mrugnij i bipnij
                Toolkit.getDefaultToolkit().beep();
                source.setBackground(Color.RED);
                new DelayedGUITask(QuickFrame.ERROR_BEEP_TIME, () -> source.setBackground(Color.WHITE));
                return false;
            }
        }
    }
}
