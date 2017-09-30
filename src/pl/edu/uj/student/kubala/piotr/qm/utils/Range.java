// QuickMean - Range.java
//---------------------------------------------------------------------
// [opis pliku]
//---------------------------------------------------------------------
// Utworzono 21:02 18.09.2017 w IntelliJ IDEA
// (C)PKua, wszystkie prawa zastrzeżone
//---------------------------------------------------------------------

package pl.edu.uj.student.kubala.piotr.qm.utils;

public class Range {
    private int beg;
    private int end;

    /**
     * Konstuktor inicjujący oba końce zerem
     */
    public Range() {

    }

    /**
     * Konstruktor inicjujący tą samą wartością oba końce
     *
     * @param value wartość do inicjacji
     */
    public Range(int value) {
        this.beg = this.end = value;
    }

    /**
     * Konstruktor inicjujący końce konkretnymi wartościami
     *
     * @param beg wartość początku
     * @param end wartość końca
     */
    public Range(int beg, int end) {
        this.beg = beg;
        this.end = end;
    }

    /**
     * Metoda wycina z przekazanego Stringa fragment wskazywany przez swoje indeksy (włącznie)
     *
     * @param text tekst do wycięcie
     * @return wycięty fragment
     */
    public String cutSubstringInclusive(CharSequence text) {
        return text.subSequence(getMin(), getMax() + 1).toString();
    }

    /**
     * Metoda wycina z przekazanego Stringa fragment wskazywany przez swoje indeksy (większy wyłącznie)
     *
     * @param text tekst do wycięcie
     * @return wycięty fragment
     */
    public String cutSubstringExclusive(CharSequence text) {
        return text.subSequence(getMin(), getMax()).toString();
    }

    /**
     * Zwraca koniec o mniejszej wartości
     *
     * @return koniec o mniejszej wartości
     */
    public int getMin() {
        return Math.min(this.beg, this.end);
    }

    /**
     * Zwraca koniec o większej wartości
     *
     * @return koniec o większej wartości
     */
    public int getMax() {
        return Math.max(this.beg, this.end);
    }

    /**
     * Zwraca true, jeśli zakres zawiera w całości drugi zakres
     *
     * @param range drugi zakres do sprawdzenia zawierania
     * @return true, jeśli zakres zawiera w całości drugi zakres
     */
    public boolean contains(Range range) {
        return contains(range.beg) && contains(range.end);
    }

    /**
     * Zwraca true, jeśli zakres zawiera podaną wartość
     *
     * @param value wartość do sprawdzenia
     * @return true, jeśli zakres zawiera podaną wartość
     */
    public boolean contains(int value) {
        return value >= getMin() && value <= getMax();
    }

    /**
     * Zwraca true, jeśli zakres przekrywa inny zakres
     *
     * @param other inny zakres
     * @return true, jeśli zakresy się przekrywają
     */
    public boolean overlaps(Range other) {
        return Math.min(getMax(), other.getMax()) >= Math.max(getMin(), other.getMin());
    }

    /**
     * Zwraca true, jeśli zakres dotyka (lub przekrywa) inny zakres
     *
     * @param other inny zakres
     * @return true, jeśli zakresy się przekrywają
     */
    public boolean touches(Range other) {
        return Math.min(getMax(), other.getMax()) >= Math.max(getMin(), other.getMin()) - 1;
    }

    /**
     * Zwraca zakres z odwróconymi końcami
     *
     * @return zakres z odwróconymi końcami
     */
    public Range invert() {
        return new Range(end, beg);
    }

    /**
     * Zwraca liczbę intów znajdujących się w zakresie
     *
     * @return długość zakresu
     */
    public int getLength() {
        return Math.abs(this.end - this.beg) + 1;
    }

    /**
     * Zwraca liczbę intów znajdujących się w zakresie. Końcówka nie jest zawarta
     *
     * @return długość zakresu
     */
    public int getLengthExclusive() {
        return Math.abs(this.end - this.beg);
    }

    public int getBeg() {
        return beg;
    }

    public int getEnd() {
        return end;
    }

    public Range shift(int difference) {
        return new Range(beg + difference, end + difference);
    }

    public static Range forText(CharSequence text) {
        if (text == null)
            return null;
        if (text.length() == 0)
            return new Range(-1);
        else
            return new Range(0, text.length() - 1);
    }

    public static Range forTextExclusive(CharSequence text) {
        if (text == null)
            return null;
        else
            return new Range(0, text.length());
    }

    @Override
    public String toString() {
        return "[" + beg + ", " + end + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Range range = (Range) o;

        return beg == range.beg && end == range.end;
    }

    @Override
    public int hashCode() {
        int result = beg;
        result = 31 * result + end;
        return result;
    }
}
