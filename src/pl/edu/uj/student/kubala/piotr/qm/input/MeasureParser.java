// QuickMean - MeasureParser.java
//---------------------------------------------------------------------
// [opis pliku]
//---------------------------------------------------------------------
// Utworzono 14:04 22.09.2017 w IntelliJ IDEA
// (C)PKua, wszystkie prawa zastrzeżone
//---------------------------------------------------------------------

package pl.edu.uj.student.kubala.piotr.qm.input;

import pl.edu.uj.student.kubala.piotr.qm.lab.Measure;
import pl.edu.uj.student.kubala.piotr.qm.utils.Range;

/**
 * Klasa służąca do parsowania pomiarów z tekstu, a także przedstawiania pomiarów w postaci tekstu w tym samym formacie.
 * Zwraca instancje klasy {@link ParsedMeasure}, króre zawierają zarówno tekstową reprezentację, jak i obiekt pomiaru.
 */
public class MeasureParser
{
    private static final char PLUS_MINUS = '±';

    private int index;
    private String text;

    /**
     * Konwertuje pomiar na Stringa w formacie przyjmowanym przez {@link MeasureParser#parseMeasure(String)}.
     * @param measure pomiar do stringizacji
     * @return {@link ParsedMeasure} zawierający przekazany pomiar i tekstową reprezentację
     */
    public ParsedMeasure printMeasure(Measure measure)
    {
        StringBuilder builder = new StringBuilder();

        builder.append(measure.getValue());
        Range valueRange = Range.forText(builder);

        // Dodaj tylko tyle niepewności, ile konieczne. Niewyszczególnione niepewności są uznawane za zerowe.
        // Kolejność podawania niepewności: kalibracji, człowieka, standardowa
        if (measure.getStandardError() != 0) {
            appendError(builder, measure.getCalibrationError());
            appendError(builder, measure.getHumanError());
            appendError(builder, measure.getStandardError());
        } else if (measure.getHumanError() != 0) {
            appendError(builder, measure.getCalibrationError());
            appendError(builder, measure.getHumanError());
        } else if (measure.getCalibrationError() != 0) {
            appendError(builder, measure.getCalibrationError());
        }

        return new ParsedMeasure(measure, builder.toString(), valueRange);
    }

    private static void appendError(StringBuilder builder, double calibrationError) {
        builder.append("±").append(calibrationError);
    }

    /**
     * Metoda dokonuje parsowania pomiaru postaci {@code 3.43±0.01±0.02±0.02}. Dowolna ilość niepewności może zostać
     * pominięta - uznawane są wtedy za zerowe. Wartość pomiaru musi być skończona, wartości niepewności skończone,
     * nieujemne.
     * @param text tekst pomiaru do sparsowania
     * @return {@link ParsedMeasure} zawierający sparsowany pomiar i przekazany tekst
     * @throws ParseException jeśli tekst nie spełnia założeń z opisu
     */
    public ParsedMeasure parseMeasure(String text) throws ParseException
    {
        if (text.isEmpty())
            throw new IllegalArgumentException("text is empty");

        // Zresetuj wewnętrzne wskaźniki
        this.text = text;
        index = 0;

        // Zjedz wartość
        Measure measure = new Measure();
        measure.setValue(eatValue());
        ParsedMeasure parsedMeasure = new ParsedMeasure(measure, text, new Range(0, index - 1));

        // Zjedz wszystkie błędy
        if (!hasNext()) return parsedMeasure;
        measure.setCalibrationError(eatError());
        if (!hasNext()) return parsedMeasure;
        measure.setHumanError(eatError());
        if (!hasNext()) return parsedMeasure;
        measure.setStandardError(eatError());

        // Upewnij się, że już nic nie ma po
        if (hasNext())
            throw new TooManyErrorsException(text, new Range(index, text.length() - 1));
        return parsedMeasure;
    }

    /* Pomocnicza metoda parsująca błąd aż do następnego plus-minus/końca i przesuwająca wewnętrzny wskaźnik */
    private double eatError() throws ParseException {
        index++;    // Pomiń plus-minus na początku błedu
        // Nieoczekiwany koniec tekstu zaraz po +-
        if (index >= text.length())
            throw new UnexpectedEndException(text, new Range(index - 1));
        int oldIdx = index;
        double value = eatValue();
        if (value < 0)
            throw new NegativeErrorException(text, new Range(oldIdx, index - 1));
        return value;
    }

    /* Pomocnicza metoda sprawdzająca, czy pozostało coś do sparsowania */
    private boolean hasNext() {
        return index < text.length();
    }

    /* Pomocnicza metoda parsująca następnego double'a aż do plus-minus/końca i przesuwająca wewnętrzny wskaźnik */
    private double eatValue() throws ParseException {
        int endIdx = nextPlusMinusIndex();   // Indeks znaku po końcu double'a
        if (endIdx == -1)
            endIdx = text.length();
        // Nieoczekiwany koniec - bieżący indeks na znaku plus-minus
        if (endIdx == index)
            throw new UnexpectedEndException(text, new Range(index));

        Range valueRange = new Range(index, endIdx - 1);
        double value;
        try {
            value = Double.parseDouble(valueRange.cutSubstringInclusive(text));
        } catch (NumberFormatException exception) {
            throw new MalformedDoubleException(text, valueRange);
        }
        // Nieskończony double - prawdopodobnie przekroczono zakres
        if (!Double.isFinite(value))
            throw new DoubleRangeException(text, valueRange);

        index = endIdx;     // Przesuń indeks
        return value;
    }

    /* Metoda pomocnicza zwracająca następny znak plus-minus, lub -1 */
    private int nextPlusMinusIndex() {
        return text.indexOf(PLUS_MINUS, index);
    }
}
