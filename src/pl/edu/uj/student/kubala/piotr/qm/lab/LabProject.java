// QuickMean - project.LabProject.java
//---------------------------------------------------------------------
// Wewnętrzna reprezentacja całego projektu pomiarów. Posiada tablicę
// ze wszystkimi grupami i podstawowe informacje o projekcie.
//---------------------------------------------------------------------
// Utworzono 20:42 02.05.2017 w IntelliJ IDEA
// (C)PKua, wszystkie prawa zastrzeżone
//---------------------------------------------------------------------

package pl.edu.uj.student.kubala.piotr.qm.lab;

import pl.edu.uj.student.kubala.piotr.qm.Model;
import pl.edu.uj.student.kubala.piotr.qm.converters.Converter;

import java.beans.PropertyChangeEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

public class LabProject extends Model
{
    private ArrayList<SeriesGroup>      seriesGroups;   // Tablica z grupami serii pomiarów

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
        this.file = file;

        this.saved = false;
        this.everSaved = false;
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
        Objects.requireNonNull(seriesGroup);
        if (this.seriesGroups.indexOf(seriesGroup) != -1)
            throw new IllegalArgumentException("Grupa jest już w projekcie");
        if (index == -1)
            this.seriesGroups.add(seriesGroup);
        else
            this.seriesGroups.add(index, seriesGroup);
        PropertyChangeEvent evt = new PropertyChangeEvent(this, "new_group", null, seriesGroup);
        this.propertyFirer.firePropertyChange(evt);
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
        return this.seriesGroups.get(pos);
    }

    /**
     * Metoda usuwa grupę serii z listy po indeksie
     * @param pos pozycja grupy serii
     * @throws IndexOutOfBoundsException jeśli element pod wskazanym indeksem nie istnieje
     * @return liczba grup serii pozostałych po usunięciu
     */
    public int deleteSeriesGroup(int pos)
    {
        SeriesGroup seriesGroup = this.seriesGroups.get(pos);
        this.seriesGroups.remove(pos);
        seriesGroup.setParentLab(null);
        PropertyChangeEvent evt = new PropertyChangeEvent(this, "del_group", seriesGroup, null);
        this.propertyFirer.firePropertyChange(evt);
        return this.seriesGroups.size();
    }

    /**
     * Metoda usuwa grupę serii z listy
     * @param seriesGroup grupa serii do usunięcia
     * @return liczba grup serii pozostałych po usunięciu
     */
    public int deleteSeriesGroup(SeriesGroup seriesGroup)
    {
        int index = this.seriesGroups.indexOf(seriesGroup);
        if (index != -1) {
            this.seriesGroups.remove(index);
            seriesGroup.setParentLab(null);
            if (this.selectedSeriesGroup > index)       // Zaktualizuj indeks wybranej grupy
                this.selectedSeriesGroup--;
            PropertyChangeEvent evt = new PropertyChangeEvent(this, "del_group", seriesGroup, null);
            this.propertyFirer.firePropertyChange(evt);
        }
        return this.seriesGroups.size();
    }

    /**
     * Metoda zwraca liczbę grup serii w laboratorium
     * @return liczba grup serii w laboratorium
     */
    public int getNumberOfGroupSeries()
    {
        return this.seriesGroups.size();
    }

    /**
     * Metoda zwraca obecnie wybraną grupę
     * @return obecnie wybrana grupa serii
     */
    public SeriesGroup getSelectedSeriesGroup()
    {
        return this.seriesGroups.get(this.selectedSeriesGroup);
    }

    /**
     * Ustawia nowy indeks wybranej grupy serii. -1 oznacza brak zaznaczenia
     * @param seriesGroupIdx indeks wybranej grupy serii
     * @throws IndexOutOfBoundsException jeśli indeks jest niepoprawny
     */
    public void setSelectedSeriesGroup(int seriesGroupIdx)
    {
        if (seriesGroupIdx != -1) // Sprawdź poprawność indeksu
            this.seriesGroups.get(seriesGroupIdx);
        SeriesGroup oldSelected = this.seriesGroups.get(this.selectedSeriesGroup);
        this.selectedSeriesGroup = seriesGroupIdx;
        SeriesGroup newSelected = this.seriesGroups.get(this.selectedSeriesGroup);
        PropertyChangeEvent evt = new PropertyChangeEvent(this, "selectedGroup", oldSelected, newSelected);
    }

    /**
     * Funkcja zwraca indeks w tablicy obecnie wybranej grupy serii
     * @return indeks obecnie wybranej grupy serii
     */
    public int getSelectedSeriesGroupIdx()
    {
        return this.selectedSeriesGroup;
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
}
