// QuickMean - project.LabProject.java
//---------------------------------------------------------------------
// Wewnętrzna reprezentacja całego projektu pomiarów. Posiada tablicę
// ze wszystkimi grupami i podstawowe informacje o projekcie.
//---------------------------------------------------------------------
// Utworzono 20:42 02.05.2017 w IntelliJ IDEA
// (C)PKua, wszystkie prawa zastrzeżone
//---------------------------------------------------------------------

package pl.edu.uj.student.kubala.piotr.qm.lab;

import pl.edu.uj.student.kubala.piotr.qm.EDTInitializable;
import pl.edu.uj.student.kubala.piotr.qm.Model;
import pl.edu.uj.student.kubala.piotr.qm.PropagatingListModel;
import pl.edu.uj.student.kubala.piotr.qm.converters.Converter;

import java.beans.PropertyChangeEvent;
import java.io.File;
import java.util.Arrays;

public class LabProject extends PropagatingListModel<SeriesGroup> implements EDTInitializable
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
        this.file = file;
        this.setPrefix(PREFIX);

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
     * @param element grupa serii do dodania
     * @param index pozyzja, na której ma być dodana. -1, jeśli na końcu
     * @throws NullPointerException jeśli seriesGroup == null
     * @throws IndexOutOfBoundsException, jeśli pos jest poza [0, {@link LabProject#getNumberOfElements()}  - 1}]
     * @throws IllegalArgumentException jeśli grupa jest już w projekcie
     */
    @Override
    public int addElement(SeriesGroup element, int index)
    {
        this.validateNotNull(element);
        this.validateAddIdx(index);
        if (this.getSeriesGroupByName(element.getName()) != null)
            throw new RuntimeException("Istnieje już grupa o nazwie nowo dodawanej - " + element.getName());
        if (index != -1 && index <= this.selectedSeriesGroup)
            this.selectedSeriesGroup++;
        return super.addElement(element, index);
    }

    /**
     * Metoda usuwa grupę serii z listy po indeksie
     * @param index pozycja grupy serii
     * @throws IndexOutOfBoundsException jeśli element pod wskazanym indeksem nie istnieje
     * @return liczba grup serii pozostałych po usunięciu
     */
    @Override
    public int deleteElement(int index)
    {
        this.validateIdx(index);
        if (selectedSeriesGroup == index)
            this.setSelectedSeriesGroup(-1);
        else if (selectedSeriesGroup > index)
            this.selectedSeriesGroup--;
        return super.deleteElement(index);
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
        this.validateNullableIdx(seriesGroupIdx);
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
        return selectedGroup.getElement(idx);
    }

    /**
     * Metoda zwraca obecnie zaznaczone serie w obecnie wybranej grupie. Jeśli którakolwiek część hierarchii nie jest
     * wybrana, tablicę zero elementową
     * @return obecnie zaznaczone serie w obecnie wybranej grupie lub {@code null}
     */
    public Series [] getSelectedSeries()
    {
        SeriesGroup sel_group = this.getSelectedSeriesGroup();
        if (sel_group == null)
            return new Series[0];
        int [] sel_series_idx = sel_group.getSelectedSeries();
        if (sel_series_idx.length == 0)
            return new Series[0];
        return Arrays.stream(sel_series_idx)
                .mapToObj(sel_group::getElement)
                .toArray(Series[]::new);
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
     * Pobiera grupę na podstawie nazwy.
     * @param name nazwa grupy
     * @return grupa o podanej nazwie lub {@code null}, jeśli nie istnieje
     */
    public SeriesGroup getSeriesGroupByName(String name)
    {
        for (SeriesGroup group : this.children)
            if (group.getName().equals(name))
                return group;
        return null;
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

    @Override
    public Model getParent() {
        throw new RuntimeException("Niezaimplementowane");
    }

    @Override
    public void setParent(Model parent) {
        throw new RuntimeException("Niezaimplementowane");
    }

    /**
     * Metoda ustawia w projekcje domyślne dane
     */
    public void init()
    {
        Series          defaultSeries;
        SeriesGroup     defaultSeriesGroup;

        /*defaultSeriesGroup = new SeriesGroup();
        defaultSeries = new Series();
        defaultSeriesGroup.addElement(defaultSeries);
        defaultSeriesGroup.setHighlightedSeries(0);
        this.addElement(defaultSeriesGroup);
        this.setSelectedSeriesGroup(0);*/

        defaultSeriesGroup = new SeriesGroup();

        defaultSeries = new Series();
        defaultSeries.addElement(new Measure(5435634, 234, 234, 0));
        defaultSeries.addElement(new Measure(5475783, 0, 34, 0));
        defaultSeries.addElement(new Measure(5436724, 734, 24, 0));
        defaultSeries.updateMean();
        defaultSeriesGroup.addElement(defaultSeries);

        defaultSeries = new Series();
        defaultSeries.setCalibrationError(500);
        defaultSeries.addElement(new Measure(545634, 0, 234, 0));
        defaultSeries.addElement(new Measure(545783, 0, 34, 0));
        defaultSeries.addElement(new Measure(546724, 0, 24, 0));
        defaultSeries.updateMean();
        defaultSeriesGroup.addElement(defaultSeries);

        defaultSeries = new Series();
        defaultSeries.addElement(new Measure(0.54534, 0.0234, 0.0234, 0));
        defaultSeries.addElement(new Measure(0.54783, 0.0, 0.034, 0));
        defaultSeries.addElement(new Measure(0.54324, 0.0734, 0.024, 0));
        defaultSeries.updateMean();
        defaultSeriesGroup.addElement(defaultSeries);

        defaultSeriesGroup.setSelectedSeries(new int[]{1});
        defaultSeriesGroup.setHighlightedSeries(1);
        this.addElement(defaultSeriesGroup);


        defaultSeriesGroup = new SeriesGroup();

        defaultSeries = new Series();
        defaultSeriesGroup.addElement(defaultSeries);

        defaultSeries = new Series();
        defaultSeriesGroup.addElement(defaultSeries);

        defaultSeries = new Series();
        defaultSeriesGroup.addElement(defaultSeries);

        defaultSeries = new Series();
        defaultSeriesGroup.addElement(defaultSeries);

        defaultSeriesGroup.setSelectedSeries(new int[]{2});
        defaultSeriesGroup.setHighlightedSeries(2);
        this.addElement(defaultSeriesGroup);
        this.setSelectedSeriesGroup(0);
    }

    @Override
    public String getEDTInitializableName() {
        return "LabProject";
    }

    @Override
    public String toString()
    {
        return "LabProject";
    }
}
