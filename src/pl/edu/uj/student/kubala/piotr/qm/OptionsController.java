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
import pl.edu.uj.student.kubala.piotr.qm.lab.SeriesGroup;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Arrays;

public class OptionsController implements Controller, ActionListener, ItemListener
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
    public void actionPerformed(ActionEvent e)
    {
        Object source = e.getSource();
        // Zignoruj ewentualny błąd - gdy nie jest podświetlona seria (powinna być)
        Series [] sel_series = this.labProject.getSelectedSeries();
        if (sel_series.length == 0)
            return;

        // Check box rozdzielania niepewności
        if (source == this.optionsPane.getSeparateErrorsCheckBox()) {
            for (Series s : sel_series) {
                s.setSeparateErrors(this.optionsPane.getSeparateErrorsCheckBox().isSelected());
                s.updateMean();
            }
        }
    }

    @Override
    public void setup() {
        // Nasłuchuj check boxa osobnych niepewności
        this.optionsPane.getSeparateErrorsCheckBox().addActionListener(this);
        // Nasłuchuj listy z ilością cyfr znacących
        this.optionsPane.getSignificantDigitsComboBox().addItemListener(this);
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
}
