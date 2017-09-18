// QuickMean - project.Series.java
//---------------------------------------------------------------------
// Wewnętrzna reprezentacja pojedynczej serii pomiarów. Przechowuje
// Poszczególne pomiary, a także ma zapisane opcje serii.
//---------------------------------------------------------------------
// Utworzono 20:43 02.05.2017 w IntelliJ IDEA
// (C)PKua, wszystkie prawa zastrzeżone
//---------------------------------------------------------------------

package pl.edu.uj.student.kubala.piotr.qm.lab;

import pl.edu.uj.student.kubala.piotr.qm.FormattedMeasure;
import pl.edu.uj.student.kubala.piotr.qm.FormattedMeasureFactory;
import pl.edu.uj.student.kubala.piotr.qm.PropagatingListModel;
import pl.edu.uj.student.kubala.piotr.qm.utils.Utils;

import java.beans.PropertyChangeEvent;
import java.util.*;
import java.util.stream.Collectors;

public class Series extends PropagatingListModel<Measure>
{
    public static final int DEFAULT_SIGNIFICANT_DIGITS = 2;
    public static final int MIN_SIGNIFICANT_DIGITS = 1;
    public static final int MAX_SIGNIFICANT_DIGITS = 6;

    /* Etykiety właściwości */
    private static final String PREFIX = "s";

    public static final String  LABEL               = PREFIX + ".label";
    public static final String  CALIBRATION_ERROR   = PREFIX + ".calibrationError";
    public static final String  HUMAN_ERROR         = PREFIX + ".humanError";
    public static final String  USE_STUDENT_FISHER  = PREFIX + ".useStudentFisher";
    public static final String  SIGNIFICANT_DIGITS  = PREFIX + ".significantDigits";
    public static final String  SEPARATE_ERRORS     = PREFIX + ".separateErrors";
    public static final String  SELECTED_MEASURES   = PREFIX + ".selectedMeasures";
    public static final String  MEAN_ERR            = PREFIX + ".mean_err";
    public static final String  NEW_MEASURE         = PREFIX + "." + NEW;
    public static final String  DEL_MEASURE         = PREFIX + "." + DEL;

    private static int staticIdx = 0;
    private static final FormattedMeasureFactory factory = new FormattedMeasureFactory();

    private Quantity            meanQuantity;   // Średnia wyliczona wielkość
    private ArrayList<Integer>  selectedMeasures;           // Zaznaczone pomiary

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
        this.setPrefix(PREFIX);

        this.selectedMeasures = new ArrayList<>();
        this.meanQuantity = new Quantity();
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
        PropertyChangeEvent evt = new PropertyChangeEvent(this, LABEL, oldValue, this.label);
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
        PropertyChangeEvent evt = new PropertyChangeEvent(this, CALIBRATION_ERROR, oldValue, this.calibrationError);
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
        PropertyChangeEvent evt = new PropertyChangeEvent(this, HUMAN_ERROR, oldValue, this.humanError);
        this.propertyFirer.firePropertyChange(evt);
    }

    public boolean isUseStudentFisher() {
        return useStudentFisher;
    }

    public void setUseStudentFisher(boolean useStudentFisher)
    {
        boolean oldValue = this.useStudentFisher;
        this.useStudentFisher = useStudentFisher;
        PropertyChangeEvent evt = new PropertyChangeEvent(this, USE_STUDENT_FISHER, oldValue, this.useStudentFisher);
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
        PropertyChangeEvent evt = new PropertyChangeEvent(this, SIGNIFICANT_DIGITS, oldValue, this.significantDigits);
        this.propertyFirer.firePropertyChange(evt);
    }

    public boolean isSeparateErrors() {
        return separateErrors;
    }

    public void setSeparateErrors(boolean separateErrors)
    {
        boolean oldValue = this.separateErrors;
        this.separateErrors = separateErrors;
        PropertyChangeEvent evt = new PropertyChangeEvent(this, SEPARATE_ERRORS, oldValue, this.separateErrors);
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
        Arrays.stream(selectedMeasures).forEach(this.children::get);        // Sprawdź poprawność indeksów
        ArrayList<Measure> oldValue = this.selectedMeasures.stream()
                .map((i) -> this.children.get(i))
                .collect(Collectors.toCollection(ArrayList::new));
        ArrayList<Measure> newValue = Arrays.stream(selectedMeasures).
                mapToObj((i) -> this.children.get(i))
                .collect(Collectors.toCollection(ArrayList::new));
        this.selectedMeasures = Arrays.stream(selectedMeasures)
                .boxed()
                .collect(Collectors.toCollection(ArrayList::new));

        PropertyChangeEvent evt = new PropertyChangeEvent(this, SELECTED_MEASURES, oldValue, newValue);
        propertyFirer.firePropertyChange(evt);
    }

    /* Gettery solo */

    public Quantity getMeanQuantity()
    {
        return meanQuantity;
    }

    /* Pozostałe metody */

    /**
     * Metoda aktualizuje średnią z przechowywanych pomiarów i ich niepewności. Wywołuje zdarzenie mean_err dla
     * zarejestrowanych obserwatorów
     */
    public void updateMean()
    {
        // Stare wartości
        Quantity oldValue = this.meanQuantity;
        double mean, standard, max;

        switch (this.children.size()) {
            // 0 pomiarów, brak wartości
            case 0:
                mean = 0;
                standard = 0;
                max = 0;
                break;

            // 1 pomiar, przepisz wartości z pojedynczego pomiaru
            case 1:
                Measure measure = this.children.get(0);
                mean = measure.getValue();

                if (separateErrors) {   // Rozdzielanie niepewności wg starej konwencji
                    max =
                            defClibration(measure) +
                            defHuman(measure);
                    standard = measure.getStandardError();
                } else {                // Sumowanie niepewności wg nowej konwencji
                    max = 0;
                    standard = Math.sqrt(
                            Math.pow(measure.getStandardError(), 2) +
                            Math.pow(defClibration(measure), 2) / 3 +
                            Math.pow(defHuman(measure), 2) / 3);
                }
                break;

            // 2 lub więcej pomiarów - trzeba już się pomęczyć z przeliczeniem ;)
            default:
                mean = this.children.stream()                  // Średnia pomiarów
                        .mapToDouble(Measure::getValue)
                        .average()
                        .orElse(0);
                double diffSquareSum = this.children.stream()       // Suma kwadratów odchyleń od średniej
                        .mapToDouble(Measure::getValue)
                        .reduce(0, (sum, m) -> sum + Math.pow(mean - m, 2));
                double measureStdDev = Math.sqrt(                   // Odchylenie standardowe pojedynczego pomiaru
                        diffSquareSum / (this.children.size() - 1)) *
                        (useStudentFisher ? StudentFisherCache.get(this.children.size() - 1) : 1);
                double stdErrSquareSum = this.children.stream()     // Suma kwardatów niepewności standardowych
                        .mapToDouble((m) ->
                                defStandard(m, measureStdDev))
                        .reduce(0, (sum, m) ->  sum + m * m);

                if (separateErrors) {   // Rozdzielanie niepewności wg starej konwencji
                    max = this.children.stream()
                            .mapToDouble((m) ->
                                    defHuman(m) +
                                    defClibration(m))
                            .average()
                            .orElse(0);
                    standard = Math.sqrt(stdErrSquareSum) / this.children.size();
                } else {                // Sumowanie niepewności wg nowej konwencji
                    double meanCalibration = this.children.stream()     // Średni błąd wzorcowania
                            .mapToDouble(this::defClibration)
                            .average()
                            .orElse(0);
                    double meanHumanError = this.children.stream()      // Średni błąd człowieka
                            .mapToDouble(this::defHuman)
                            .average()
                            .orElse(0);

                    max = 0;
                    standard = Math.sqrt(
                            stdErrSquareSum / Math.pow(this.children.size(), 2) +
                            Math.pow(meanCalibration, 2) / 3 +
                            Math.pow(meanHumanError, 2) / 3);
                }
                break;
        }

        this.meanQuantity = new Quantity(mean, standard, max);
        // Odpal wiadomość o zmianie średniej i niepewności dla listenerów
        this.propertyFirer.firePropertyChange(new PropertyChangeEvent(this, MEAN_ERR, oldValue, this.meanQuantity));
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
     * @param element pomiar do dodania
     * @param index pozyzja, na której ma być dodany. -1, jeśli na końcu
     * @throws NullPointerException jeśli measure == null
     * @throws IndexOutOfBoundsException jeśli index jest poza [-1, {@link Series#getNumberOfElements()}]
     * @throws IllegalArgumentException jeśli pomiar już jest w serii
     */
    @Override
    public int addElement(Measure element, int index)
    {
        this.validateNotNull(element);
        this.validateAddIdx(index);
        Utils.shiftIndicesAfterAddition(index, this.selectedMeasures);
        return super.addElement(element, index);
    }

    /**
     * Metoda usuwa pomiar z listy po indeksie i ustawia w nim rodzica na null.
     * @param index pozycja pomiaru
     * @return liczba pomiarów pozostałych po usunięciu
     * @throws IndexOutOfBoundsException jeśli element pod wskazanym indeksem nie istnieje
     */
    @Override
    public int deleteElement(int index)
    {
        this.validateIdx(index);
        Utils.removeElementFromIndicesList(index, this.selectedMeasures);
        return super.deleteElement(index);
    }

    @Override
    public String toString()
    {
        FormattedMeasure measure = factory.format(this.meanQuantity);
        return "seria \"" + this.label + "\": " + measure.toString();
    }
}
