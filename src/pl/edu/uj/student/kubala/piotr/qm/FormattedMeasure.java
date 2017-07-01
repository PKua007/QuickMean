// QuickMean - FormattedMeasure.java
//---------------------------------------------------------------------
// Klasa formatująca pomiar na podstawie jego niepewności.
// Wybiera
//---------------------------------------------------------------------
// Utworzono 18:03 09.06.2017 w IntelliJ IDEA
// (C)PKua, wszystkie prawa zastrzeżone
//---------------------------------------------------------------------

package pl.edu.uj.student.kubala.piotr.qm;

public class FormattedMeasure implements Cloneable {
    /**
     * Znak plus minus - przydatne przy wyświetlaniu
     */
    public static final char PLUS_MINUS = '±';
    /**
     * Znak plus minus ze spacjami - przydatne przy wyświetlaniu
     */
    public static final String PLUS_MINUS_SPACE = " ± ";
    /**
     * Znak x
     */
    public static final char CROSS = '×';
    private static final String CROSS_SPACE = " × ";


    /**
     * Sformatowana wartość pomiaru
     */
    public String value;
    /**
     * Sformatowany bład standardowy
     */
    public String standardError;
    /**
     * Sformatowany błąd maksymalny
     */
    public String maxError;
    /**
     * Czy postać wykładnicza
     */
    public boolean scientificForm;
    /**
     * Wykładnik, jeśli postać wykładnicza
     */
    public int exponent;
    /**
     * Czy rozdzielać niepewności?
     */
    public boolean separateErrors;

    public FormattedMeasure(String value, String standardError, String maxError, boolean scientificForm, int exponent) {
        this.value = value;
        this.standardError = standardError;
        this.maxError = maxError;
        this.scientificForm = scientificForm;
        this.exponent = exponent;
    }

    public FormattedMeasure() {

    }

    /**
     * Zwraca reprezentację Stringową postaci (3.06 += 0.56 += 0.40) * 10^5
     * @return reprezentacja Stringowa
     */
    @Override
    public String toString()
    {
        String body;
        if (separateErrors)
            body = value + PLUS_MINUS_SPACE + standardError + PLUS_MINUS_SPACE + maxError;
        else
            body = value + PLUS_MINUS_SPACE + standardError;
        if (scientificForm)
            return "(" + body + ") * 10^" + exponent;
        else
            return body;
    }

    /**
     * Zwraca sformatowaną (z ładnym wykładnikiem) Stringową posta (3.06 += 0.56 += 0.40) * 10^5. Wykładnik nie wystaje
     * poza linię tekstu
     * @return sformatowana reprezentacja Stringowa
     */
    public String toHTMLCompact()
    {
        String body;
        if (separateErrors)
            body = value + PLUS_MINUS_SPACE + standardError + PLUS_MINUS_SPACE + maxError;
        else
            body = value + PLUS_MINUS_SPACE + standardError;
        if (scientificForm)
            return "<html><span style='white-space: nowrap;'>(" + body + ")" + CROSS + "10<sup style='font-size: 80%;'>" + exponent + "</sup></span></html>";
        else
            return "<html><span style='white-space: nowrap;'>" + body + "</span></html>";
    }

    /**
     * Zwraca sformatowaną (z ładnym wykładnikiem) Stringową posta (3.06 += 0.56 += 0.40) * 10^5. Wykładnik wystaje
     * poza linię tekstu
     * @return sformatowana reprezentacja Stringowa
     */
    public String toHTML()
    {
        String body;
        if (separateErrors)
            body = value + PLUS_MINUS_SPACE + standardError + PLUS_MINUS_SPACE + maxError;
        else
            body = value + PLUS_MINUS_SPACE + standardError;
        if (scientificForm)
            return "<html><span style='white-space: nowrap;'>(" + body + ")" + CROSS + "10<sup>" + exponent + "</sup></span></html>";
        else
            return "<html><span style='white-space: nowrap;'>" + body + "</span></html>";
    }

    @Override
    public Object clone()
    {
        try {
            // Płytka kopia, żadnych referencji do mutualnego typu
            return super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
