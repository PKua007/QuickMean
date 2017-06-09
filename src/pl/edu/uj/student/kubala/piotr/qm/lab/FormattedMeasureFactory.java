// QuickMean - FormattedMeasureFactory.java
//---------------------------------------------------------------------
// Fabryka sformatowanych pomiarów na podstawie ustalonego wzorca.
//---------------------------------------------------------------------
// Utworzono 18:22 09.06.2017 w IntelliJ IDEA
// (C)PKua, wszystkie prawa zastrzeżone
//---------------------------------------------------------------------

package pl.edu.uj.student.kubala.piotr.qm.lab;

public class FormattedMeasureFactory {
    private static final int DEFAULT_MIN_FIXED_EXPONENT = -3;
    private static final int DEFAULT_MAX_FIXED_EXPONENT = 3;
    private static final int DEFAULT_ERROR_SIGNIFICANT_DIGITS = 2;

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
     * @param measure pomiar do sformatowania
     * @return sformatowany pomiar
     */
    public FormattedMeasure format(Measure measure) {
        FormattedMeasure result = new FormattedMeasure();
        result.separateErrors = this.separateErrors;

        int valueSignif = getSignifOrZero(measure.getValue());
        int standardSignif = getSignifOrIntMin(measure.getStandardError());
        int maxSignif = getSignifOrIntMin(measure.getCalibrationError() + measure.getHumanError());
        int firstSignif = Math.max(standardSignif, maxSignif);      // pierwsza cyfra znacząca większej niepewności


        if (valueSignif > maxFixedExponent || valueSignif < minFixedExponent) {     // Przedstaw w postaci wykładniczej
            result.scientificForm = true;
            result.exponent = valueSignif;

            if (firstSignif > Integer.MIN_VALUE) {      // Którykolwiek z błedów niezerowy
                // Liczba cyfr po przecinku do wyświetlenia - taka, żeby była odpowiednia ilość cyfr znaczących większego
                // błędu
                int valueDotDigits = Math.max(0, valueSignif - firstSignif + this.errorSignificantDigits - 1);

                result.value = String.format("%." + valueDotDigits + "f",
                        measure.getValue() * Math.pow(10, -result.exponent));
                result.standardError = String.format("%." + valueDotDigits + "f",
                        measure.getStandardError() * Math.pow(10, -result.exponent));
                result.maxError = this.separateErrors
                        ? String.format("%." + valueDotDigits + "f",
                                (measure.getCalibrationError() + measure.getHumanError()) * Math.pow(10, -result.exponent))
                        : "";
            } else {        // Oba błedy zerowe - walnij maksymalna dokładność
                result.value = Double.toString(measure.getValue() * Math.pow(10, -result.exponent));
                result.standardError = "0";
                result.maxError = this.separateErrors ? "0" : "";
            }
        } else {        // Przedstaw w postaci "tradycyjnej"
            result.scientificForm = false;

            if (firstSignif > Integer.MIN_VALUE) {      // Którykolwiek z błedów niezerowy
                result.value = "a";
                result.standardError = "b";
                result.maxError = "c";
            } else {        // Oba błedy zerowe - walnij maksymalna dokładność
                result.value = Double.toString(measure.getValue());
                result.standardError = "0";
                result.maxError = this.separateErrors ? "0" : "";
            }
        }

        /*int digitDiff = valueSignif - uncertainitySignif + SIGNIFICANT_DIGITS;
        if (digitDiff < 0)  digitDiff = 0;
        return String.format("%." + digitDiff + "g += %." + SIGNIFICANT_DIGITS + "g", value, uncertainity);*/
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

        Measure measure = new Measure(67.435325, 0.564356, 0, 0);
        FormattedMeasure formattedMeasure = factory.format(measure);
        System.out.println(formattedMeasure);
    }
}