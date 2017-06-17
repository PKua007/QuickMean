// QuickMean - PropagatingModel.java
//---------------------------------------------------------------------
// Rozszerzenie modelu, które implementuje PropartyChangeListener
// i wyrzuca "w górę" odebrane zdarzenia
//---------------------------------------------------------------------
// Utworzono 13:12 28.05.2017 w IntelliJ IDEA
// (C)PKua, wszystkie prawa zastrzeżone
//---------------------------------------------------------------------

package pl.edu.uj.student.kubala.piotr.qm.lab;

import pl.edu.uj.student.kubala.piotr.qm.utils.Utils;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Objects;


//#############################################
// DO ZROBIENIA:
// - setParent
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
     * @throws IndexOutOfBoundsException jeśli index jest poza [-1, {@link Series#getNumberOfMeasures()}]
     * @throws IllegalArgumentException jeśli pomiar już jest w serii
     */
    protected final void addChild(E child, int index)
    {
        Objects.requireNonNull(child);
        if (this.children.indexOf(child) != -1) {
            throw new IllegalArgumentException(child.getClass().getSimpleName() + " jest już w " + this.getClass().getSimpleName());
        } if (index == -1) {
            this.children.add(child);
        } else {
            this.children.add(index, child);
        }

        //measure.setParentSeries(this);
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
    protected final void addChild(E child)
    {
        this.addChild(child, -1);
    }

    /**
     * Metoda pobiera pomiar
     * @param pos pozycja pomiaru
     * @throws IndexOutOfBoundsException, jeśli pos jest poza [0, {@link Series#getNumberOfMeasures() - 1}]
     * @return pomiar z podanej pozycji
     */
    protected final E getChild(int pos)
    {
        return this.children.get(pos);
    }

    /**
     * Metoda usuwa pomiar z listy po indeksie i ustawia w nim rodzica na null.
     * @param pos pozycja pomiaru
     * @return liczba pomiarów pozostałych po usunięciu
     * @throws IndexOutOfBoundsException jeśli element pod wskazanym indeksem nie istnieje
     */
    protected final int deleteChild(int pos)
    {
        E child = this.children.get(pos);
        // Usuń dzieci, jeśli posiada
        if (child instanceof PropagatingListModel)
            ((PropagatingListModel)child).clearChildren();

        this.children.remove(pos);
        //measure.setParentSeries(null);
        child.removePropertyChangeListener(this);
        PropertyChangeEvent evt = new PropertyChangeEvent(this, prefix + "." + DEL, child, null);
        this.propertyFirer.firePropertyChange(evt);
        return this.children.size();
    }

    protected final void clearChildren()
    {
        while (this.getNumberOfChildren() > 0)
            deleteChild(0);
    }

    /**
     * Metoda zwraca liczbę pomiarów w serii
     * @return liczba pomiarów w serii
     */
    protected final int getNumberOfChildren()
    {
        return this.children.size();
    }

    /**
     * Metoda zwraca indeks podanego pomiaru, lub -1, jeśli nie znaleziono
     * @param child poszukiwany pomiar
     * @return indeks poszukiwanego pomiaru, lub -1, jeśli nie znaleziono
     */
    protected int getChildIdx(E child)
    {
        return this.children.indexOf(child);
    }
}
