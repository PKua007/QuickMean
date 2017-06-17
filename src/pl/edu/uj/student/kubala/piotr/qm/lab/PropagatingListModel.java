// QuickMean - PropagatingModel.java
//---------------------------------------------------------------------
// Rozszerzenie modelu, które implementuje PropartyChangeListener
// i wyrzuca "w górę" odebrane zdarzenia
//---------------------------------------------------------------------
// Utworzono 13:12 28.05.2017 w IntelliJ IDEA
// (C)PKua, wszystkie prawa zastrzeżone
//---------------------------------------------------------------------

package pl.edu.uj.student.kubala.piotr.qm.lab;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Objects;


//#############################################
// DO ZROBIENIA:
// - nieokreślony stan po złym wywołaniu
//#############################################


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
     * Ustawia prefiks specyficzny dla rozszerzającej klasy
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
     * Metoda dodaje pomiar do listy pomiarów i ustawia w nim rodzica na this. Niedozwolona wartość null.
     * @param child pomiar do dodania
     * @param index pozyzja, na której ma być dodany. -1, jeśli na końcu
     * @throws NullPointerException jeśli measure == null
     * @throws IndexOutOfBoundsException jeśli index jest poza [-1, {@link Series#getNumberOfChildren()}]
     * @throws IllegalArgumentException jeśli pomiar już jest w serii
     */
    public void addChild(E child, int index)
    {
        this.validateNotNull(child);
        if (this.children.indexOf(child) != -1) {
            throw new IllegalArgumentException(child.getClass().getSimpleName() + " jest już w " + this.getClass().getSimpleName());
        } if (index == -1) {
            this.children.add(child);
        } else {
            this.children.add(index, child);
        }

        child.setParent(this);
        child.addPropertyChangeListener(this);
        PropertyChangeEvent evt = new PropertyChangeEvent(this, prefix + "." + NEW, null, child);
        this.propertyFirer.firePropertyChange(evt);
    }

    /**
     * Metoda dodaje pomiar na końcu listy pomiarów i ustawia w nim rodzica na this. Niedozwolona wartość null.
     * @param child pomiar do dodania
     * @throws NullPointerException jeśli measure == null
     * @throws IllegalArgumentException jeśli pomiar już jest w serii
     */
    public void addChild(E child)
    {
        this.addChild(child, -1);
    }

    /**
     * Metoda pobiera pomiar
     * @param pos pozycja pomiaru
     * @throws IndexOutOfBoundsException, jeśli pos jest poza [0, {@link Series#getNumberOfChildren()} - 1]
     * @return pomiar z podanej pozycji
     */
    public E getChild(int pos)
    {
        return this.children.get(pos);
    }

    /**
     * Metoda usuwa pomiar z listy po indeksie i ustawia w nim rodzica na null.
     * @param pos pozycja pomiaru
     * @return liczba pomiarów pozostałych po usunięciu
     * @throws IndexOutOfBoundsException jeśli element pod wskazanym indeksem nie istnieje
     */
    public int deleteChild(int pos)
    {
        E child = this.children.get(pos);
        // Usuń dzieci, jeśli posiada
        if (child instanceof PropagatingListModel)
            ((PropagatingListModel)child).clearChildren();

        this.children.remove(pos);
        child.setParent(null);
        child.removePropertyChangeListener(this);
        PropertyChangeEvent evt = new PropertyChangeEvent(this, prefix + "." + DEL, child, null);
        this.propertyFirer.firePropertyChange(evt);
        return this.children.size();
    }

    public int deleteChild(E child)
    {
        int idx = this.getChildIdx(child);
        if (idx != -1)
            deleteChild(idx);
        return this.children.size();
    }

    /**
     * Metoda usuwa wszystkie dzieci na liście
     */
    public void clearChildren()
    {
        while (this.getNumberOfChildren() > 0)
            deleteChild(0);
    }

    /**
     * Metoda zwraca liczbę pomiarów w serii
     * @return liczba pomiarów w serii
     */
    public int getNumberOfChildren()
    {
        return this.children.size();
    }

    /**
     * Metoda zwraca indeks podanego pomiaru, lub -1, jeśli nie znaleziono
     * @param child poszukiwany pomiar
     * @return indeks poszukiwanego pomiaru, lub -1, jeśli nie znaleziono
     */
    public int getChildIdx(E child)
    {
        return this.children.indexOf(child);
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
