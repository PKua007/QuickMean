// QuickMean - project.Measure.java
//---------------------------------------------------------------------
// Klasa reprezentująca pojedynczy pomiar i jego niepewności
//---------------------------------------------------------------------
// Utworzono 21:20 02.05.2017 w IntelliJ IDEA
// (C)PKua, wszystkie prawa zastrzeżone
//---------------------------------------------------------------------

package pl.edu.uj.student.kubala.piotr.qm.lab;

import pl.edu.uj.student.kubala.piotr.qm.FormattedMeasure;
import pl.edu.uj.student.kubala.piotr.qm.FormattedMeasureFactory;
import pl.edu.uj.student.kubala.piotr.qm.Model;

import java.beans.PropertyChangeEvent;

public class Measure extends Model implements Cloneable
{
    /* Etykiety właściwości */
    public static final String  VALUE = "m.value";
    public static final String  CALIBRATION_ERROR = "m.calibrationError";
    public static final String  HUMAN_ERROR = "m.humanError";
    public static final String  STANDARD_ERROR = "m.standardError";

    private static final FormattedMeasureFactory factory = new FormattedMeasureFactory(true);

    private double value;               // Wartość pomiaru
    private double calibrationError;    // Niepewność wzorcowania przyrządu pomiarowego
    private double humanError;          // Niepewność człowieka
    private double standardError;       // Odchylenie standardowe

    /**
     * Konstruktor przyjmujący wszystkie parametry
     * @param value wartość pomiaru
     * @param calibrationError błąd wzorcowania
     * @param humanError bład człowieka
     * @param standardError odchylanie standardowe
     * @throws IllegalArgumentException jeśli którykolwiek z podanych błędów jest ujemny, nieokreślony lub nieskończony
     */
    public Measure(double value, double calibrationError, double humanError, double standardError)
    {
        checkValue(value);
        checkCalibrationError(calibrationError);
        checkHumanError(humanError);
        checkStandardError(standardError);

        this.value = value;
        this.calibrationError = calibrationError;
        this.humanError = humanError;
        this.standardError = standardError;
    }

    /* Pomocnicza metoda zrzucająca wyjątek, jeśli podana wartość jest zła */
    private void checkValue(double value)
    {
        if (!Double.isFinite(value))
            throw new IllegalArgumentException("wartość musi być skończona: " + value);
    }

    /* Pomocnicza metoda zrzucająca wyjątek, jeśli podany błąd jest zły */
    private void checkCalibrationError(double calibrationError)
    {
        if (!Double.isFinite(calibrationError) || calibrationError < 0)
            throw new IllegalArgumentException("błąd wzorcowania musi być nieujemny i skończony: " + calibrationError);
    }

    /* Pomocnicza metoda zrzucająca wyjątek, jeśli podany błąd jest zły */
    private void checkHumanError(double humanError)
    {
        if (!Double.isFinite(humanError) || humanError < 0)
            throw new IllegalArgumentException("błąd człowieka musi być nieujmeny i skończony: " + humanError);
    }

    /* Pomocnicza metoda zrzucająca wyjątek, jeśli podany błąd jest zły */
    private void checkStandardError(double standardError)
    {
        if (!Double.isFinite(standardError) || standardError < 0)
            throw new IllegalArgumentException("niepewność standardowa musi być nieujemna i skończona: " + standardError);
    }

    /**
     * Bezparametryczny konstruktor domyślny
     */
    public Measure()
    {
        this(0, 0, 0, 0);
    }

    /**
     * Konstruktor pomiaru bez niepewności
     * @param value wartość pomiaru
     */
    public Measure(double value)
    {
        this.value = value;
    }

    /* Gettery i settery parametrów */

    public double getValue() {
        return value;
    }

    /**
     * Ustawia wartość pomiaru na nową wartość.
     * @param value nowa wartość wartości pomiaru
     * @throws IllegalArgumentException jeśli wartość jest nieokreślona lub nieskończona
     */
    public void setValue(double value)
    {
        checkValue(value);
        double oldValue = this.value;
        this.value = value;
        PropertyChangeEvent evt = new PropertyChangeEvent(this, VALUE, oldValue, this.value);
        this.propertyFirer.firePropertyChange(evt);
    }

    public double getCalibrationError() {
        return calibrationError;
    }

    /**
     * Ustawia błąd wzorcowania na nową wartość.
     * @param calibrationError nowa wartość błędu wzorcowania
     * @throws IllegalArgumentException jeśli podany błąd jest ujemny, nieokreślony lub nieskończony
     */
    public void setCalibrationError(double calibrationError)
    {
        checkCalibrationError(calibrationError);
        double oldValue = this.calibrationError;
        this.calibrationError = calibrationError;
        PropertyChangeEvent evt = new PropertyChangeEvent(this, CALIBRATION_ERROR, oldValue, this.calibrationError);
        this.propertyFirer.firePropertyChange(evt);
    }

    public double getHumanError() {
        return humanError;
    }

    /**
     * Ustawia błąd człowieka na nową wartość.
     * @param humanError nowa wartość błędu wzorcowania
     * @throws IllegalArgumentException jeśli podany błąd jest ujemny, nieokreślony lub nieskończony
     */
    public void setHumanError(double humanError)
    {
        checkHumanError(humanError);
        double oldValue = this.humanError;
        this.humanError = humanError;
        PropertyChangeEvent evt = new PropertyChangeEvent(this, HUMAN_ERROR, oldValue, this.humanError);
        this.propertyFirer.firePropertyChange(evt);
    }

    public double getStandardError() {
        return standardError;
    }

    /**
     * Ustawia niepewność standardową na nową wartość.
     * @param standardError nowa wartość błędu wzorcowania
     * @throws IllegalArgumentException jeśli podana niepewność jest ujemna, nieokreślona lub nieskończona
     */
    public void setStandardError(double standardError)
    {
        checkStandardError(standardError);
        double oldValue = this.standardError;
        this.standardError = standardError;
        PropertyChangeEvent evt = new PropertyChangeEvent(this, STANDARD_ERROR, oldValue, this.standardError);
        this.propertyFirer.firePropertyChange(evt);
    }

    /**
     * Metoda kradnie dane z innego pomiaru
     * @param measure pomiar do okradzenia
     */
    public void swallowMeasure(Measure measure)
    {
        value = measure.value;
        calibrationError = measure.calibrationError;
        humanError = measure.humanError;
        standardError = measure.standardError;
    }

    @Override
    public Object clone()
    {
        Measure measure;
        try {
            measure = (Measure) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
        return measure;
    }

    @Override
    public String toString()
    {
        Quantity quantity = new Quantity(this.value, this.standardError, this.humanError + this.calibrationError);
        FormattedMeasure measure = factory.format(quantity);
        return "pomiar: " + measure.toString();
    }
}
