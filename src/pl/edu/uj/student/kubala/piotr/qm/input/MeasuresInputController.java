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

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

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
        doc.setDocumentFilter(new MeasureDocumentFilter());

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

    private void reparseText()
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

    private class MeasureDocumentFilter extends DocumentFilter
    {
        @Override
        public void remove(FilterBypass fb, int offset, int length) throws BadLocationException {
            System.out.println("remove [offset = " + offset + ", length = " + length + "]");
            removeImpl(fb, offset, length);
            SwingUtilities.invokeLater(MeasuresInputController.this::reparseText);
        }

        @Override
        public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
            System.out.println("insert [string = \"" + string + "\", offset = " + offset + "]");
            fb.insertString(offset, string, attr);
        }

        @Override
        public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
            System.out.println("replace [text = \"" + text + "\", offset = " + offset + ", length = " + length + "]");
            replaceImpl(fb, offset, length, text, attrs);
            SwingUtilities.invokeLater(MeasuresInputController.this::reparseText);
        }

        private void removeImpl(FilterBypass fb, int offset, int length) throws BadLocationException {
            Document doc = fb.getDocument();
            String content = doc.getText(0, doc.getLength());

            if (length == 1) {
                switch (content.charAt(offset)) {
                    case ';':
                        if (offset + 1 <= content.length() && content.charAt(offset + 1) == ' ') {
                            fb.remove(offset, 2);
                            return;
                        }
                        break;
                    case ' ':
                        if (offset > 0 && content.charAt(offset - 1) == ';') {
                            fb.remove(offset - 1, 2);
                            return;
                        }
                        break;
                    default:
                        if ((offset == 0 || (offset > 2 && content.charAt(offset - 1) == ' '))
                                && offset < content.length() - 2 && "; ".equals(content.substring(offset + 1, offset + 3))) {
                            fb.remove(offset, 3);
                            return;
                        }
                        break;
                }
            }

            fb.remove(offset, length);
        }

        private void replaceImpl(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
            Document doc = fb.getDocument();
            String content = doc.getText(0, doc.getLength());
            JEditorPane editorPane = measuresInput.getInputPane();

            if (",".equals(text) || ".".equals(text))
                text = ".";

            if (length == 0) {
                if ("-".equals(text)) {
                    if (offset > 0 && content.charAt(offset - 1) == '+') {
                        fb.replace(offset - 1, 1, "±", null);
                        return;
                    }
                }

                if (";".equals(text) || " ".equals(text)) {
                    if (offset == 0)
                        return;
                    else if (offset < content.length() && (content.charAt(offset) == ';' || content.charAt(offset) == ' '))
                        fb.replace(offset, 1, content.substring(offset, offset + 1), null);
                    else if (content.charAt(offset - 1) == ';' || content.charAt(offset - 1) == ' ')
                        return;
                    else
                        fb.replace(offset, 0, "; ", null);
                    return;
                } else {
                    if (offset < content.length() && content.charAt(offset) == ' ') {
                        fb.replace(offset, length, " " + text + ";", attrs);
                        editorPane.getCaret().setDot(offset + 2);
                        return;
                    }
                }
            }

            fb.replace(offset, length, text, attrs);
        }
    }
}
