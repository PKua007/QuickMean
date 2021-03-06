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
import pl.edu.uj.student.kubala.piotr.qm.Main;
import pl.edu.uj.student.kubala.piotr.qm.lab.LabProject;
import pl.edu.uj.student.kubala.piotr.qm.lab.Series;
import pl.edu.uj.student.kubala.piotr.qm.lab.SeriesGroup;
import pl.edu.uj.student.kubala.piotr.qm.utils.Range;
import pl.edu.uj.student.kubala.piotr.qm.utils.Utils;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.regex.Pattern;

public class MeasuresInputController implements Controller
{
    private LabProject      labProject;
    private MeasuresInput   measuresInput;
    private Handler         handler;

    private int lastCaretMeasureInfoIdx = -1;
    private MeasureDocumentFilter filter;

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
        filter = new MeasureDocumentFilter(measuresInput.getInputPane(), this::reparseText, this::disableFilter);
        doc.setDocumentFilter(filter);

        handler = new Handler();
        editor.addCaretListener(handler);
        labProject.addPropertyChangeListener(handler);

        Action deleteMeasureAction = new DeleteMeasureAction();
        Utils.copyButtonAction(measuresInput.getDeleteMeasureButton(), deleteMeasureAction);
        Action nextSeriesAction = new NextSeriesAction();
        Utils.copyButtonAction(measuresInput.getNextSeriesButton(), nextSeriesAction);

        final KeyStroke enterStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, false);
        measuresInput.getInputPane().getKeymap().addActionForKeyStroke(enterStroke, nextSeriesAction);
        measuresInput.getDeleteMeasureButton().setAction(deleteMeasureAction);
        measuresInput.getNextSeriesButton().setAction(nextSeriesAction);
    }

    private void disableFilter() {
        filter.setEditingBlocked(true);
    }

    @Override
    public String getEDTInitializableName() {
        return "MeasuresInputController";
    }

    private void reparseText(int selectionIndex, int selectionLength)
    {
        SwingUtilities.invokeLater(() -> reparseText0(selectionIndex, selectionLength));
    }

    private void reparseText0(int selectionIndex, int selectionLength) {
        if (Main.PARSE_AND_HIGHLIGHT_DEBUG)
            System.out.println("Reparsing text...");

        // Parse and highlight
        SeriesParser parser = new SeriesParser();
        String text = measuresInput.getInputPane().getText();
        SeriesInputInfo seriesInputInfo = parser.parseSeries(text, selectionIndex, selectionLength);
        measuresInput.setSeriesInputInfo(seriesInputInfo);
        measuresInput.highlightInputPane(seriesInputInfo.getMeasuresInSelection());

        // Add parsed measures to Series
        Series highlightedSeries = labProject.getHighlightedSeries();
        if (highlightedSeries == null)
            return;

        highlightedSeries.clear();
        for (MeasureInputInfo measureInputInfo : seriesInputInfo.getAllInfos())
            if (measureInputInfo.isCorrect())
                highlightedSeries.addElement(measureInputInfo.getMeasure());
        highlightedSeries.updateMean();

        // Unlock text input and save measure input info with text caret
        JTextPane pane = measuresInput.getInputPane();
        lastCaretMeasureInfoIdx = seriesInputInfo.getMeasureInfoIdxForCaretPos(pane.getCaretPosition());
        filter.setEditingBlocked(false);
    }

    /* Akcja dodawania następnej serii za obecnie podświetloną */
    private class NextSeriesAction extends AbstractAction {

        @Override
        public void actionPerformed(ActionEvent e) {
            Series highlightedSeries = labProject.getHighlightedSeries();
            if (highlightedSeries == null)
                return;

            SeriesGroup group = (SeriesGroup) highlightedSeries.getParent();
            Series newSeries = new Series();
            int newIdx = group.addElement(newSeries, group.getElementIdx(highlightedSeries) + 1);
            group.setHighlightedSeries(newIdx);
            group.setSelectedSeries(new int[]{newIdx});
            measuresInput.getInputPane().requestFocusInWindow();
        }
    }

    /* Akcja usuwania pomiarów, na którym jest kursor (lub zaznaczenie) */
    private class DeleteMeasureAction extends AbstractAction {

        @Override
        public void actionPerformed(ActionEvent e) {
            Series highlightedSeries = labProject.getHighlightedSeries();
            if (highlightedSeries == null)
                return;
            SeriesInputInfo seriesInputInfo = measuresInput.getSeriesInputInfo();
            if (seriesInputInfo == null)
                return;

            final JTextPane pane = measuresInput.getInputPane();
            Range selectedMeasures = getSelectedMeasuresFromCaret(seriesInputInfo, pane.getCaret());
            if (selectedMeasures == null)
                return;

            Range measuresTextRange = getMeasuresTextRange(seriesInputInfo, selectedMeasures);
            try {
                pane.getDocument()
                        .remove(measuresTextRange.getBeg(), measuresTextRange.getLength());
            } catch (BadLocationException e1) {
                throw new RuntimeException(e1);
            }
            pane.requestFocusInWindow();
        }

        /* Constructs text range containing range of measures */
        private Range getMeasuresTextRange(SeriesInputInfo seriesInputInfo, Range selectedMeasures) {
            return new Range(
                    seriesInputInfo.getMeasureInfo(selectedMeasures.getMin()).getTextRange().getBeg(),
                    seriesInputInfo.getMeasureInfo(selectedMeasures.getMax()).getTextRange().getEnd());
        }

        /* Fetches selectd measures range based on selection computed form caret object */
        private Range getSelectedMeasuresFromCaret(SeriesInputInfo seriesInputInfo, Caret caret) {
            return seriesInputInfo.getMeasureInfosRangeForSelection(
                    Math.min(caret.getDot(), caret.getMark()), Math.abs(caret.getDot() - caret.getMark()));
        }
    }

    /* Private inner class handling all Events */
    private class Handler implements CaretListener, PropertyChangeListener
    {
        /* Invoked when caret moves in series. Change highlighted measure */
        @Override
        public void caretUpdate(CaretEvent e) {
            if (filter.isEditingBlocked())   // do not handle caret, if editing blocked - wait for parsing end
                return;

            SeriesInputInfo currentSeriesInfo = measuresInput.getSeriesInputInfo();
            Caret caret = measuresInput.getInputPane().getCaret();

            int currentMeasureInfoIdx = currentSeriesInfo.getMeasureInfoIdxForCaretPos(caret.getDot());
            if (lastCaretMeasureInfoIdx != currentMeasureInfoIdx) {
                if (Main.PARSE_AND_HIGHLIGHT_DEBUG)
                    System.out.println("Unhighlighting: " + lastCaretMeasureInfoIdx + ", highlighting: " + currentMeasureInfoIdx);

                if (lastCaretMeasureInfoIdx != -1)
                    measuresInput.highlightInputPane(new Range(lastCaretMeasureInfoIdx));
                if (currentMeasureInfoIdx != -1)
                    measuresInput.highlightInputPane(new Range(currentMeasureInfoIdx));
                lastCaretMeasureInfoIdx = currentMeasureInfoIdx;
            }
        }

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            switch (evt.getPropertyName())
            {
                // Invoked when highlighted series changes (including group change). Print and colorize new series
                case SeriesGroup.HIGHLIGHTED_SERIES:
                case LabProject.SELECTED_GROUP:
                    Series highlightedSeries = labProject.getHighlightedSeries();
                    if (highlightedSeries == null)
                        break;
                    SeriesParser seriesParser = new SeriesParser();
                    SeriesInputInfo inputInfo = seriesParser.printSeries(highlightedSeries);
                    installChangedSeries(inputInfo);
                    break;
            }
        }

        /* Installs new chosen series and colorizes it */
        private void installChangedSeries(SeriesInputInfo inputInfo) {
            JTextPane pane = measuresInput.getInputPane();
            AbstractDocument paneDoc = (AbstractDocument) pane.getDocument();

            // Remove caret listener and document filter - they'll go crazy otherwise. Restore afterwards
            pane.removeCaretListener(this);
            paneDoc.setDocumentFilter(null);
            measuresInput.setSeriesInputInfo(inputInfo);
            lastCaretMeasureInfoIdx = -1;   // Reset current highlighted measure
            paneDoc.setDocumentFilter(filter);
            pane.addCaretListener(this);

            if (Main.PARSE_AND_HIGHLIGHT_DEBUG)
                System.out.println("highlighting after printing series");
            measuresInput.highlightInputPane();
        }
    }

    /**
     * Filtr dokumentu obługujący inteligentne wpisywanie pomiarów w formacie przyjmowanym przez
     * {@link SeriesParser}. Jego zachowanie w skrócie:
     * <ul>
     *     <li>wpisanie "," lub "." powoduje zamianę na "."</li>
     *     <li>wpisanie "+-" powoduje zamianę na "±"</li>
     *     <li>wciśnięcie " " lub ";" powoduje wstawienie pełnego odstępu "; "</li>
     *     <li>wstawianie drugiego pełnego odstępu w istniejącym powoduje jedynie przesunięcie karety</li>
     *     <li>wklejany tekst również jest przetwarzany tak, jak poprzednie podpkunty. Wynik wklejenia / wycięcia /
     *     usunięcia zaznaczenia również zachowa format</li>
     *     <li>również odstępy powstałe na brzegach wklejenia/wycięcia są w intuicyjny sposób przetwarzane</li>
     *     <li>zmazanie pojedynczego znaku odstępu scala sąsiednie pomiary</li>
     *     <li>odstęp nie może się znaleźć na początku okna. Na końcu może być wstawiony, ale nie musi</li>
     *     <li>wstawienie nowego pomiaru między ";" i " " zamienia rozdzielony pełny odstęp na 2 pełne odstępy</li>
     *     <li>zmazanie całego pomiaru powoduje scalenie dwóch pełnych odstępów w 1 pełny odstęp</li>
     * </ul>
     */
    public static class MeasureDocumentFilter extends DocumentFilter
    {
        @FunctionalInterface
        public interface ParseExecutor
        {
            void parse(int selectionStart, int selectionEnd);
        }

        private final Pattern normalizationPattern = Pattern.compile("[; ]+");

        private JTextPane pane;
        private ParseExecutor parseExecutor;
        private Runnable runBeforeEdit;
        private boolean editingBlocked = false;

        public MeasureDocumentFilter(JTextPane pane, ParseExecutor parseExecutor, Runnable runBeforeEdit) {
            this.pane = pane;
            this.parseExecutor = parseExecutor;
            this.runBeforeEdit = runBeforeEdit;
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
            if (editingBlocked)    // Ignored edits while editing is blocked
                return;
            runBeforeEdit.run();
            replace0(fb, offset, length, text, attrs);
        }

        private void replace0(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
            Document document = fb.getDocument();
            String originalText = document.getText(0, document.getLength());

            // Inserting minus preceded by plus - change to plus-minus
            if (length == 0 && "-".equals(text) && offset > 0 && originalText.charAt(offset - 1) == '+') {
                fb.replace(offset - 1, 1, "±", attrs);
                parseExecutor.parse(offset - 1, 1);
                return;
            }

            // Initially prepare replace text. Add additional spaces on ends when replaced range is surrounded by
            // spaces - it will make sure this spaces won't vanish after further processing. Normalize spaces.
            String initialReplace = selectionTouchesSpaces(originalText, offset, length)
                    ? normalizeSpaces(" " + text + " ")
                    : normalizeSpaces(text);

            // Add spaces surrounding "text to replace" to "replace text"
            Range toReplaceRange = getExpandedOnSpacesRange(originalText, offset, length);
            String finalReplace = originalText.substring(toReplaceRange.getBeg(), offset) +
                    initialReplace +
                    originalText.substring(offset + length, toReplaceRange.getEnd());

            // Make remaining normalization and replacements
            finalReplace = normalizeSurroundingSpaces(finalReplace, offset);
            finalReplace = replaceCommas(finalReplace);
            finalReplace = replacePlusMinus(finalReplace);

            Range replacedRange = getExpandedOnSpacesRange(originalText, offset, length);
            fb.replace(replacedRange.getBeg(), replacedRange.getLengthExclusive(), finalReplace, null);

            // Original insertion didn't end with space, but computed insertion does - user doesn't intend to move caret
            // to next measure, so it should be corrected
            if (!deletingMeasureBeginning(originalText, offset, length)
                    && !textEndsWithSpace(text) && textEndsWithSpace(finalReplace))
                pane.getCaret().setDot(replacedRange.getBeg() + finalReplace.length() - 2);

            parseExecutor.parse(replacedRange.getBeg(), finalReplace.length());
        }

        private String replacePlusMinus(String text) {
            return text.replace("+-", "±");
        }

        private String replaceCommas(String text) {
            return text.replace(',', '.');
        }

        /* Returns text where all spaces are change to "; " */
        private String normalizeSpaces(String text) {
            return normalizationPattern.matcher(text).replaceAll("; ");
        }

        /* Deletes single spaces on ends, longer spaces on ends are normalized. */
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

        /* Trim range so that it doesn't have spaces on ends */
        private Range getShrunkOnSpacesRange(CharSequence text, int offset, int length) {
            int beg = offset, end = beg + length;
            while (beg < text.length() && isSpace(text.charAt(beg)))
                beg++;
            while (end > beg && isSpace(text.charAt(end - 1)))
                end--;
            return new Range(beg, end);
        }

        /* Expand range so that it has as much spaces on ends as it can have */
        private Range getExpandedOnSpacesRange(CharSequence text, int offset, int length) {
            int beg = offset, end = offset + length;
            while (beg > 0 && isSpace(text.charAt(beg - 1)))    // expand on the left
                beg--;
            while (end < text.length() && isSpace(text.charAt(end)))   // expand on the right
                end++;
            return new Range(beg, end);
        }

        private boolean isSpace(char c) {
            return c == ' ' || c == ';';
        }

        private boolean textEndsWithSpace(String text) {
            return text.endsWith(" ") || text.endsWith(";");
        }

        private boolean deletingMeasureBeginning(String text, int offset, int length) {
            return (offset == 0 || isSpace(text.charAt(offset - 1)))  // left end of selection touches space/text beg
                    && length > 0  // replacing anything
                    && offset < text.length() && !isSpace(text.charAt(offset));  // first replaced char is not space
        }

        /* Check weather selection in text touches spaces on its both ends, eg. "; [--selection--]; 55" */
        private boolean selectionTouchesSpaces(String text, int offset, int length) {
            return text.length() >= 2       // length at least 2 required for touching
                    && offset != 0 && isSpace(text.charAt(offset - 1))      // left end touches?
                    && offset + length < text.length() && isSpace(text.charAt(offset + length));  // right end touches?
        }

        /* Check weather selection in text touches spaces or string ends on its both ends, eg. "[--selection--] 55" */
        private boolean selectionTouchesSpacesOrEnds(String text, int offset, int length) {
            return (offset == 0 || isSpace(text.charAt(offset - 1)))      // left end touches?
                    && (offset + length >= text.length() || isSpace(text.charAt(offset + length)));  // right end touches?
        }

        public boolean isEditingBlocked() {
            return editingBlocked;
        }

        public void setEditingBlocked(boolean editingBlocked) {
            this.editingBlocked = editingBlocked;
        }
    }
}
