// QuickMean - project.Measure.java
//---------------------------------------------------------------------
// Klasa reprezentująca pojedynczy pomiar i jego niepewności
//---------------------------------------------------------------------
// Utworzono 21:20 02.05.2017 w IntelliJ IDEA
// (C)PKua, wszystkie prawa zastrzeżone
//---------------------------------------------------------------------

package pl.edu.uj.student.kubala.piotr.qm.lab;

public class Measure
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
     */
    public Measure(double value, double calibrationError, double humanError, double standardError)
    {
        this.value = value;
        this.calibrationError = calibrationError;
        this.humanError = humanError;
        this.standardError = standardError;
        this.parentSeries = null;
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

    public void setValue(double value) {
        this.value = value;
    }

    public double getCalibrationError() {
        return calibrationError;
    }

    public void setCalibrationError(double calibrationError) {
        this.calibrationError = calibrationError;
    }

    public double getHumanError() {
        return humanError;
    }

    public void setHumanError(double humanError) {
        this.humanError = humanError;
    }

    public double getStandardError() {
        return standardError;
    }

    public void setStandardError(double standardError) {
        this.standardError = standardError;
    }

    public Series getParentSeries() {
        return parentSeries;
    }

    public void setParentSeries(Series parentSeries) {
        this.parentSeries = parentSeries;
    }
}
