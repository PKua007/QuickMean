// QuickMean - MeanDisplay.java
//---------------------------------------------------------------------
// Widok okienka z ładnie wyświetloną, dużą średnią.
//---------------------------------------------------------------------
// Utworzono 20:39 02.05.2017 w IntelliJ IDEA
// (C)PKua, wszystkie prawa zastrzeżone
//---------------------------------------------------------------------

package pl.edu.uj.student.kubala.piotr.qm;

import pl.edu.uj.student.kubala.piotr.qm.lab.*;
import pl.edu.uj.student.kubala.piotr.qm.utils.RoundedBorder;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Objects;

import static pl.edu.uj.student.kubala.piotr.qm.QuickFrame.BORDER_COLOR;
import static pl.edu.uj.student.kubala.piotr.qm.QuickFrame.TITLE_COLOR;
import static pl.edu.uj.student.kubala.piotr.qm.QuickFrame.BORDER_RADIUS;

public class MeanDisplay implements View, PropertyChangeListener
{
    private static final String     MEAN_OF_MEASURES = "X\u0305 ± σ(X\u0305) ± ΔX\u0305";
    private static final Font       MEAN_FONT = new Font("Dialog", Font.PLAIN, 20);
    private static final int        MEAN_MARGIN = 8;
    private static final int        TOP_MARGIN_BIAS = -6;
    private static final int        HEIGHT = 60;
    private static final Color      MEAN_COLOR = new Color(0x5A83BF);
    private static final String     EMPTY_HTML = "<html> </html>";

    private LabProject      labProject;
    private QuickFrame      parentFrame;
    private JPanel          panel;
    private JLabel          meanLabel;

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
        if (this.panel != null)
            throw new RuntimeException("MeanDisplay::init wywołane drugi raz");

        this.meanLabel = new JLabel(EMPTY_HTML, JLabel.CENTER);
        this.meanLabel.setFont(MEAN_FONT);
        this.meanLabel.setForeground(MEAN_COLOR);

        Border roundBorder = new RoundedBorder(BORDER_RADIUS, BORDER_COLOR);
        Border compoundBorder = BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(roundBorder, MEAN_OF_MEASURES, TitledBorder.CENTER, TitledBorder.TOP, null, TITLE_COLOR),
                BorderFactory.createEmptyBorder(MEAN_MARGIN + TOP_MARGIN_BIAS, MEAN_MARGIN, MEAN_MARGIN, MEAN_MARGIN)
        );

        this.panel = new JPanel(new BorderLayout());
        this.panel.setBorder(compoundBorder);
        this.panel.setPreferredSize(new Dimension(0, HEIGHT));
        this.meanLabel.setVerticalAlignment(SwingConstants.BOTTOM);
        this.panel.add(this.meanLabel, BorderLayout.CENTER);

        // Nasłuchuj projektu
        this.labProject.addPropertyChangeListener(this);
    }

    @Override
    public String getEDTInitializableName() {
        return "MeanDisplay";
    }

    /**
     * Zwraca panel z JLabelem ze średnią
     * @return panel z JLabelem ze średnią
     * @throws NullPointerException jeśli niezainicjowane
     */
    public JPanel getPanel() {
        return Objects.requireNonNull(this.panel);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt)
    {
        Series series = this.labProject.getHighlightedSeries();

        switch (evt.getPropertyName()) {
            // Zmiana liczby cyfr znaczących lub rozdzielania niepewności lub zmiana wartości średniej
            case Series.SEPARATE_ERRORS:
            case Series.SIGNIFICANT_DIGITS:
            case Series.MEAN_ERR:
                // Zignoruj, jeśli nie dotyczy wyświetlanej
                if (evt.getSource() != series)
                    return;

                // BEZ BREAK! MA SIĘ WYKONAĆ KOD AKTUALIZACJI ŚREDNIEJ

            // Zmiana podświetlonej serii (lub grupy)
            case LabProject.SELECTED_GROUP:
            case SeriesGroup.HIGHLIGHTED_SERIES:
                // Bądź przygotowany na brak podświetlenia
                if (series == null) {
                    this.meanLabel.setText(EMPTY_HTML);
                    return;
                }

                // Sformatuj średnią do wyświetlenia
                Quantity quantity = series.getMeanQuantity();
                FormattedMeasureFactory factory = new FormattedMeasureFactory();
                factory.setErrorSignificantDigits(series.getSignificantDigits());
                factory.setSeparateErrors(series.isSeparateErrors());
                FormattedMeasure formattedMeasure = factory.format(quantity);
                this.meanLabel.setText(formattedMeasure.toHTMLCompact());
                break;
        }
    }
}
