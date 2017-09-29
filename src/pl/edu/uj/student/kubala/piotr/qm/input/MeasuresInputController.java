// QuickMean - MeasuresInputController.java
//---------------------------------------------------------------------
// Kontroler okna z wprowadzonymi pomiarami do średniej.
//---------------------------------------------------------------------
// Utworzono 20:40 02.05.2017 w IntelliJ IDEA
// (C)PKua, wszystkie prawa zastrzeżone
//---------------------------------------------------------------------

package pl.edu.uj.student.kubala.piotr.qm.input;

import pl.edu.uj.student.kubala.piotr.qm.Controller;
import pl.edu.uj.student.kubala.piotr.qm.EDTInitializationManager;
import pl.edu.uj.student.kubala.piotr.qm.lab.LabProject;
import pl.edu.uj.student.kubala.piotr.qm.lab.Series;
import pl.edu.uj.student.kubala.piotr.qm.utils.Range;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;
import java.util.regex.Pattern;

public class MeasuresInputController implements Controller
{
    private LabProject      labProject;
    private MeasuresInput   measuresInput;

    /**
     * Konstruktor kontrolera okna z pomiarami przyjmujący model i widok
     * @param labProject model laboratorium
     * @param measuresInput widok okna z pomiarami
     */
    public MeasuresInputController(LabProject labProject, MeasuresInput measuresInput) {
        this.labProject = labProject;
        this.measuresInput = measuresInput;

        EDTInitializationManager manager = EDTInitializationManager.getInstance();
        manager.registerElement(this);
        manager.addDependency(this, measuresInput);
    }

    @Override
    public void init() {
        JEditorPane editor = this.measuresInput.getInputPane();
        AbstractDocument doc = (AbstractDocument) editor.getDocument();
        doc.setDocumentFilter(new MeasureDocumentFilter(measuresInput.getInputPane()));

        Handler handler = new Handler();
        //this.labProject.addPropertyChangeListener(handler);
        //editor.getCaret().addChangeListener(handler);
        //editor.addFocusListener(handler);
        editor.addCaretListener(handler);
    }

    @Override
    public String getEDTInitializableName() {
        return "MeasuresInputController";
    }

    public void reparseText()
    {
        SeriesParser parser = new SeriesParser();
        String text = measuresInput.getInputPane().getText();
        SeriesInputInfo seriesInputInfo = parser.parseSeries(text);
        measuresInput.setSeriesInputInfo(seriesInputInfo);

        Series highlightedSeries = labProject.getHighlightedSeries();
        if (highlightedSeries == null)
            return;
        highlightedSeries.clear();
        for (MeasureInputInfo measureInputInfo : seriesInputInfo.getAllInfos())
            if (measureInputInfo.isCorrect())
                highlightedSeries.addElement(measureInputInfo.getMeasure());
        highlightedSeries.updateMean();
    }

    private class Handler implements CaretListener
    {
        @Override
        public void caretUpdate(CaretEvent e) {
            SwingUtilities.invokeLater(measuresInput::highlightInputPane);
        }
    }

    /**
     * Filtr dokumentu obługujący inteligentne wpisywanie pomiarów w formacie przyjmowanym przez
     * {@link SeriesParser}. Jego zachowanie w skrócie:
     * <ul>
     *     <li>wpisanie "," lub "." powoduje zamianę na "."</li>
     *     <li>wpisanie "+-" powoduje zamianę na "±"</li>
     *     <li>wciśnięcie " " lub ";" powoduje wstawienie pełnego odstępu "; "</li>
     *     <li>wstawianie drugiego pełnego odstępu powoduje jedynie przesunięcie karety</li>
     *     <li>wklejany tekst również jest przetwarzany tak, jak poprzednie podpkunty. Wynik wklejenia / wycięcia /
     *     usunięcia zaznaczenia również również zachowa format</li>
     *     <li>zmazanie pojedynczego znaku odstępu scala sąsiednie pomiary</li>
     *     <li>również odstępy powstałe na brzegach wklejenia/wycięcia są w intuicyjny sposób przetwarzane</li>
     *     <li>odstęp nie może się znaleźć na początku okna. Na końcu może być wstawiony, ale nie musi</li>
     *     <li>wstawienie nowego pomiaru między ";" i " " zamienia rozdzielony pełny odstęp na 2 pełne odstępy</li>
     *     <li>zmazanie całego pomiaru powoduje scalenie dwóch pełnych odstępów w 1 pełny odstęp</li>
     * </ul>
     */
    public static class MeasureDocumentFilter extends DocumentFilter
    {
        private final Pattern normalizationPattern = Pattern.compile("[; ]+");

        private JTextPane pane;

        public MeasureDocumentFilter(JTextPane pane) {
            this.pane = pane;
        }

        @Override
        public void remove(FilterBypass fb, int offset, int length) throws BadLocationException
        {
            replace(fb, offset, length, "", null);
        }

        @Override
        public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException
        {
            replace(fb, offset, 0, string, attr);
        }

        @Override
        public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
            Document document = fb.getDocument();
            String originalText = document.getText(0, document.getLength());

            // Inserting minus preceded by plus - change to plus-minus
            if (length == 0 && "-".equals(text) && offset > 0 && originalText.charAt(offset - 1) == '+') {
                fb.replace(offset - 1, 1, "±", attrs);
                return;
            }

            StringBuilder replaceSimulation = new StringBuilder(originalText);
            String initialReplace = selectionTouchesSpaces(originalText, offset, length)
                    ? normalizeSpaces(" " + text + " ")
                    : normalizeSpaces(text);
            replaceSimulation.replace(offset, offset + length, initialReplace);

            Range toReplaceRange = getExpandedOnSpacesRange(replaceSimulation, offset, initialReplace.length());
            String finalReplace = toReplaceRange.cutSubstringExclusive(replaceSimulation);
            finalReplace = normalizeSurroundingSpaces(finalReplace, offset);
            finalReplace = replaceCommas(finalReplace);
            finalReplace = replacePlusMinus(finalReplace);

            Range replacedRange = getExpandedOnSpacesRange(originalText, offset, length);
            fb.replace(replacedRange.getBeg(), replacedRange.getLengthExclusive(), finalReplace, attrs);

            // Original insertion didn't end with space, but computed insertion does - user doesn't intend to move caret
            // to next measure, so it should be corrected
            if (!textEndsWithSpace(text) && textEndsWithSpace(finalReplace))
                pane.getCaret().setDot(replacedRange.getBeg() + finalReplace.length() - 2);
        }

        private boolean textEndsWithSpace(String text) {
            return text.endsWith(" ") || text.endsWith(";");
        }

        private String replacePlusMinus(String text) {
            return text.replace("+-", "±");
        }

        private String replaceCommas(String text) {
            return text.replace(',', '.');
        }

        private String normalizeSpaces(String text) {
            return normalizationPattern.matcher(text).replaceAll("; ");
        }

        private String normalizeSurroundingSpaces(String text, int selectionStart) {
            // Fast bypass
            if (text.length() == 0)
                return text;
            if (text.length() == 1 && isSpace(text.charAt(0)))
                return "";

            // Trim single spaced on the ends. Normalize longer spaces on the ends. Delete space on the beginning
            // if selection starts at 0 index - we don't need space on series input beginning.
            Range nonSpaceRange = getShrunkOnSpacesRange(text, 0, text.length());
            String trim = nonSpaceRange.cutSubstringExclusive(text);
            if (selectionStart > 0 && nonSpaceRange.getBeg() > 1)
                trim = "; " + trim;
            if (nonSpaceRange.getEnd() < text.length())
                trim = trim + "; ";
            return trim;
        }

        private Range getShrunkOnSpacesRange(CharSequence text, int offset, int length) {
            int beg = offset, end = beg + length;
            while (beg < text.length() && isSpace(text.charAt(beg)))
                beg++;
            while (end > beg && isSpace(text.charAt(end - 1)))
                end--;
            return new Range(beg, end);
        }

        private boolean isSpace(char c) {
            return c == ' ' || c == ';';
        }

        private Range getExpandedOnSpacesRange(CharSequence text, int offset, int length) {
            int beg = offset, end = offset + length;
            while (beg > 0 && isSpace(text.charAt(beg - 1)))    // expand on the left
                beg--;
            while (end < text.length() && isSpace(text.charAt(end)))   // expand on the right
                end++;
            return new Range(beg, end);
        }

        private boolean selectionTouchesSpaces(String text, int offset, int length) {
            return text.length() >= 2       // length at least 2 required for touching
                    && offset != 0 && isSpace(text.charAt(offset - 1))      // left end touches?
                    && offset + length < text.length() && isSpace(text.charAt(offset + length));  // right end touches?
        }

        public JTextPane getPane() {
            return pane;
        }
    }
}
