// QuickMean - project.Series.java
//---------------------------------------------------------------------
// Wewnętrzna reprezentacja pojedynczej serii pomiarów. Przechowuje
// Poszczególne pomiary, a także ma zapisane opcje serii.
//---------------------------------------------------------------------
// Utworzono 20:43 02.05.2017 w IntelliJ IDEA
// (C)PKua, wszystkie prawa zastrzeżone
//---------------------------------------------------------------------

package pl.edu.uj.student.kubala.piotr.qm.lab;

import pl.edu.uj.student.kubala.piotr.qm.utils.Utils;

import java.beans.PropertyChangeEvent;
import java.util.*;
import java.util.stream.Collectors;

public class Series extends PropagatingModel
{
    public static final int DEFAULT_SIGNIFICANT_DIGITS = 2;
    public static final int MIN_SIGNIFICANT_DIGITS = 1;
    public static final int MAX_SIGNIFICANT_DIGITS = 6;

    private static int staticIdx = 0;

    private ArrayList<Measure>  measures;       // Tablica z pomiarami
    private double              mean;           // Średnia pomiarów
    private double              calculatedStandardError;    // Obliczona niepewność standarwoda
    private double              calculatedMaxError;         // Obliczony błąd maksymalny
    private ArrayList<Integer>  selectedMeasures;           // Zaznaczone pomiary
    private SeriesGroup         parentGroup;    // Grupa, do której należy seria pomiarowa

    private String          label;              // Nazwa serii pomiarowej (może być również wartością powiązanej zmiennej)
    private double          calibrationError;   // Domyślna niepewność wzorcowania dla całej serii
    private double          humanError;         // Błąd człowieka dla całej serii
    private boolean         useStudentFisher;   // Używaj współczynników Studenta-Fishera
    private int             significantDigits;  // Liczba cyfr znaczących niepewności
    private boolean         separateErrors;     // Czy rozdzielać niepwność

    /**
     * Konstruktor serii pomiarów. Inicjuje domyślne wartości dla serii
     * @param label nazwa serii
     */
    public Series(String label)
    {
        this.label = Objects.requireNonNull(label);

        this.measures = new ArrayList<>();
        this.selectedMeasures = new ArrayList<>();
        this.significantDigits = DEFAULT_SIGNIFICANT_DIGITS;
    }

    /**
     * Konstruktor domyślny serii pomiarów. Tworzy serie o nazwach "seria x", gdzie x to kolejne liczby dodatnie
     */
    public Series()
    {
        this("seria " + (++staticIdx));
    }

    /* Settery i gettery */

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        String oldValue = this.label;
        this.label = Objects.requireNonNull(label);
        PropertyChangeEvent evt = new PropertyChangeEvent(this, "series.label", oldValue, this.label);
        this.propertyFirer.firePropertyChange(evt);
    }

    public double getCalibrationError() {
        return calibrationError;
    }

    /**
     * Ustawia błąd wzorcowania dotyczący całej serii - nim podstawiane są nieokreślone wartości dla poszczególnych
     * pomiarów
     * @param calibrationError błąd wzorcowania w całej serii
     * @throws IllegalArgumentException jeśli podany błąd jest ujemny, nieokreślony lub nieskończony
     */
    public void setCalibrationError(double calibrationError)
    {
        if (!Double.isFinite(calibrationError) || calibrationError < 0)
            throw new IllegalArgumentException("błąd wzorcowania musi być nieujemny i skończony: " + calibrationError);

        double oldValue = this.calibrationError;
        this.calibrationError = calibrationError;
        PropertyChangeEvent evt = new PropertyChangeEvent(this, "series.calibrationError", oldValue, this.calibrationError);
        this.propertyFirer.firePropertyChange(evt);
    }

    public double getHumanError() {
        return humanError;
    }

    /**
     * Ustawia błąd człowieka dotyczący całej serii - nim podstawiane są nieokreślone wartości dla poszczególnych
     * pomiarów
     * @param humanError błąd człowieka w całej serii
     * @throws IllegalArgumentException jeśli podany błąd jest ujemny, nieokreślony lub nieskończony
     */
    public void setHumanError(double humanError)
    {
        if (!Double.isFinite(humanError) || humanError < 0)
            throw new IllegalArgumentException("błąd człowieka musi być nieujemny i skończony: " + humanError);

        double oldValue = this.humanError;
        this.humanError = humanError;
        PropertyChangeEvent evt = new PropertyChangeEvent(this, "series.humanError", oldValue, this.humanError);
        this.propertyFirer.firePropertyChange(evt);
    }

    public boolean isUseStudentFisher() {
        return useStudentFisher;
    }

    public void setUseStudentFisher(boolean useStudentFisher)
    {
        boolean oldValue = this.useStudentFisher;
        this.useStudentFisher = useStudentFisher;
        PropertyChangeEvent evt = new PropertyChangeEvent(this, "series.useStudentFisher", oldValue, this.useStudentFisher);
        this.propertyFirer.firePropertyChange(evt);
    }

    public int getSignificantDigits() {
        return significantDigits;
    }

    public void setSignificantDigits(int significantDigits)
    {
        if (significantDigits < MIN_SIGNIFICANT_DIGITS || significantDigits > MAX_SIGNIFICANT_DIGITS)
            throw new IllegalArgumentException("Liczba cyfr znaczących musi być z przedziału [" + MIN_SIGNIFICANT_DIGITS +
                    ", " + MAX_SIGNIFICANT_DIGITS + "]: " + significantDigits);

        int oldValue = this.significantDigits;
        this.significantDigits = significantDigits;
        PropertyChangeEvent evt = new PropertyChangeEvent(this, "series.significantDigits", oldValue, this.significantDigits);
        this.propertyFirer.firePropertyChange(evt);
    }

    public boolean isSeparateErrors() {
        return separateErrors;
    }

    public void setSeparateErrors(boolean separateErrors)
    {
        boolean oldValue = this.separateErrors;
        this.separateErrors = separateErrors;
        PropertyChangeEvent evt = new PropertyChangeEvent(this, "series.separateErrors", oldValue, this.separateErrors);
        this.propertyFirer.firePropertyChange(evt);
    }

    public int[] getSelectedMeasures()
    {
        return this.selectedMeasures.stream()
                .mapToInt(Integer::intValue)
                .toArray();
    }

    /**
     * Ustawia nową listę indeksów wybranych pomiarów. Powiadamia listenerów wysyłając starą i nową listę REFERENCJI
     * do zaznaczonych pomiarów
     * @param selectedMeasures nowe indeksy zaznaczonych pomiarów
     * @throws IndexOutOfBoundsException jeśli któryś z indeksów jest niepoprawny
     * @throws NullPointerException jeśli {@code selectedMeasures == null}
     */
    public void setSelectedMeasures(int[] selectedMeasures)
    {
        Arrays.stream(selectedMeasures).forEach(this.measures::get);        // Sprawdź poprawność indeksów
        Measure [] oldValue = this.selectedMeasures.stream()
                .map((i) -> this.measures.get(i))
                .toArray(Measure[]::new);
        Measure [] newValue = Arrays.stream(selectedMeasures).
                mapToObj((i) -> this.measures.get(i))
                .toArray(Measure[]::new);
        this.selectedMeasures = Arrays.stream(selectedMeasures)
                .boxed()
                .collect(Collectors.toCollection(ArrayList::new));

        PropertyChangeEvent evt = new PropertyChangeEvent(this, "series.selectedMeasures", oldValue, newValue);
        propertyFirer.firePropertyChange(evt);
    }

    public SeriesGroup getParentGroup() {
        return parentGroup;
    }

    public void setParentGroup(SeriesGroup parentGroup) {
        this.parentGroup = parentGroup;
    }

    /* Gettery solo */

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
     * Metoda aktualizuje średnią z przechowywanych pomiarów i ich niepewności. Wywołuje zdarzenie mean_err dla
     * zarejestrowanych obserwatorów
     */
    public void updateMean()
    {
        // Stare wartości
        double [] oldValues = new double[]{ this.mean, this.calculatedStandardError, this.calculatedMaxError };

        switch (this.measures.size()) {
            // 0 pomiarów, brak wartości
            case 0:
                this.mean = 0;
                this.calculatedStandardError = 0;
                this.calculatedMaxError = 0;
                break;

            // 1 pomiar, przepisz wartości z pojedynczego pomiaru
            case 1:
                Measure measure = this.measures.get(0);
                this.mean = measure.getValue();

                if (separateErrors) {   // Rozdzielanie niepewności wg starej konwencji
                    this.calculatedMaxError =
                            defClibration(measure) +
                            defHuman(measure);
                    this.calculatedStandardError = measure.getStandardError();
                } else {                // Sumowanie niepewności wg nowej konwencji
                    this.calculatedMaxError = 0;
                    this.calculatedStandardError = Math.sqrt(
                            Math.pow(measure.getStandardError(), 2) +
                            Math.pow(defClibration(measure), 2) / 3 +
                            Math.pow(defHuman(measure), 2) / 3);
                }
                break;

            // 2 lub więcej pomiarów - trzeba już się pomęczyć z przeliczeniem ;)
            default:
                this.mean = this.measures.stream()                  // Średnia pomiarów
                        .mapToDouble(Measure::getValue)
                        .average()
                        .orElse(0);
                double diffSquareSum = this.measures.stream()       // Suma kwadratów odchyleń od średniej
                        .mapToDouble(Measure::getValue)
                        .reduce(0, (sum, m) -> sum + Math.pow(this.mean - m, 2));
                double measureStdDev = Math.sqrt(                   // Odchylenie standardowe pojedynczego pomiaru
                        diffSquareSum / (this.measures.size() - 1)) *
                        (useStudentFisher ? StudentFisherCache.get(this.measures.size() - 1) : 1);
                double stdErrSquareSum = this.measures.stream()     // Suma kwardatów niepewności standardowych
                        .mapToDouble((m) ->
                                defStandard(m, measureStdDev))
                        .reduce(0, (sum, m) ->  sum + m * m);

                if (separateErrors) {   // Rozdzielanie niepewności wg starej konwencji
                    this.calculatedMaxError = this.measures.stream()
                            .mapToDouble((m) ->
                                    defHuman(m) +
                                    defClibration(m))
                            .average()
                            .orElse(0);
                    this.calculatedStandardError = Math.sqrt(stdErrSquareSum) / this.measures.size();
                } else {                // Sumowanie niepewności wg nowej konwencji
                    double meanCalibration = this.measures.stream()     // Średni błąd wzorcowania
                            .mapToDouble(this::defClibration)
                            .average()
                            .orElse(0);
                    double meanHumanError = this.measures.stream()      // Średni błąd człowieka
                            .mapToDouble(this::defHuman)
                            .average()
                            .orElse(0);

                    this.calculatedMaxError = 0;
                    this.calculatedStandardError = Math.sqrt(
                            stdErrSquareSum / Math.pow(this.measures.size(), 2) +
                            Math.pow(meanCalibration, 2) / 3 +
                            Math.pow(meanHumanError, 2) / 3);
                }
                break;
        }

        // Nowe wartości
        double [] newValues = new double[]{ this.mean, this.calculatedStandardError, this.calculatedMaxError };
        // Odpal wiadomość o zmianie średniej i niepewności dla listenerów
        this.propertyFirer.firePropertyChange(new PropertyChangeEvent(this, "series.mean_err", oldValues, newValues));
    }

    /* Pomocnicza metoda - zwraca measure.getStandardError(), jeśli niezerowy, albo domyślny def, jeśli zerowy */
    private double defStandard(Measure measure, double def)
    {
        return measure.getStandardError() == 0 ? def : measure.getStandardError();
    }

    /* Pomocnicza metoda - zwraca measure.getCalibrationError(), jeśli niezerowy, albo domyślny this.calibrationError, jeśli zerowy */
    private double defClibration(Measure measure)
    {
        return measure.getCalibrationError() == 0 ? this.calibrationError : measure.getCalibrationError();
    }

    /* Pomocnicza metoda - zwraca measure.gethumanError(), jeśli niezerowy, albo domyślny this.humanError, jeśli zerowy */
    private double defHuman(Measure measure)
    {
        return measure.getHumanError() == 0 ? this.humanError : measure.getHumanError();
    }

    /**
     * Metoda dodaje pomiar do listy pomiarów i ustawia w nim rodzica na this. Niedozwolona wartość null.
     * @param measure pomiar do dodania
     * @param index pozyzja, na której ma być dodany. -1, jeśli na końcu
     * @throws NullPointerException jeśli measure == null
     * @throws IndexOutOfBoundsException jeśli index jest poza [-1, {@link Series#getNumberOfMeasures()}]
     * @throws IllegalArgumentException jeśli pomiar już jest w serii
     */
    public void addMeasure(Measure measure, int index)
    {
        Objects.requireNonNull(measure);
        if (this.measures.indexOf(measure) != -1) {
            throw new IllegalArgumentException("Pomiar już jest w serii");
        } if (index == -1) {
            this.measures.add(measure);
        } else {
            this.measures.add(index, measure);
            Utils.shiftIndicesAfterAddition(index, this.selectedMeasures);
        }

        measure.setParentSeries(this);
        measure.addPropertyChangeListener(this);
        PropertyChangeEvent evt = new PropertyChangeEvent(this, "series.new_measure", null, measure);
        this.propertyFirer.firePropertyChange(evt);
    }

    /**
     * Metoda dodaje pomiar na końcu listy pomiarów i ustawia w nim rodzica na this. Niedozwolona wartość null.
     * @param measure pomiar do dodania
     * @throws NullPointerException jeśli measure == null
     * @throws IllegalArgumentException jeśli pomiar już jest w serii
     */
    public void addMeasure(Measure measure)
    {
        this.addMeasure(measure, -1);
    }

    /**
     * Metoda pobiera pomiar
     * @param pos pozycja pomiaru
     * @throws IndexOutOfBoundsException, jeśli pos jest poza [0, {@link Series#getNumberOfMeasures() - 1}]
     * @return pomiar z podanej pozycji
     */
    public Measure getMeasure(int pos)
    {
        return this.measures.get(pos);
    }

    /**
     * Metoda usuwa pomiar z listy po indeksie i ustawia w nim rodzica na null.
     * @param pos pozycja pomiaru
     * @return liczba pomiarów pozostałych po usunięciu
     * @throws IndexOutOfBoundsException jeśli element pod wskazanym indeksem nie istnieje
     */
    public int deleteMeasure(int pos)
    {
        Measure measure = this.measures.get(pos);
        this.measures.remove(pos);
        measure.setParentSeries(null);
        measure.removePropertyChangeListener(this);
        Utils.removeElementFromIndicesList(pos, this.selectedMeasures);
        PropertyChangeEvent evt = new PropertyChangeEvent(this, "series.del_measure", measure, null);
        this.propertyFirer.firePropertyChange(evt);
        return this.measures.size();
    }

    /**
     * Metoda usuwa pomiar z listy i ustawia w nim rodzica na null
     * @param measure pomiar do usunięcia
     * @return liczba pomiarów pozostałych po usunięciu
     */
    public int deleteMeasure(Measure measure)
    {
        int index = this.measures.indexOf(measure);
        if (index != -1) {
            this.measures.remove(index);
            measure.setParentSeries(null);
            measure.removePropertyChangeListener(this);
            Utils.removeElementFromIndicesList(index, this.selectedMeasures);
            PropertyChangeEvent evt = new PropertyChangeEvent(this, "series.del_measure", measure, null);
            this.propertyFirer.firePropertyChange(evt);
        }
        return this.measures.size();
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
