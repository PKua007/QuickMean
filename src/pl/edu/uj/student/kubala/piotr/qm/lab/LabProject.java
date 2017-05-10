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

import java.io.File;
import java.util.ArrayList;

public class LabProject
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
     * @param pos pozyzja, na której ma być dodana. -1, jeśli na końcu
     */
    public void addSeriesGroup(SeriesGroup seriesGroup, int pos)
    {

    }

    /**
     * Metoda dodaje grupę serii na końcu listy
     * @param seriesGroup grupa serii do dodania
     */
    public void addSeriesGroup(SeriesGroup seriesGroup)
    {
        this.addSeriesGroup(seriesGroup, -1);
    }

    /**
     * Metoda pobiera grupę serii
     * @param pos pozycja grupy serii na liście
     * @return grupa serii z podanej pozycji
     */
    public SeriesGroup getSeriesGroup(int pos)
    {
        return this.seriesGroups.get(pos);
    }

    /**
     * Metoda usuwa grupę serii z listy po indeksie
     * @param pos pozycja grupy serii
     * @return liczba grup serii pozostałych po usunięciu
     */
    public int deleteSeriesGroup(int pos)
    {
        return 0;
    }

    /**
     * Metoda usuwa grupę serii z listy
     * @param seriesGroup grupa serii do usunięcia
     * @return liczba grup serii pozostałych po usunięciu
     */
    public int deleteSeriesGroup(SeriesGroup seriesGroup)
    {
        return 0;
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
     * Metoda ustawia obecnie wybraną grupę serii
     * @param seriesGroupIdx indeks wybranej serii grup
     */
    public void setSelectedSeriesGroup(int seriesGroupIdx)
    {

    }

    /**
     * Funkcja zwraca indeks w tablicy obecnie wybranej grupy serii
     * @return indeks obecnie wybranej grupy serii
     */
    public int getSelectedSeriesGroupIdx()
    {
        return 0;
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
