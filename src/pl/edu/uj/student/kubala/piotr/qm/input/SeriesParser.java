// QuickMean - MeasureParser.java
//---------------------------------------------------------------------
// [opis pliku]
//---------------------------------------------------------------------
// Utworzono 21:54 21.09.2017 w IntelliJ IDEA
// (C)PKua, wszystkie prawa zastrzeżone
//---------------------------------------------------------------------

package pl.edu.uj.student.kubala.piotr.qm.input;

import pl.edu.uj.student.kubala.piotr.qm.lab.Measure;

public class SeriesParser
{
    private String text;
    private int index;

    public SeriesParser() {
    }

    public SeriesInputInfo parseSeries(String text)
    {
        return parseSeries(text, -1, 0);
    }

    public SeriesInputInfo parseSeries(String text, int selectionIndex, int selectionLength)
    {
        SeriesInputInfo result = new SeriesInputInfo(text);

        this.text = text;
        this.index = 0;
        skipSeparators();

        MeasureParser measureParser = new MeasureParser();
        while (hasNext()) {
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
            result.addMeasureInfo(measureInputInfo);
            skipSeparators();
        }
        return result;
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
