// QuickMean - project.SeriesGroup.java
//---------------------------------------------------------------------
// Wewnętrzna reprezentacja grupy serii pomiarów. Zawieta tablicę
// z poszczególnymi seriami i dane grupy
//---------------------------------------------------------------------
// Utworzono 20:43 02.05.2017 w IntelliJ IDEA
// (C)PKua, wszystkie prawa zastrzeżone
//---------------------------------------------------------------------

package pl.edu.uj.student.kubala.piotr.qm.lab;

import pl.edu.uj.student.kubala.piotr.qm.Model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class SeriesGroup extends Model
{
    public static final String  DEFAULT_LABEL_HEADER = "Nazwa serii";

    private static int staticIdx = 0;

    private ArrayList<Series>   series;             // Tablica z seriami pomiarowymi
    private int []              selectedSeries;     // Indeksy w tablicy zaznaczonych serii grup
    private LabProject          parentLab;          // Laboratorium, do którego należy grupa pomiarów

    private String          name;               // Nazwa serii pomiarów
    private String          labelHeader;        // Nagłówek w tabeli przy etykietach serii

    /**
     * Konstruktor grupy inicjujący domyślne wartości
     * @param name nazwa grupy pomiarów
     */
    public SeriesGroup(String name)
    {
        this.name = Objects.requireNonNull(name);

        this.series = new ArrayList<>();
        this.selectedSeries = new int[0];
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

    public void setName(String name) {
        this.name = Objects.requireNonNull(name);
    }

    public String getLabelHeader() {
        return labelHeader;
    }

    public void setLabelHeader(String labelHeader) {
        this.labelHeader = Objects.requireNonNull(labelHeader);
    }

    public int[] getSelectedSeries() {
        return Arrays.copyOf(this.selectedSeries, selectedSeries.length);
    }

    public void setSelectedSeries(int[] selectedSeries) {
        this.selectedSeries = Arrays.copyOf(selectedSeries, selectedSeries.length);
    }

    /* Gettery solo */

    public LabProject getParentLab() {
        return parentLab;
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
        Objects.requireNonNull(series);
        if (this.series.indexOf(series) != -1)
            throw new IllegalArgumentException("Seria jest już w grupie");
        if (index == -1)
            this.series.add(series);
        else
            this.series.add(index, series);
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
        return this.series.get(pos);
    }

    /**
     * Metoda usuwa serię z listy po indeksie
     * @param pos pozycja serii
     * @throws IndexOutOfBoundsException jeśli element pod wskazanym indeksem nie istnieje
     * @return liczba serii pozostałych po usunięciu
     */
    public int deleteSeries(int pos)
    {
        Series series = this.series.get(pos);
        this.series.remove(pos);
        series.setParentGroup(null);
        return this.series.size();
    }

    /**
     * Metoda usuwa serię z listy
     * @param series seria do usunięcia
     * @return liczba serii pozostałych po usunięciu
     */
    public int deleteSeries(Series series)
    {
        if (this.series.remove(series))
            series.setParentGroup(null);
        return this.series.size();
    }

    /**
     * Metoda zwraca liczbę serii w grupie
     * @return liczba serii w grupie
     */
    public int getNumberOfSeries()
    {
        return this.series.size();
    }
}
