// QuickMean - MeasureParser.java
//---------------------------------------------------------------------
// [opis pliku]
//---------------------------------------------------------------------
// Utworzono 21:54 21.09.2017 w IntelliJ IDEA
// (C)PKua, wszystkie prawa zastrzeżone
//---------------------------------------------------------------------

package pl.edu.uj.student.kubala.piotr.qm.input;

import pl.edu.uj.student.kubala.piotr.qm.utils.Range;

/**
 * <p>Klasa służąca do parsowania serii pomiarów z tekstu, a także przedstawiania serii w postaci tekstu w tym samym
 * formacie - pomiary w formacie przyjmowanym przez {@link MeasureParser} rozdzielone <quote>; </quote>:
 * {@code 30.3±0.4; 70.3; 60±1; }
 * <p>Pozwala także na dowolną kombinację dowolnej liczby spacji i średników jako rozdzielenie oraz na początku i na
 * końcu. Zapewnia to większą odporność na błędy.
 * <p>Umożliwia także wygenerowanie pełnego {@link SeriesInputInfo} z istniejącej serii - powstały tekst jest podanym
 * wyżej formacie.
 */
public class SeriesParser
{
    private String text;
    private int index;

    static class SelectionRangeBuilder
    {
        private Range textSelectionRange;

        private int seriesInputInfoIndex = 0;
        private int selectionRangeStart = -1;
        private int selectionRangeEnd = -1;

        public SelectionRangeBuilder(int selectionIndex, int selectionLength) {
            if (selectionIndex == -1 || selectionLength == 0)
                textSelectionRange = null;
            else
                textSelectionRange = new Range(selectionIndex, selectionIndex + selectionLength - 1);
        }

        public void checkNextMeasureInfo(MeasureInputInfo measureInputInfo) {
            if (textSelectionRange == null)
                return;

            if (textSelectionRange.overlaps(measureInputInfo.getTextRange())) {
                if (!isSelectionBeingBuilt())       // Napotkano na pierwszy pomiar w zaznaczeniu - zacznij budować
                    selectionRangeStart = seriesInputInfoIndex;
            } else if (isSelectionBeingBuilt()) {   // Napotkano na pierwszy pomiar poza zaznaczeniem - przestań budować
                selectionRangeEnd = seriesInputInfoIndex - 1;
            }
            seriesInputInfoIndex++;
        }

        private boolean isSelectionBeingBuilt() {
            return selectionRangeStart != -1 && selectionRangeEnd == -1;
        }

        public Range getRange() {
            if (selectionRangeStart == -1)      // Nie wybudowano zaznaczenia
                return null;
            else if (selectionRangeEnd == -1)   // Zaczęto budować, ale nie zakończono - koniec na ostatnim pomiarze
                return new Range(selectionRangeStart, seriesInputInfoIndex - 1);
            else                                // Zaznaczenie w całości zbudowane
                return new Range(selectionRangeStart, selectionRangeEnd);
        }
    }

    public SeriesParser() {
    }

    /**
     * Parsuje tekst reprezentujący serię pomiarów rozdzielonych <quote>; </quote> (jak w opisie klasy). Zwraca instncję
     * {@link SeriesInputInfo} zawierającą sparsowane pomiary, a także informację o ich położeniu w tekście i miejscach,
     * które nie mogły zostać sparsowane. Zakłada brak zaznaczenia w tekście.
     * @param text tekst do sparsowania
     * @return {@link SeriesInputInfo} opisujący wyniki parsowania
     */
    public SeriesInputInfo parseSeries(String text)
    {
        return parseSeries(text, -1, 0);
    }

    /**
     * Parsuje tekst reprezentujący serię pomiarów rozdzielonych <quote>; </quote> (jak w opisie klasy). Zwraca instncję
     * {@link SeriesInputInfo} zawierającą sparsowane pomiary, a także informację o ich położeniu w tekście i miejscach,
     * które nie mogły zostać sparsowane. W zwróconej informacji znajduje się zakres pomiarów, które znalazły się
     * częściowo w zaznaczeniu.
     * @param text tekst do sparsowania
     * @param selectionIndex indeks początku zaznaczenia (-1 jeśli brak)
     * @param selectionLength długość zaznaczenia (0 jeśli brak)
     * @return {@link SeriesInputInfo} opisujący wyniki parsowania
     */
    public SeriesInputInfo parseSeries(String text, int selectionIndex, int selectionLength)
    {
        validateSelection(text, selectionIndex, selectionLength);

        SeriesInputInfo result = new SeriesInputInfo(text);
        MeasureParser measureParser = new MeasureParser();
        SelectionRangeBuilder selectionRangeBuilder = new SelectionRangeBuilder(selectionIndex, selectionLength);

        // Zresetuj do parsowanai nowego tekstu
        this.text = text;
        this.index = 0;

        skipSeparators();       // Pomiń separatory na początku
        while (hasNext()) {     // Pobieraj kolejne pomiary, aż do końca
            int measureStartIdx = index;
            String measureText = eatMeasure();
            MeasureInputInfo measureInputInfo;
            try {
                ParsedMeasure parsedMeasure = measureParser.parseMeasure(measureText);
                measureInputInfo = MeasureInputInfo.createCorrect(parsedMeasure, measureStartIdx);
            } catch (ParseException e) {
                measureInputInfo = MeasureInputInfo.createIncorrect(
                        measureStartIdx, measureText.length(), e.getMalformedRange());
            }
            selectionRangeBuilder.checkNextMeasureInfo(measureInputInfo);
            result.addMeasureInfo(measureInputInfo);
            skipSeparators();   // Pomiń separatory po pomiarze
        }
        result.setMeasuresInSelection(selectionRangeBuilder.getRange());
        return result;
    }

    private static void validateSelection(String text, int selectionIndex, int selectionLength) {
        if (selectionLength < 0)
            throw new IllegalArgumentException("selectionLength");
        if (selectionIndex < -1 || selectionIndex >= text.length() || selectionIndex + selectionLength > text.length())
            throw new StringIndexOutOfBoundsException("Selection exceeds string range");
    }

    private String eatMeasure() {
        int measureStartIdx = index;
        skipMeasure();
        return text.substring(measureStartIdx, index);
    }

    private boolean hasNext() {
        return index < text.length();
    }

    private void skipSeparators() {
        while (index < text.length() && isIndexOnSeparator())
            index++;
    }

    private void skipMeasure() {

        while (index < text.length() && !isIndexOnSeparator())
            index++;
    }

    private boolean isIndexOnSeparator() {
        return text.charAt(index) == ' ' || text.charAt(index) == ';';
    }
}
