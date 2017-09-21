// QuickMean - Range.java
//---------------------------------------------------------------------
// [opis pliku]
//---------------------------------------------------------------------
// Utworzono 21:02 18.09.2017 w IntelliJ IDEA
// (C)PKua, wszystkie prawa zastrzeżone
//---------------------------------------------------------------------

package pl.edu.uj.student.kubala.piotr.qm.utils;

public class Range
{
    public int beg;
    public int end;

    /**
     * Konstuktor inicjujący oba końce zerem
     */
    public Range() {

    }

    /**
     * Konstruktor inicjujący tą samą wartością oba końce
     * @param value wartość do inicjacji
     */
    public Range(int value) {
        this.beg = this.end = value;
    }

    /**
     * Konstruktor inicjujący końce konkretnymi wartościami
     * @param beg wartość początku
     * @param end wartość końca
     */
    public Range(int beg, int end) {
        this.beg = beg;
        this.end = end;
    }

    /**
     * Zwraca koniec o mniejszej wartości
     * @return koniec o mniejszej wartości
     */
    public int getMin() {
        return Math.min(this.beg, this.end);
    }

    /**
     * Zwraca koniec o większej wartości
     * @return koniec o większej wartości
     */
    public int getMax() { return Math.max(this.beg, this.end); }

    /**
     * Zwraca true, jeśli zakres przekrywa inny zakres
     * @param other inny zakres
     * @return true, jeśli zakresy się przekrywają
     */
    public boolean overlaps(Range other)
    {
        return Math.min(getMax(), other.getMax()) >= Math.max(getMin(), other.getMin());
    }

    public Range invert()
    {
        return new Range(end, beg);
    }

    public int getLength() {
        return Math.abs(this.end - this.beg);
    }
}
