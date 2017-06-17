// QuickMean - project.SeriesGroup.java
//---------------------------------------------------------------------
// Wewnętrzna reprezentacja grupy serii pomiarów. Zawieta tablicę
// z poszczególnymi seriami i dane grupy
//---------------------------------------------------------------------
// Utworzono 20:43 02.05.2017 w IntelliJ IDEA
// (C)PKua, wszystkie prawa zastrzeżone
//---------------------------------------------------------------------

package pl.edu.uj.student.kubala.piotr.qm.lab;

import pl.edu.uj.student.kubala.piotr.qm.utils.Utils;

import java.beans.PropertyChangeEvent;
import java.util.*;
import java.util.stream.Collectors;

public class SeriesGroup extends PropagatingListModel<Series>
{
    public static final String  DEFAULT_LABEL_HEADER = "Nazwa serii";

    /* Etykiety zdarzeń */
    private static final String PREFIX = "sg";
    
    public static final String  NAME                = PREFIX + ".name";
    public static final String  LABEL_HEADER        = PREFIX + ".labelHeader";
    public static final String  SELECTED_SERIES     = PREFIX + ".selectedSeries";
    public static final String  HIGHLIGHTED_SERIES  = PREFIX + ".highlightedSeries";
    public static final String  SELECTING_NOW       = PREFIX + ".selectingNow";
    public static final String  NEW_SERIES          = PREFIX + "." + NEW;
    public static final String  DEL_SERIES          = PREFIX + "." + NEW;
    
    private static int staticIdx = 0;
    
    private ArrayList<Integer>  selectedSeries;     // Indeksy w tablicy zaznaczonych serii grup
    private int                 highlightedSeries;  // Indeks podświetlonej serii ("bieżącej")
    private LabProject          parentLab;          // Laboratorium, do którego należy grupa pomiarów
    private boolean             selectingNow;       // Czy obecnie trwa zaznaczanie serii?

    private String          name;               // Nazwa serii pomiarów
    private String          labelHeader;        // Nagłówek w tabeli przy etykietach serii

    /**
     * Konstruktor grupy inicjujący domyślne wartości
     * @param name nazwa grupy pomiarów
     */
    public SeriesGroup(String name)
    {
        this.setPrefix(PREFIX);

        this.name = Objects.requireNonNull(name);

        this.selectedSeries = new ArrayList<>();
        this.highlightedSeries = -1;
        this.labelHeader = DEFAULT_LABEL_HEADER;
    }

    public SeriesGroup()
    {
        this("grupa " + (++staticIdx));
    }

    /* Gettery i settery */

    public String getName() {
        return name;
    }

    public void setName(String name)
    {
        String oldValue = this.name;
        this.name = Objects.requireNonNull(name);
        PropertyChangeEvent evt = new PropertyChangeEvent(this, NAME, oldValue, this.name);
        this.propertyFirer.firePropertyChange(evt);
    }

    public String getLabelHeader() {
        return labelHeader;
    }

    public void setLabelHeader(String labelHeader)
    {
        String oldValue = this.labelHeader;
        this.labelHeader = Objects.requireNonNull(labelHeader);
        PropertyChangeEvent evt = new PropertyChangeEvent(this, LABEL_HEADER, oldValue, this.labelHeader);
        this.propertyFirer.firePropertyChange(evt);
    }

    public int[] getSelectedSeries() {
        return selectedSeries.stream()
                .mapToInt(Integer::intValue)
                .toArray();
    }

    /**
     * Ustawia nową listę indeksów wybranych serii pomiarów. Powiadamia listenerów wysyłając starą i nową listę REFERENCJI
     * do zaznaczonych serii
     * @param selectedSeries nowe indeksy zaznaczonych serii
     * @throws IndexOutOfBoundsException jeśli któryś z indeksów jest niepoprawny
     * @throws NullPointerException jeśli {@code selectedMeasures == null}
     */
    public void setSelectedSeries(int[] selectedSeries)
    {
        Arrays.stream(selectedSeries).forEach(this.children::get);        // Sprawdź poprawność indeksów
        Series [] oldSelected = this.selectedSeries.stream()
                .map((i) -> this.children.get(i)).
                        toArray(Series[]::new);
        Series [] newSelected = Arrays.stream(selectedSeries).
                mapToObj((i) -> this.children.get(i)).
                toArray(Series[]::new);
        this.selectedSeries = Arrays.stream(selectedSeries)
                .boxed()
                .collect(Collectors.toCollection(ArrayList::new));

        PropertyChangeEvent evt = new PropertyChangeEvent(this, SELECTED_SERIES, oldSelected, newSelected);
        this.propertyFirer.firePropertyChange(evt);
    }

    public LabProject getParentLab() {
        return parentLab;
    }

    public void setParentLab(LabProject parentLab) {
        this.parentLab = Objects.requireNonNull(parentLab);
    }

    public int getHighlightedSeriesIdx() {
        return highlightedSeries;
    }

    public void setHighlightedSeries(int highlightedSeries) {
        // Sprawdź poprawność indeksu
        if (highlightedSeries != -1)
            this.children.get(highlightedSeries);
        int oldValue = this.highlightedSeries;
        int newValue = highlightedSeries;
        this.highlightedSeries = highlightedSeries;
        PropertyChangeEvent evt = new PropertyChangeEvent(this, HIGHLIGHTED_SERIES, oldValue, newValue);
        this.propertyFirer.firePropertyChange(evt);
    }

    /* Pozostałe metody */

    /**
     * Metoda dodaje serię do listy
     * @param series seria do dodania
     * @param index pozyzja, na której ma być dodana. -1, jeśli na końcu
     * @throws NullPointerException jeśli series == null
     * @throws IndexOutOfBoundsException, jeśli pos jest poza [0, {@link SeriesGroup#getNumberOfSeries() - 1}]
     * @throws IllegalArgumentException jeśli seria jest już w grupie
     */
    public void addSeries(Series series, int index)
    {
        Utils.shiftIndicesAfterAddition(index, this.selectedSeries);
        if (index != -1 && this.highlightedSeries >= index)
            this.highlightedSeries++;
        this.addChild(series, index);
    }

    /**
     * Metoda dodaje serię na końcu listy
     * @param series seria do dodania
     * @throws NullPointerException jeśli series == null
     * @throws IllegalArgumentException jeśli seria jest już w grupie
     */
    public void addSeries(Series series)
    {
        this.addSeries(series, -1);
    }

    /**
     * Metoda pobiera serię
     * @param pos pozycja serii na liście
     * @throws IndexOutOfBoundsException jeśli element pod wskazanym indeksem nie istnieje
     * @return seria z podanej pozycji
     */
    public Series getSeries(int pos)
    {
        return this.getChild(pos);
    }

    /**
     * Metoda usuwa serię z listy po indeksie. Jeśli usuwana seria jest właśnie podświetlona, ustawia podświetlenie na
     * -1 i wyzwalane jest zdarzenia zmiany podświetlenia
     * @param pos pozycja serii
     * @throws IndexOutOfBoundsException jeśli element pod wskazanym indeksem nie istnieje
     * @return liczba serii pozostałych po usunięciu
     */
    public int deleteSeries(int pos)
    {
        Utils.removeElementFromIndicesList(pos, this.selectedSeries);
        if (this.highlightedSeries == pos)
            this.setHighlightedSeries(-1);
        else if (this.highlightedSeries > pos)
            this.highlightedSeries--;
        return this.deleteChild(pos);
    }

    /**
     * Metoda usuwa serię z listy. Jeśli usuwana seria jest właśnie podświetlona, ustawia podświetlenie na
     * -1 i wyzwalane jest zdarzenia zmiany podświetlenia
     * @param series seria do usunięcia
     * @return liczba serii pozostałych po usunięciu
     */
    public int deleteSeries(Series series)
    {
        int index = this.children.indexOf(series);
        if (index != -1)
            this.deleteSeries(index);
        return this.children.size();
    }

    /**
     * Metoda zwraca liczbę serii w grupie
     * @return liczba serii w grupie
     */
    public int getNumberOfSeries()
    {
        return this.getNumberOfChildren();
    }

    /**
     * Metoda zwraca indeks podanej serii, lub -1, jeśli nie znaleziono
     * @param series poszukiwana seria
     * @return indeks podanej serii, lub -1, jeśli nie znaleziono
     */
    public int getSeriesIdx(Series series)
    {
        return this.getChildIdx(series);
    }

    /**
     * Zwraca true, jeśli obecnie trwa zaznaczanie pomiarów (myszka nie jest jeszcze puszczona
     * @return {@code true}, jeśli trwa zaznaczanie, {@code false} w przeciwnym wypadku
     */
    public boolean isSelectingNow() {
        return selectingNow;
    }

    /**
     * Ustawia informację, czy trwa zaznaczanie (myszka nie jest jeszcze puszczona)
     * @param selectingNow czy trwa zaznaczanie?
     */
    public void setSelectingNow(boolean selectingNow) {
        boolean oldValue = this.selectingNow;
        this.selectingNow = selectingNow;
        PropertyChangeEvent evt = new PropertyChangeEvent(this, SELECTING_NOW, oldValue, selectingNow);
        this.propertyFirer.firePropertyChange(evt);
    }
}
