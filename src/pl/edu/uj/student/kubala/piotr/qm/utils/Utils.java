// QuickMean - Utils.java
//---------------------------------------------------------------------
// Klasa z przydatnymi metodami.
//---------------------------------------------------------------------
// Utworzono 17:23 17.05.2017 w IntelliJ IDEA
// (C)PKua, wszystkie prawa zastrzeżone
//---------------------------------------------------------------------

package pl.edu.uj.student.kubala.piotr.qm.utils;

import pl.edu.uj.student.kubala.piotr.qm.Main;

import javax.swing.*;
import java.util.ArrayList;
import java.util.ListIterator;

public class Utils
{

    /**
     * Usuwa wszystkie wystąpienia elementu o podanym indeksie z listy {@code list} i zmniejsza o jeden indeksy
     * od niego większe. Funkcja przydatna, jeśli przechowujemy na liście indeksy elementów w tablicy/kolekcji, jeden
     * z elementów jest usuwamy i trzeba zaktualizować indeksy w {@code list}.
     * @param index indeks usuwanego elementu
     * @param list lista indeków
     */
    public static void removeElementFromIndicesList(int index, ArrayList<Integer> list)
    {
        if (index == -1)
            return;

        ListIterator<Integer> it = list.listIterator();
        Integer value;
        while(it.hasNext()) {
            value = it.next();
            if (value == index)
                it.remove();
            else if (value > index)
                it.set(value - 1);
        }
    }

    /**
     * Aktualizuje indeksy na liście  {@code list} po dodaniu nowego elementu do wyjściowej listy - zwiększa o jeden
     * wszystkie indeksy większe lub równe niż podany. Funkcja przydatna, jeśli przechowujemy na liście indeksy
     * elementów w kolekcji, a zostanie dodany nowy element i trzeba  indeksy w {@code list}.
     * @param index indeks na wyjściowej liście, na który został wstawiony nowy element
     * @param list lista indeksów
     */
    public static void shiftIndicesAfterAddition(int index, ArrayList<Integer> list)
    {
        if (index == -1)
            return;

        ListIterator<Integer> it = list.listIterator();
        Integer value;
        while(it.hasNext()) {
            value = it.next();
            if (value >= index)
                it.set(value + 1);
        }
    }

    /**
     * Kopiuje parametry jednej akcji do drugiej. Przydatne np. przy ustawianiu akcji już istniejącego przycisku z
     * nazwą, mnemonikiem, itd.
     * @param source źródło wartości kluczy
     * @param dest cel do skopiowania
     */
    public static void copyButtonAction(AbstractButton source, Action dest)
    {
        dest.putValue(Action.ACTION_COMMAND_KEY, source.getActionCommand());
        dest.putValue(Action.MNEMONIC_KEY, source.getMnemonic());
        dest.putValue(Action.NAME, source.getText());
        dest.putValue(Action.SHORT_DESCRIPTION, source.getToolTipText());
        dest.putValue(Action.SMALL_ICON, source.getIcon());
        dest.putValue("enabled", source.isEnabled());
    }
}
