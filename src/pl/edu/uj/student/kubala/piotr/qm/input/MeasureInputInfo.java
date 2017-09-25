// QuickMean - ParsedMeasureInfo.java
//---------------------------------------------------------------------
// [opis pliku]
//---------------------------------------------------------------------
// Utworzono 21:54 21.09.2017 w IntelliJ IDEA
// (C)PKua, wszystkie prawa zastrzeżone
//---------------------------------------------------------------------

package pl.edu.uj.student.kubala.piotr.qm.input;

import pl.edu.uj.student.kubala.piotr.qm.lab.Measure;
import pl.edu.uj.student.kubala.piotr.qm.utils.Range;

import java.util.Objects;

/**
 * Klasa opisująca informację o pomiarze w serii w okienku pomiarów. Obiekty składowane przez {@link SeriesInputInfo}).
 * Opisuje położenie w tekście, czy pomiar jest poprawny, zakres wartości, indeks wystąpienia błędu.
 */
public class MeasureInputInfo
{
    private Measure measure;
    private Range textRange;
    private Range valueRange;
    private Range errorRange;
    private boolean correct;

    private MeasureInputInfo(Measure measure, Range textRange, Range valueRange, Range errorRange, boolean correct) {
        this.measure = measure;
        this.textRange = textRange;
        this.valueRange = valueRange;
        this.errorRange = errorRange;
        this.correct = correct;
    }

    /**
     * Tworzy informację o prawidłowo sparsowanym pomiarze. Zawiera informacje o położeniu pomiaru we fragmencie tekstu,
     * sparsowany pomiar i zakres tekstu, w którym się znajduje wartość pomiaru (reszta to niepewności). Zakres błędu
     * ustawiony na null.
     * @param measure prawidłowo sparsowany pomiar
     * @param textRange zakres całego tekstu, w którym leży pomiar
     * @param valueRange zakres całego tekstu, w którym jest wartość pomiaru
     * @return {@link MeasureInputInfo} zawierający wymienione informacje
     */
    public static MeasureInputInfo createCorrect(Measure measure, Range textRange, Range valueRange)
    {
        Objects.requireNonNull(valueRange);
        if (textRange.getMin() < 0)
            throw new StringIndexOutOfBoundsException("textRange in negative integer semi-axis: " + textRange);
        if (!textRange.contains(valueRange))
            throw new IllegalArgumentException("valueRange " + valueRange + " not in textRange " + textRange);

        Objects.requireNonNull(measure);
        return new MeasureInputInfo(measure, textRange, valueRange, null, true);
    }

    /**
     * Tworzy informację o prawidłowo sparsowanym pomiarze ze sparsowanego pomiaru {@link ParsedMeasure}. Przyjmuje
     * indeks w tekście, w którym się zaczyna pomiar. Resztę potrzebnych informacji pobiera z {@code parsedMeasure}.
     * Zakres błędu ustawiony na null.
     * @param parsedMeasure sparsowany pomiar z dodaktowymi informacjami
     * @param textIdx indeks w tekście, w którym się zaczyna pomiar
     * @return {@link MeasureInputInfo} zawierające wymienione informacje
     */
    public static MeasureInputInfo createCorrect(ParsedMeasure parsedMeasure, int textIdx)
    {
        if (textIdx < 0)
            throw new StringIndexOutOfBoundsException(textIdx);
        Range textRange = Range.forText(parsedMeasure.getText()).shift(textIdx);
        Range valueRange = parsedMeasure.getValueRange().shift(textIdx);
        return createCorrect(parsedMeasure.getMeasure(), textRange, valueRange);
    }

    /**
     * Tworzy informację o nieprawidłowo sparsowanym pomiarze. Zawiera informacje o położeniu pomiaru we fragmencie
     * tekstu i zakres napotkania błędu. Pomiar i zakres wartości ustawiane są na null.
     * @param textRange zakres całego tekstu, w którym leży pomiar
     * @param errorRange zakres całego tekstu, który jest błędny
     * @return {@link MeasureInputInfo} zawierający wymienione informacje
     */
    public static MeasureInputInfo createIncorrect(Range textRange, Range errorRange)
    {
        if (textRange.getMin() < 0)
            throw new StringIndexOutOfBoundsException("textRange in negative integer semi-axis: " + textRange);
        if (!textRange.contains(errorRange))
            throw new IllegalArgumentException("errorRange " + errorRange + " not in textRange " + textRange);
        return new MeasureInputInfo(null, textRange, null, errorRange, false);
    }

    /**
     * Tworzy informację o nieprawidłowo sparsowanym pomiarze. Przyjmuje początkowy indeks błędu w tekście, długość
     * tekstu i zakres błędu względem tego indeksu - jest on przesuwany do zakresu bezwzględnego. Pomiar i zakres
     * wartości ustawiane są na null
     * @param textIdx indeks w tekście początku źle sformatowanego pomiaru
     * @param measureTextLength długość tekstu pomiaru
     * @param relativeErrorRange zakres błędu względem początkowego indeksu
     * @return {@link MeasureInputInfo} zawierający wymienione informacje
     */
    public static MeasureInputInfo createIncorrect(int textIdx, int measureTextLength, Range relativeErrorRange)
    {
        if (measureTextLength <= 0)
            throw new IllegalArgumentException("measureTextLength <= 0: " + measureTextLength);
        if (textIdx < 0)
            throw new StringIndexOutOfBoundsException(textIdx);
        Range textRange = new Range(textIdx, textIdx + measureTextLength - 1);
        Range errorRange = relativeErrorRange.shift(textIdx);
        if (!textRange.contains(errorRange))
            throw new IllegalArgumentException("relativeErrorRange " + relativeErrorRange + " not in valid range "
                    + textRange.shift(-textIdx));
        return createIncorrect(textRange, errorRange);
    }

    public Measure getMeasure() {
        return measure;
    }

    public Range getTextRange() {
        return textRange;
    }

    public Range getValueRange() {
        return valueRange;
    }

    public Range getErrorRange() {
        return errorRange;
    }

    public boolean isCorrect() {
        return correct;
    }
}
