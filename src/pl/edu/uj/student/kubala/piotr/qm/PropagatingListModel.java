// QuickMean - PropagatingModel.java
//---------------------------------------------------------------------
// Rozszerzenie modelu, które implementuje PropartyChangeListener
// i wyrzuca "w górę" odebrane zdarzenia
//---------------------------------------------------------------------
// Utworzono 13:12 28.05.2017 w IntelliJ IDEA
// (C)PKua, wszystkie prawa zastrzeżone
//---------------------------------------------------------------------

package pl.edu.uj.student.kubala.piotr.qm;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;



/**
 * Absrakcyjna klasa modelu w postaci listy innych modeli, przechwytująca zdarzenia z podległych modeli i przekazująca
 * swoim listenerom. Udostępnia metody dodawania i usuwania elementów wspólne dla wszystkich klas potomnych.
 * @param <E> typ przechowywanego elementu
 */
public abstract class PropagatingListModel<E extends Model> extends Model implements PropertyChangeListener
{
    protected static final String NEW = "new";
    protected static final String DEL = "del";

    private String prefix = "";

    protected ArrayList<E> children;

    public PropagatingListModel()
    {
        this.children = new ArrayList<>();
    }

    /**
     * Ustawia prefiks specyficzny dla rozszerzającej klasy dla nazw właściwości PropertyChangeEvent.
     * @param prefix prefisk umieszczany przez nazwami właściwości przy wywoływaniu zdarzeń
     */
    protected void setPrefix(String prefix)
    {
        this.prefix = Objects.requireNonNull(prefix);
    }

    /**
     * Wyłapuje PropertyChangeEvent z podległych modeli i wysyła do swoich listenerów.
     * @param evt zdarzenie zmiany właściwości
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt)
    {
        this.propertyFirer.firePropertyChange(evt);
    }

    /**
     * Metoda dodaje element do listy i ustawia w nim rodzica na this. Niedozwolona wartość null.
     * @param element element do dodania
     * @param index pozyzja, na której ma być dodany. -1, jeśli na końcu
     * @return indeks dodanego elementu
     * @throws NullPointerException jeśli element == null
     * @throws IndexOutOfBoundsException jeśli index jest poza [-1, {@link PropagatingListModel#getNumberOfElements()}]
     * @throws IllegalArgumentException jeśli element już jest na liście
     */
    public int addElement(E element, int index)
    {
        int idx;
        this.validateNotNull(element);
        if (this.children.indexOf(element) != -1) {
            throw new IllegalArgumentException(element.getClass().getSimpleName() + " jest już w " + this.getClass().getSimpleName());
        } if (index == -1) {
            this.children.add(element);
            idx = getNumberOfElements() - 1;
        } else {
            this.children.add(index, element);
            idx = index;
        }

        element.setParent(this);
        element.addPropertyChangeListener(this);
        PropertyChangeEvent evt = new PropertyChangeEvent(this, prefix + "." + NEW, null, element);
        this.propertyFirer.firePropertyChange(evt);
        return idx;
    }

    /**
     * Metoda dodaje element na końcu listy i ustawia w nim rodzica na this. Niedozwolona wartość null.
     * @param element element do dodania
     * @return indeks dodanego elementu
     * @throws NullPointerException jeśli element == null
     * @throws IllegalArgumentException jeśli element już jest na liście
     */
    public int addElement(E element)
    {
        return this.addElement(element, -1);
    }

    /**
     * Metoda zwraca element spod indeksu
     * @param index indeks do zwrócenia elementu
     * @throws IndexOutOfBoundsException jeśli index jest poza [0, {@link PropagatingListModel#getNumberOfElements()} - 1]
     * @return element z podanej pozycji
     */
    public E getElement(int index)
    {
        return this.children.get(index);
    }

    /**
     * Metoda usuwa element z listy po indeksie i ustawia w nim rodzica na null.
     * @param index pozycja elementu
     * @return liczba elementów pozostałych po usunięciu
     * @throws IndexOutOfBoundsException jeśli element pod wskazanym indeksem nie istnieje
     */
    public int deleteElement(int index)
    {
        E element = this.children.get(index);
        // Usuń dzieci, jeśli posiada
        if (element instanceof PropagatingListModel)
            ((PropagatingListModel)element).clear();

        this.children.remove(index);
        element.setParent(null);
        element.removePropertyChangeListener(this);
        PropertyChangeEvent evt = new PropertyChangeEvent(this, prefix + "." + DEL, element, null);
        this.propertyFirer.firePropertyChange(evt);
        return getNumberOfElements();
    }

    /**
     * Metoda usuwa zakres elementów z listy, zaczynając od początku. Dla każdego rozsyła osobne powiadomienia
     * @param begidx początkowy indeks (włącznie)
     * @param endidx końcowy indeks (wyłącznie)
     * @throws IndexOutOfBoundsException jeśli indeksy leżą poza dozwolonym zakresem
     * @throws IllegalArgumentException jesli {@code begidx > endidx}
     * @return liczba elementów pozostałych po usunięciu
     */
    public int deleteElementRange(int begidx, int endidx)
    {
        // TODO: fix this bullshit
        throw new UnsupportedOperationException();
        /*validateIdx(begidx);
        validateIdx(endidx - 1);
        if (begidx > endidx)
            throw new IllegalArgumentException("begidx > endidx");
        while (--endidx >= begidx)
            this.deleteElement(begidx);
        return getNumberOfElements();*/
    }

    /**
     * Metoda usuwa element z listy poprzez referencję.
     * @param element element do usunięcia
     * @return liczba elementów pozostałych po usunięciu
     */
    public int deleteElement(E element)
    {
        int idx = this.getElementIdx(element);
        if (idx != -1)
            deleteElement(idx);
        return this.children.size();
    }

    /**
     * Metoda usuwa wszystkie elementy na liście. Powiadomienia PropertyChangeEvent są wysyłane dla każdego z osobna
     */
    public void clear()
    {
        while (getNumberOfElements() > 0)
            this.deleteElement(0);
    }

    /**
     * Metoda zwraca niemodyfikowalną listę wszystkich elementów
     * @return niemodyfikowalna lista wszystkich elementów
     */
    public List<E> getAllElements()
    {
        return Collections.unmodifiableList(children);
    }

    /**
     * Metoda zwraca liczbę pomiarów w serii
     * @return liczba pomiarów w serii
     */
    public int getNumberOfElements()
    {
        return this.children.size();
    }

    /**
     * Metoda zwraca indeks podanego pomiaru, lub -1, jeśli nie znaleziono
     * @param element poszukiwany pomiar
     * @return indeks poszukiwanego pomiaru, lub -1, jeśli nie znaleziono
     */
    public int getElementIdx(E element)
    {
        return this.children.indexOf(element);
    }

    /**
     * Testuj, czy dzieciak niezerowy
     * @param child dzieciak do przetestowania
     * @throws NullPointerException jeśli dzieciak zerowy
     */
    protected void validateNotNull(E child) {
        Objects.requireNonNull(child);
    }

    /**
     * Testuj, czy indeks prawidłowy (bez -1)
     * @param idx indeks do testowania
     * @throws IndexOutOfBoundsException jeśli niepoprany indeks elementu
     */
    protected void validateIdx(int idx) {
        this.children.get(idx);
    }

    /**
     * Testuj, czy indeks prawidłowy (z -1)
     * @param idx indeks do testorania
     * @throws IndexOutOfBoundsException jeśli niepoprawny indeks elementu
     */
    protected void validateNullableIdx(int idx) {
        if (idx != -1)
            this.children.get(idx);
    }

    /**
     * Testuj, czy prawidłowy indeks przy dodawaniu (dozwolony -1 i o 1 większy niż maksymalny)
     * @param idx indeks do sprawdzenia
     * @throws IndexOutOfBoundsException jeśli nieprawidłowy indeks
     */
    protected void validateAddIdx(int idx) {
        if (idx < -1 || idx > this.children.size())
            throw new IndexOutOfBoundsException("Index: " + idx + ", Size: " + this.children.size());
    }
}
