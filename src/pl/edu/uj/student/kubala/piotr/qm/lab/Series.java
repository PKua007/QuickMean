// QuickMean - project.Series.java
//---------------------------------------------------------------------
// Wewnętrzna reprezentacja pojedynczej serii pomiarów. Przechowuje
// Poszczególne pomiary, a także ma zapisane opcje serii.
//---------------------------------------------------------------------
// Utworzono 20:43 02.05.2017 w IntelliJ IDEA
// (C)PKua, wszystkie prawa zastrzeżone
//---------------------------------------------------------------------

package pl.edu.uj.student.kubala.piotr.qm.lab;

import java.util.ArrayList;
import java.util.Arrays;

public class Series
{
    public static final int DEFAULT_SIGNIFICANT_DIGITS = 2;

    private ArrayList<Measure>  measures;       // Tablica z pomiarami
    private double              mean;           // Średnia pomiarów
    private double              calculatedStandardError;    // Obliczona niepewność standarwoda
    private double              calculatedMaxError;         // Obliczony błąd maksymalny
    private int []              selectedMeasures;           // Zaznaczone pomiary
    private SeriesGroup         parentGroup;    // Grupa, do której należy seria pomiarowa

    private String          label;              // Nazwa serii pomiarowej (może być również wartością powiązanej zmiennej)
    private double          calibrationError;   // Domyślna niepewność wzorcowania dla całej serii
    private double          humanError;         // Błąd człowieka dla całej serii
    private boolean         useStudentFisher;   // Używaj współczynników Studenta-Fishera
    private int             significantDigits;  // Liczba cyfr znaczących niepewności
    private boolean         separateErrors;     // Czy rozdzielać niepwność

    /**
     * Konstruktor serii pomiarów. Inicjuje domyślne wartości dla serii.
     * @param parentGroup grupa, do której należy seria pomiarów
     * @param label nazwa serii
     */
    public Series(SeriesGroup parentGroup, String label)
    {
        this.parentGroup = parentGroup;
        this.label = label;

        this.measures = new ArrayList<>();
        this.selectedMeasures = new int[0];
        this.significantDigits = DEFAULT_SIGNIFICANT_DIGITS;
    }

    /* Settery i gettery */

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
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

    public boolean isUseStudentFisher() {
        return useStudentFisher;
    }

    public void setUseStudentFisher(boolean useStudentFisher) {
        this.useStudentFisher = useStudentFisher;
    }

    public int getSignificantDigits() {
        return significantDigits;
    }

    public void setSignificantDigits(int significantDigits) {
        this.significantDigits = significantDigits;
    }

    public boolean isSeparateErrors() {
        return separateErrors;
    }

    public void setSeparateErrors(boolean separateErrors) {
        this.separateErrors = separateErrors;
    }

    public int[] getSelectedMeasures() {
        return Arrays.copyOf(this.selectedMeasures, this.selectedMeasures.length);
    }

    public void setSelectedMeasures(int[] selectedMeasures) {
        this.selectedMeasures = Arrays.copyOf(selectedMeasures, selectedMeasures.length);
    }

    /* Gettery solo */

    public SeriesGroup getParentGroup() {
        return parentGroup;
    }

    public double getMean() {
        return mean;
    }

    public double getCalculatedStandardError() {
        return calculatedStandardError;
    }

    public double getCalculatedMaxError() {
        return calculatedMaxError;
    }

    /* Pozostałe metody */

    /**
     * Metoda oblicza średnią z przechowywanych pomiarów
     */
    public void calculateMean()
    {

    }

    /**
     * Metoda przelicza niepewności przechowywanych pomiarów
     */
    public void calculateErrors()
    {

    }

    /**
     * Metoda dodaje pomiar do listy pomiarów
     * @param measure pomiar do dodania
     * @param pos pozyzja, na której ma być dodany. -1, jeśli na końcu
     */
    public void addMeasure(Measure measure, int pos)
    {

    }

    /**
     * Metoda dodaje pomiar na końcu listy pomiarów
     * @param measure pomiar do dodania
     */
    public void addMeasure(Measure measure)
    {
        this.addMeasure(measure, -1);
    }

    /**
     * Metoda pobiera pomiar
     * @param pos pozycja pomiaru
     * @return pomiar z podanej pozycji
     */
    public Measure getMeasure(int pos)
    {
        return this.measures.get(pos);
    }

    /**
     * Metoda usuwa pomiar z listy po indeksie
     * @param pos pozycja pomiaru
     * @return liczba pomiarów pozostałych po usunięciu
     */
    public int deleteMeasure(int pos)
    {
        return 0;
    }

    /**
     * Metoda usuwa pomiar z listy
     * @param measure pomiar do usunięcia
     * @return liczba pomiarów pozostałych po usunięciu
     */
    public int deleteMeasure(Measure measure)
    {
        return 0;
    }

    /**
     * Metoda zwraca liczbę pomiarów w serii
     * @return liczba pomiarów w serii
     */
    public int getNumberOfMeasures()
    {
        return this.measures.size();
    }
}
