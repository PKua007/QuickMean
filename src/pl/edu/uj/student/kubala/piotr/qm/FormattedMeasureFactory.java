// QuickMean - FormattedMeasureFactory.java
//---------------------------------------------------------------------
// Fabryka sformatowanych pomiarów na podstawie ustalonego wzorca.
//---------------------------------------------------------------------
// Utworzono 18:22 09.06.2017 w IntelliJ IDEA
// (C)PKua, wszystkie prawa zastrzeżone
//---------------------------------------------------------------------

package pl.edu.uj.student.kubala.piotr.qm;

import pl.edu.uj.student.kubala.piotr.qm.lab.Quantity;

import java.util.Locale;

public class FormattedMeasureFactory {
    private static final int DEFAULT_MIN_FIXED_EXPONENT = -3;
    private static final int DEFAULT_MAX_FIXED_EXPONENT = 3;
    private static final int DEFAULT_ERROR_SIGNIFICANT_DIGITS = 2;
    private static final Locale ENGLISH_LOCALE = Locale.ENGLISH;

    private int minFixedExponent;       // Minimalny wykładnik, dla której zostaje postać zwykła
    private int maxFixedExponent;       // Maksymalny wykładnik, dla którego zostaje postać zwykła
    private int errorSignificantDigits; // Ilość cyfr znaczących w błędzie
    private boolean separateErrors;     // Czy rozdzielać niepewności

    /**
     * Konstruktor inicjujący domyślne wartości
     */
    public FormattedMeasureFactory() {
        this(DEFAULT_MIN_FIXED_EXPONENT, DEFAULT_MAX_FIXED_EXPONENT, DEFAULT_ERROR_SIGNIFICANT_DIGITS, false);
    }

    /**
     * Konstruktor przyjmujący wszystkie parametry
     *
     * @param minFixedExponent       minimalny wykładnik, dla której zostaje postać zwykła
     * @param maxFixedExponent       maksymalny wykładnik, dla którego zostaje postać zwykła
     * @param errorSignificantDigits ilość cyfr znaczących w błędzie
     * @param separateErrors         czy rozdzielać niepewności
     */
    public FormattedMeasureFactory(int minFixedExponent, int maxFixedExponent, int errorSignificantDigits, boolean separateErrors) {
        this.minFixedExponent = minFixedExponent;
        this.maxFixedExponent = maxFixedExponent;
        this.errorSignificantDigits = errorSignificantDigits;
        this.separateErrors = separateErrors;
    }

    /* Gettery i settery */

    public int getMinFixedExponent() {
        return minFixedExponent;
    }

    public void setMinFixedExponent(int minFixedExponent) {
        this.minFixedExponent = minFixedExponent;
    }

    public int getMaxFixedExponent() {
        return maxFixedExponent;
    }

    public void setMaxFixedExponent(int maxFixedExponent) {
        this.maxFixedExponent = maxFixedExponent;
    }

    public int getErrorSignificantDigits() {
        return errorSignificantDigits;
    }

    public void setErrorSignificantDigits(int errorSignificantDigits) {
        this.errorSignificantDigits = errorSignificantDigits;
    }

    public boolean isSeparateErrors() {
        return separateErrors;
    }

    public void setSeparateErrors(boolean separateErrors) {
        this.separateErrors = separateErrors;
    }

    /**
     * Formatuje pomiar na podstawie ustalonych parametrów.
     *
     * @param quantity wielkość do sformatowania
     * @return sformatowany pomiar
     */
    public FormattedMeasure format(Quantity quantity) {
        FormattedMeasure result = new FormattedMeasure();
        result.separateErrors = this.separateErrors;

        int valueSignif = getSignifOrZero(quantity.getValue());
        int standardSignif = getSignifOrIntMin(quantity.getStandardError());
        int maxSignif = getSignifOrIntMin(quantity.getMaxError());
        int firstSignif = Math.max(standardSignif, maxSignif);      // pierwsza cyfra znacząca większej niepewności


        if (valueSignif > maxFixedExponent || valueSignif < minFixedExponent) {     // Przedstaw w postaci wykładniczej
            result.scientificForm = true;
            result.exponent = valueSignif;

            if (firstSignif > Integer.MIN_VALUE) {      // Którykolwiek z błedów niezerowy
                // Liczba cyfr po przecinku do wyświetlenia - taka, żeby była odpowiednia ilość cyfr znaczących większego
                // błędu
                int valueDotDigits = Math.max(0, valueSignif - firstSignif + this.errorSignificantDigits - 1);
                double factor = Math.pow(10, -result.exponent);

                result.value = String.format(ENGLISH_LOCALE, "%." + valueDotDigits + "f", quantity.getValue() * factor);
                result.standardError = String.format(ENGLISH_LOCALE, "%." + valueDotDigits + "f", quantity.getStandardError() * factor);
                result.maxError = this.separateErrors
                        ? String.format(ENGLISH_LOCALE, "%." + valueDotDigits + "f", quantity.getMaxError() * factor)
                        : "";
            } else {        // Oba błedy zerowe - walnij maksymalna dokładność
                result.value = Double.toString(quantity.getValue() * Math.pow(10, -result.exponent));
                result.standardError = "0.0";
                result.maxError = this.separateErrors ? "0.0" : "";
            }
        } else {        // Przedstaw w postaci "tradycyjnej"
            result.scientificForm = false;

            if (firstSignif > Integer.MIN_VALUE) {      // Którykolwiek z błedów niezerowy
                // Pozycja ostatniej niezerowej cyfry - normalnie ostatnia cyfra niepewności, czhyba że niepewność
                // większa niż wynik, to pierwsa cyfra wyniku
                int valueLastDigit = Math.min(valueSignif, firstSignif - this.errorSignificantDigits + 1);
                if (valueLastDigit < 0) {  // Występuje część ułamkowa, bądź kończy się na jednościach
                    result.value = String.format(ENGLISH_LOCALE, "%." + (-valueLastDigit) + "f", quantity.getValue());
                    result.standardError = String.format(ENGLISH_LOCALE, "%." + (-valueLastDigit) + "f", quantity.getStandardError());
                    result.maxError = this.separateErrors
                            ? String.format(ENGLISH_LOCALE, "%." + (-valueLastDigit) + "f", quantity.getMaxError())
                            : "";
                } else {    // Ostatnia cyfra to cyfra dziesiątek, setek, itd. Trzeba dopisać ileś zer
                    String trailingZeros = new String(new char[valueLastDigit]).replace('\0', '0');
                    double factor = Math.pow(10, -valueLastDigit);

                    result.value = String.format(ENGLISH_LOCALE, "%.0f%s", quantity.getValue() * factor, trailingZeros);
                    result.standardError = String.format(ENGLISH_LOCALE, "%.0f%s", quantity.getStandardError() * factor, trailingZeros);
                    result.maxError = this.separateErrors
                            ? String.format(ENGLISH_LOCALE, "%.0f%s", quantity.getMaxError() * factor, trailingZeros)
                            : "";
                }
            } else {        // Oba błedy zerowe - walnij maksymalną dokładność
                result.value = Double.toString(quantity.getValue());
                result.standardError = "0.0";
                result.maxError = this.separateErrors ? "0.0" : "";
            }
        }

        return result;
    }

    /* Zwraca pozycję pierwszej znaczącej cyfry, lub zero, jeśli wartość to 0 */
    private int getSignifOrZero(double value) {
        if (value == 0)
            return 0;
        else
            return (int) Math.floor(Math.log10(value));
    }

    /* Zwraca pozycję pierwszej znaczącej cyfry, lub minimalnego inta, jeśli wartość to 0 */
    private int getSignifOrIntMin(double value) {
        if (value == 0)
            return Integer.MIN_VALUE;
        else
            return (int) Math.floor(Math.log10(value));
    }

    public static void main(String[] args)
    {
        FormattedMeasureFactory factory = new FormattedMeasureFactory();
        factory.setSeparateErrors(true);

        Quantity quantity = new Quantity(456.234, 4.5003, 12.023);
        FormattedMeasure formattedMeasure = factory.format(quantity);
        System.out.println(formattedMeasure);
    }
}