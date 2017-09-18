// QuickMean - project.SeriesGroup.java
//---------------------------------------------------------------------
// Wewnętrzna reprezentacja grupy serii pomiarów. Zawieta tablicę
// z poszczególnymi seriami i dane grupy
//---------------------------------------------------------------------
// Utworzono 20:43 02.05.2017 w IntelliJ IDEA
// (C)PKua, wszystkie prawa zastrzeżone
//---------------------------------------------------------------------

package pl.edu.uj.student.kubala.piotr.qm.lab;

import pl.edu.uj.student.kubala.piotr.qm.PropagatingListModel;
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
    private boolean             selectingNow;       // Czy obecnie trwa zaznaczanie serii?

    private String          name;               // Nazwa serii pomiarów
    private String          labelHeader;        // Nagłówek w tabeli przy etykietach serii

    /**
     * Konstruktor grupy inicjujący domyślne wartości
     * @param name nazwa grupy pomiarów
     */
    public SeriesGroup(String name)
    {
        this.name = Objects.requireNonNull(name);
        this.setPrefix(PREFIX);

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
        // TODO kontrola zmian zaznaczenia przy zmianach grup
        Arrays.stream(selectedSeries).forEach(this.children::get);        // Sprawdź poprawność indeksów
        ArrayList<Series> oldSelected = this.selectedSeries.stream()
                .map(this.children::get)
                .collect(Collectors.toCollection(ArrayList::new));
        ArrayList<Series> newSelected = Arrays.stream(selectedSeries)
                .mapToObj(this.children::get)
                .collect(Collectors.toCollection(ArrayList::new));
        this.selectedSeries = Arrays.stream(selectedSeries)
                .boxed()
                .collect(Collectors.toCollection(ArrayList::new));

        PropertyChangeEvent evt = new PropertyChangeEvent(this, SELECTED_SERIES, oldSelected, newSelected);
        this.propertyFirer.firePropertyChange(evt);
    }

    public int getHighlightedSeriesIdx() {
        return highlightedSeries;
    }

    public void setHighlightedSeries(int highlightedSeries) {
        // Sprawdź poprawność indeksu
        this.validateNullableIdx(highlightedSeries);
        int oldValue = this.highlightedSeries;
        this.highlightedSeries = highlightedSeries;
        PropertyChangeEvent evt = new PropertyChangeEvent(this, HIGHLIGHTED_SERIES, oldValue, highlightedSeries);
        this.propertyFirer.firePropertyChange(evt);
    }

    /* Pozostałe metody */

    /**
     * Metoda dodaje serię do listy
     * @param element seria do dodania
     * @param index pozyzja, na której ma być dodana. -1, jeśli na końcu
     * @throws NullPointerException jeśli series == null
     * @throws IndexOutOfBoundsException, jeśli pos jest poza [0, {@link SeriesGroup#getNumberOfElements()}  - 1}]
     * @throws IllegalArgumentException jeśli seria jest już w grupie
     */
    @Override
    public int addElement(Series element, int index)
    {
        this.validateNotNull(element);
        this.validateAddIdx(index);
        Utils.shiftIndicesAfterAddition(index, this.selectedSeries);
        if (index != -1 && this.highlightedSeries >= index)
            this.highlightedSeries++;
        return super.addElement(element, index);
    }

    /**
     * Metoda usuwa serię z listy po indeksie. Jeśli usuwana seria jest właśnie podświetlona, ustawia podświetlenie na
     * -1 i wyzwalane jest zdarzenia zmiany podświetlenia
     * @param index pozycja serii
     * @throws IndexOutOfBoundsException jeśli element pod wskazanym indeksem nie istnieje
     * @return liczba serii pozostałych po usunięciu
     */
    @Override
    public int deleteElement(int index)
    {
        this.validateIdx(index);
        Utils.removeElementFromIndicesList(index, this.selectedSeries);
        if (this.highlightedSeries == index)
            this.setHighlightedSeries(-1);
        else if (this.highlightedSeries > index)
            this.highlightedSeries--;
        return super.deleteElement(index);
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

    @Override
    public String toString() {
        return "grupa \"" + this.name + "\"";
    }
}
