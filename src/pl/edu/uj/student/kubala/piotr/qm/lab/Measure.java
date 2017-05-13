// QuickMean - project.Measure.java
//---------------------------------------------------------------------
// Klasa reprezentująca pojedynczy pomiar i jego niepewności
//---------------------------------------------------------------------
// Utworzono 21:20 02.05.2017 w IntelliJ IDEA
// (C)PKua, wszystkie prawa zastrzeżone
//---------------------------------------------------------------------

package pl.edu.uj.student.kubala.piotr.qm.lab;

import pl.edu.uj.student.kubala.piotr.qm.Model;

public class Measure extends Model
{
    private double value;               // Wartość pomiaru
    private double calibrationError;    // Niepewność wzorcowania przyrządu pomiarowego
    private double humanError;          // Niepewność człowieka
    private double standardError;       // Odchylenie standardowe
    private Series parentSeries;        // Seria, do której pomiar należy

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
        this.parentSeries = null;
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
        this.value = value;
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
        this.calibrationError = calibrationError;
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
        this.humanError = humanError;
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
        this.standardError = standardError;
    }

    public Series getParentSeries() {
        return parentSeries;
    }

    public void setParentSeries(Series parentSeries) {
        this.parentSeries = parentSeries;
    }
}
