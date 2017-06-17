// QuickMean - project.LabProject.java
//---------------------------------------------------------------------
// Wewnętrzna reprezentacja całego projektu pomiarów. Posiada tablicę
// ze wszystkimi grupami i podstawowe informacje o projekcie.
//---------------------------------------------------------------------
// Utworzono 20:42 02.05.2017 w IntelliJ IDEA
// (C)PKua, wszystkie prawa zastrzeżone
//---------------------------------------------------------------------

package pl.edu.uj.student.kubala.piotr.qm.lab;

import pl.edu.uj.student.kubala.piotr.qm.converters.Converter;

import java.beans.PropertyChangeEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

public class LabProject extends PropagatingListModel<SeriesGroup>
{
    /* Etykiety właściwości */
    private static final String PREFIX = "lp";
    
    public static final String  NEW_GROUP = PREFIX + "." + NEW;
    public static final String  DEL_GROUP = PREFIX + "." + DEL;
    public static final String  SELECTED_GROUP = PREFIX + ".selected_group";

    private File        file;           // Plik, w którym jest zapisane laboratorium (zawiera nazwę)
    private boolean     saved;          // Czy zmiany w pliku są zapisane?
    private boolean     everSaved;      // Czy plik był kiedykolwiek zapisany?
    private int         selectedSeriesGroup;  // Wybrana grupa

    /**
     * Konstruktor tworzący laboratorium. Zapisuje plik, z którym jest powiązane
     * @param file plik, z którym laboratorium jest powiązane
     */
    public LabProject(File file)
    {
        this.setPrefix(PREFIX);

        this.file = file;

        this.saved = false;
        this.everSaved = false;
        this.selectedSeriesGroup = -1;
    }

    /* Gettery solo */

    public File getFile() {
        return file;
    }

    public boolean isSaved() {
        return saved;
    }

    public boolean isEverSaved() {
        return everSaved;
    }

    /* Metody zarządzania seriami grup */

    /**
     * Metoda dodaje grupę serii do listy
     * @param seriesGroup grupa serii do dodania
     * @param index pozyzja, na której ma być dodana. -1, jeśli na końcu
     * @throws NullPointerException jeśli seriesGroup == null
     * @throws IndexOutOfBoundsException, jeśli pos jest poza [0, {@link LabProject#getNumberOfGroupSeries()}  - 1}]
     * @throws IllegalArgumentException jeśli grupa jest już w projekcie
     */
    public void addSeriesGroup(SeriesGroup seriesGroup, int index)
    {
        if (index != -1 && index <= this.selectedSeriesGroup)
            this.selectedSeriesGroup++;
        this.addChild(seriesGroup, index);
    }

    /**
     * Metoda dodaje grupę serii na końcu listy
     * @param seriesGroup grupa serii do dodania
     * @throws NullPointerException jeśli seriesGroup == null
     * @throws IllegalArgumentException jeśli grupa jest już w projekcie
     */
    public void addSeriesGroup(SeriesGroup seriesGroup)
    {
        this.addSeriesGroup(seriesGroup, -1);
    }

    /**
     * Metoda pobiera grupę serii
     * @param pos pozycja grupy serii na liście
     * @throws IndexOutOfBoundsException jeśli element pod wskazanym indeksem nie istnieje
     * @return grupa serii z podanej pozycji
     */
    public SeriesGroup getSeriesGroup(int pos)
    {
        return this.getChild(pos);
    }

    /**
     * Metoda usuwa grupę serii z listy po indeksie
     * @param pos pozycja grupy serii
     * @throws IndexOutOfBoundsException jeśli element pod wskazanym indeksem nie istnieje
     * @return liczba grup serii pozostałych po usunięciu
     */
    public int deleteSeriesGroup(int pos)
    {
        if (selectedSeriesGroup == pos)
            this.setSelectedSeriesGroup(-1);
        else if (selectedSeriesGroup > pos)
            this.selectedSeriesGroup--;
        return this.deleteChild(pos);
    }

    /**
     * Metoda usuwa grupę serii z listy
     * @param seriesGroup grupa serii do usunięcia
     * @return liczba grup serii pozostałych po usunięciu
     */
    public int deleteSeriesGroup(SeriesGroup seriesGroup)
    {
        int index = this.children.indexOf(seriesGroup);
        if (index != -1)
            this.deleteSeriesGroup(index);
        return this.children.size();
    }

    /**
     * Metoda zwraca liczbę grup serii w laboratorium
     * @return liczba grup serii w laboratorium
     */
    public int getNumberOfGroupSeries()
    {
        return this.getNumberOfChildren();
    }

    /**
     * Metoda zwraca obecnie wybraną grupę
     * @return obecnie wybrana grupa serii
     */
    public SeriesGroup getSelectedSeriesGroup()
    {
        if (this.selectedSeriesGroup == -1)
            return null;
        else
            return this.children.get(this.selectedSeriesGroup);
    }

    /**
     * Ustawia nowy indeks wybranej grupy serii. -1 oznacza brak zaznaczenia
     * @param seriesGroupIdx indeks wybranej grupy serii
     * @throws IndexOutOfBoundsException jeśli indeks jest niepoprawny
     */
    public void setSelectedSeriesGroup(int seriesGroupIdx)
    {
        if (seriesGroupIdx != -1) // Sprawdź poprawność indeksu
            this.children.get(seriesGroupIdx);
        SeriesGroup oldSelected = getSelectedSeriesGroup();
        this.selectedSeriesGroup = seriesGroupIdx;
        SeriesGroup newSelected = getSelectedSeriesGroup();
        PropertyChangeEvent evt = new PropertyChangeEvent(this, SELECTED_GROUP, oldSelected, newSelected);
        this.propertyFirer.firePropertyChange(evt);
    }

    /**
     * Metoda zwraca obecnie podświetloną serię w obecnie wybranej grupie. Jeśli którakolwiek część hierarchii nie
     * jest wybrana - {@code null}.
     * @return obecnie podświetloną serię w obecnie wybranej grupie lub {@code null}
     */
    public Series getHighlightedSeries()
    {
        SeriesGroup selectedGroup = this.getSelectedSeriesGroup();
        if (selectedGroup == null)
            return null;
        int idx = selectedGroup.getHighlightedSeriesIdx();
        if (idx == -1)
            return null;
        return selectedGroup.getSeries(idx);
    }

    /**
     * Funkcja zwraca indeks w tablicy obecnie wybranej grupy serii
     * @return indeks obecnie wybranej grupy serii
     */
    public int getSelectedSeriesGroupIdx()
    {
        return this.selectedSeriesGroup;
    }

    /**
     * Metoda zwraca indeks podanej grupy serii, lub -1, jeśli nie znaleziono
     * @param seriesGroup poszukiwana grupa serii
     * @return indeks podanej grupy serii, lub -1, jeśli nie znaleziono
     */
    public int getSeriesGroupIdx(SeriesGroup seriesGroup)
    {
        return this.getChildIdx(seriesGroup);
    }

    /* Metody zapisu i otwierania */

    /**
     * Metoda wczytuje laboratorium z pliku i zapamiętuje go. Używa do tego strategii LABConverter
     * @param file plik, z którego laboratorium ma zostać wczytane
     */
    public void loadFromFile(File file)
    {

    }

    /**
     * Metoda zapisuje laboratorium w pliku i zapamiętuje go. Używa do tego strategii LABConverter
     * @param file plik, do któego laboratorium ma zostać zapisane
     */
    public void saveToFile(File file)
    {

    }

    /**
     * Metoda importuje dane do laboratorium
     * @param converter konwerter, który ma zaimportować laboratorium
     */
    public void importLab(Converter converter)
    {

    }

    /**
     * Metoda eksportuje dane laboratirum
     * @param converter konwerter, który ma wyeksportować laboratorium
     */
    public void exportLab(Converter converter)
    {

    }

    /**
     * Metoda ustawia w projekcje domyślne dane
     */
    public void setupDefault()
    {
        Series          defaultSeries;
        SeriesGroup     defaultSeriesGroup;

        /*defaultSeriesGroup = new SeriesGroup();
        defaultSeries = new Series();
        defaultSeriesGroup.addSeries(defaultSeries);
        defaultSeriesGroup.setHighlightedSeries(0);
        this.addSeriesGroup(defaultSeriesGroup);
        this.setSelectedSeriesGroup(0);*/

        defaultSeriesGroup = new SeriesGroup();

        defaultSeries = new Series();
        defaultSeries.addMeasure(new Measure(5435634, 234, 234, 0));
        defaultSeries.addMeasure(new Measure(5475783, 234, 34, 0));
        defaultSeries.addMeasure(new Measure(5436724, 734, 24, 0));
        defaultSeries.updateMean();
        defaultSeriesGroup.addSeries(defaultSeries);

        defaultSeries = new Series();
        defaultSeries.addMeasure(new Measure(545634, 234, 234, 0));
        defaultSeries.addMeasure(new Measure(545783, 234, 34, 0));
        defaultSeries.addMeasure(new Measure(546724, 734, 24, 0));
        defaultSeries.updateMean();
        defaultSeriesGroup.addSeries(defaultSeries);

        defaultSeries = new Series();
        defaultSeries.addMeasure(new Measure(0.54534, 0.0234, 0.0234, 0));
        defaultSeries.addMeasure(new Measure(0.54783, 0.0234, 0.034, 0));
        defaultSeries.addMeasure(new Measure(0.54324, 0.0734, 0.024, 0));
        defaultSeries.updateMean();
        System.out.println(defaultSeries.getMeanQuantity());
        defaultSeriesGroup.addSeries(defaultSeries);

        defaultSeriesGroup.setHighlightedSeries(1);
        this.addSeriesGroup(defaultSeriesGroup);


        defaultSeriesGroup = new SeriesGroup();

        defaultSeries = new Series();
        defaultSeriesGroup.addSeries(defaultSeries);

        defaultSeries = new Series();
        defaultSeriesGroup.addSeries(defaultSeries);

        defaultSeries = new Series();
        defaultSeriesGroup.addSeries(defaultSeries);

        defaultSeries = new Series();
        defaultSeriesGroup.addSeries(defaultSeries);

        defaultSeriesGroup.setHighlightedSeries(2);
        this.addSeriesGroup(defaultSeriesGroup);
        this.setSelectedSeriesGroup(0);
    }
}
