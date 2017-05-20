// QuickMean - Utils.java
//---------------------------------------------------------------------
// Klasa z przydatnymi metodami.
//---------------------------------------------------------------------
// Utworzono 17:23 17.05.2017 w IntelliJ IDEA
// (C)PKua, wszystkie prawa zastrzeżone
//---------------------------------------------------------------------

package pl.edu.uj.student.kubala.piotr.qm.utils;

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
}
