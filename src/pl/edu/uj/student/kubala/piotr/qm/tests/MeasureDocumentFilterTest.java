// QuickMean - MeasureDocumentFilterTest.java
//---------------------------------------------------------------------
// [opis pliku]
//---------------------------------------------------------------------
// Utworzono 13:49 28.09.2017 w IntelliJ IDEA
// (C)PKua, wszystkie prawa zastrzeżone
//---------------------------------------------------------------------

package pl.edu.uj.student.kubala.piotr.qm.tests;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import pl.edu.uj.student.kubala.piotr.qm.input.MeasuresInputController;

import javax.swing.*;
import javax.swing.text.*;


class MeasureDocumentFilterTest
{
    private Bypass bypass;
    private JTextPane pane = new JTextPane();
    private MeasuresInputController.MeasureDocumentFilter filter
            = new MeasuresInputController.MeasureDocumentFilter(pane, (mock1, mock2) -> {}, () -> {});

    class Bypass extends DocumentFilter.FilterBypass
    {
        private String text;
        private int replaceOffset;
        private int replaceLength;
        private String replaceString;

        public Bypass(String text) throws BadLocationException {
            this.text = text;
        }

        public String getText() {
            return text;
        }

        public int getReplaceOffset() {
            return replaceOffset;
        }

        public int getReplaceLength() {
            return replaceLength;
        }

        public String getReplaceString() {
            return replaceString;
        }

        @Override
        public Document getDocument() {
            return pane.getDocument();
        }

        @Override
        public void remove(int offset, int length) throws BadLocationException {

            throw new RuntimeException("Shouldn't use remove");
        }

        @Override
        public void insertString(int offset, String string, AttributeSet attr) throws BadLocationException {
            throw new RuntimeException("Shouldn't use insertString");
        }

        @Override
        public void replace(int offset, int length, String string, AttributeSet attrs) throws BadLocationException {
            if (length < 0)
                throw new IllegalArgumentException("length < 0");
            if (offset < 0)
                throw new BadLocationException(text, offset);
            if (offset + length > text.length())
                throw new BadLocationException(text, offset + length);
            StyledDocument document = pane.getStyledDocument();
            StringBuilder builder = new StringBuilder(text);
            builder.replace(offset, offset + length, string);
            document.remove(offset, length);
            document.insertString(offset, string, attrs);
            text = builder.toString();
            assertEquals(text, document.getText(0, document.getLength()));
        }

    }

    private void setupBypass(String text) throws BadLocationException {
        pane.setText(text);
        bypass = new Bypass(text);
    }

    private void replace(int offset, int length, String text, boolean caretLeft) throws BadLocationException {
        Caret caret = pane.getCaret();
        if (caretLeft) {
            caret.setDot(offset + length);
            caret.moveDot(offset);
        } else {
            caret.setDot(offset);
            caret.moveDot(offset + length);
        }
        filter.replace(bypass, offset, length, text, null);
    }

    private void replace(int offset, int length, String text) throws BadLocationException {
        replace(offset, length, text, true);
    }

    @Test
    void insertDigitToEmpty() throws BadLocationException {
        setupBypass("");
        replace(0, 0, "5");
        expectText("5");
    }

    private void expectText(String expected) {
        assertEquals(expected, bypass.getText());
    }

    @Test
    void insertDigitToNonEmptyOnTheEnd() throws BadLocationException {
        setupBypass("5");
        replace(1, 0, "6");
        expectText("56");
    }

    @Test
    void insertDigitToNonEmptyWithSemicolonOnTheEnd() throws BadLocationException {
        setupBypass("5; ");
        replace(1, 0, "6");
        expectText("56; ");
    }

    @Test
    void insertDigitToNonEmptyWithSemicolonOnTheBeginning() throws BadLocationException {
        setupBypass("5; ");
        replace(1, 0, "6");
        expectText("56; ");
    }

    @Test
    void insertDigitInTheMiddle() throws BadLocationException {
        setupBypass("56; ");
        replace(1, 0, "7");
        expectText("576; ");
    }

    @Test
    void insertSpaceAfter() throws BadLocationException {
        setupBypass("56");
        replace(2, 0, " ");
        expectText("56; ");

        setupBypass("56");
        replace(2, 0, ";");
        expectText("56; ");
    }

    @Test
    void insertMoreSpacesAfter() throws BadLocationException {
        setupBypass("56");
        replace(2, 0, "; ;; ; ");
        expectText("56; ");
    }

    @Test
    void insertMoreDigits() throws BadLocationException {
        setupBypass("56; 78; 90; ");
        replace(4, 0, "122");
        expectText("56; 12278; 90; ");
    }

    @Test
    void insertBetweenSpaces() throws BadLocationException {
        setupBypass("12; 34; 56; ");
        replace(3, 0, "9");
        expectText("12; 9; 34; 56; ");

        setupBypass("12; 34; 56; ");
        replace(3, 0, "098");
        expectText("12; 098; 34; 56; ");
    }

    @Test
    void insertBetweenSpacesWithSpaces() throws BadLocationException {
        setupBypass("12; 34; 56; ");
        replace(3, 0, "; 098;");
        expectText("12; 098; 34; 56; ");
    }

    @Test
    void insertInMeasureWithSpaces() throws BadLocationException {
        setupBypass("123; 346; 789; ");
        replace(5, 0, ";098;");
        expectText("123; 098; 346; 789; ");

        setupBypass("123; 346; 789; ");
        replace(6, 0, ";098;");
        expectText("123; 3; 098; 46; 789; ");

        setupBypass("123; 346; 789; ");
        replace(6, 0, ";098");
        expectText("123; 3; 09846; 789; ");
    }

    @Test
    void insertWithSpacesAnZeroIndex() throws BadLocationException {
        setupBypass("123; 346; 789; ");
        replace(0, 0, "; 098;");
        expectText("098; 123; 346; 789; ");
    }

    @Test
    void travelOnSpace() throws BadLocationException {
        setupBypass("12; 34; ");

        replace(2, 0, ";");
        expectText("12; 34; ");
        replace(2, 0, " ");
        expectText("12; 34; ");
        replace(3, 0, ";");
        expectText("12; 34; ");
        replace(3, 0, " ");
        expectText("12; 34; ");
    }

    @Test
    void repairBrokenSpace() throws BadLocationException {
        setupBypass("12;34; ");
        replace(3, 0, ";");
        expectText("12; 34; ");

        setupBypass("12 34; ");
        replace(3, 0, ";");
        expectText("12; 34; ");

        setupBypass("12; ;; 34; ");
        replace(3, 0, ";");
        expectText("12; 34; ");

        setupBypass("34;");
        replace(3, 0, ";");
        expectText("34; ");
    }

    @Test
    void spaceAtBeginningShouldNotBeInserted() throws BadLocationException {
        setupBypass("12; 34; ");
        replace(0, 0, " ");
        expectText("12; 34; ");

        setupBypass("12; 34; ");
        replace(0, 0, ";");
        expectText("12; 34; ");
    }

    @Test
    void divideMeasures() throws BadLocationException {
        setupBypass("12; 34; ");
        replace(5, 0, " ");
        expectText("12; 3; 4; ");

        setupBypass("12; 34; ");
        replace(5, 0, ";");
        expectText("12; 3; 4; ");
    }

    @Test
    void divideMeasuresWithLotsOfSpaces() throws BadLocationException {
        setupBypass("12; 34; ");
        replace(5, 0, ";;  ;; ; ;; ; ;");
        expectText("12; 3; 4; ");
    }

    @Test
    void deleteSpace() throws BadLocationException {
        setupBypass("12; 34; ");
        replace(2, 1, "");
        expectText("1234; ");

        setupBypass("12; 34; ");
        replace(3, 1, "");
        expectText("1234; ");

        setupBypass("12; 34; ");
        replace(2, 2, "");
        expectText("1234; ");
    }

    @Test
    void deleteSpaceAtTheEnd() throws BadLocationException {
        setupBypass("12; 34; ");
        replace(6, 1, "");
        expectText("12; 34");

        setupBypass("12; 34; ");
        replace(7, 1, "");
        expectText("12; 34");

        setupBypass("12; 34; ");
        replace(6, 2, "");
        expectText("12; 34");
    }

    @Test
    void deleteToCombineMeasures() throws BadLocationException {
        setupBypass("123; 456; 789; ");
        replace(1, 5, "");
        expectText("156; 789; ");
    }

    @Test
    void deleteOneDigitFromMeasure() throws BadLocationException {
        setupBypass("123; 456; 890; ");
        replace(2, 1, "");
        expectText("12; 456; 890; ");
    }

    @Test
    void deleteTheOnlyDigitFromMeasure() throws BadLocationException {
        setupBypass("123; 4; 567; ");
        replace(5, 1, "");
        expectText("123; 567; ");
    }

    @Test
    void deleteTheOnlyDigitFromLastMeasure() throws BadLocationException {
        setupBypass("123; 567; 4; ");
        replace(10, 1, "");
        expectText("123; 567; ");

        setupBypass("123; 567; 4");
        replace(10, 1, "");
        expectText("123; 567; ");

    }

    @Test
    void deleteWholeMeasure() throws BadLocationException {
        setupBypass("123; 456; 789; ");
        replace(5, 3, "");
        expectText("123; 789; ");

        setupBypass("123; 456; 789; ");
        replace(10, 3, "");
        expectText("123; 456; ");
    }

    @Test
    void deleteWholeMeasureWithSomeSpace() throws BadLocationException {
        setupBypass("123; 456; 789; ");
        replace(3, 5, "");
        expectText("123; 789; ");

        setupBypass("123; 456; 789; ");
        replace(4, 5, "");
        expectText("123; 789; ");

        setupBypass("123; 456; 789; ");
        replace(5, 5, "");
        expectText("123; 789; ");

        setupBypass("123; 456; 789; ");
        replace(4, 4, "");
        expectText("123; 789; ");

        setupBypass("123; 456; 789; ");
        replace(5, 4, "");
        expectText("123; 789; ");
    }

    @Test
    void replaceMeasureWithAnother() throws BadLocationException {
        setupBypass("123; 456; 789; ");
        replace(5, 3, "0e5");
        expectText("123; 0e5; 789; ");
    }

    @Test
    void replaceSpaceWithSpace() throws BadLocationException {
        setupBypass("123; 456; 789; ");
        replace(4, 1, ";  ; ");
        expectText("123; 456; 789; ");
        replace(3, 2, "  ; ;;");
        expectText("123; 456; 789; ");
        replace(13, 2, ";;; ");
        expectText("123; 456; 789; ");
    }

    @Test
    void replaceMeasureWithSpace() throws BadLocationException {
        setupBypass("123; 456; 789; ");
        replace(5, 3, ";;; ");
        expectText("123; 789; ");
    }

    @Test
    void replaceFewMeasuresWithSpacesWithFewMeasuresWithSpaces() throws BadLocationException {
        setupBypass("123; 456; 789; ");
        replace(4, 7, " 456; 32; ");
        expectText("123; 456; 32; 89; ");

        setupBypass("123; 456; 789; ");
        replace(4, 7, "456; 32 ");
        expectText("123456; 32; 89; ");

        setupBypass("123; 456; 789; ");
        replace(4, 7, " 456; 32");
        expectText("123; 456; 3289; ");
    }

    @Test
    void insertMeasuresWithMessedUpSpaces() throws BadLocationException {
        setupBypass("123");
        replace(3, 0, ";; ;456;789;;; 000;; ");
        expectText("123; 456; 789; 000; ");
    }

    @Test
    void insertEverythingMessedUpAsMuchAsItCanBe() throws BadLocationException {
        setupBypass("123; 456; 789; ");
        replace(0, 6, ";0e4; ;;; 00;; 11");
        expectText("0e4; 00; 1156; 789; ");
    }

    @Test
    void insertWithPlusMinus() throws BadLocationException {
        setupBypass("");
        replace(0, 0, "2+-3");
        expectText("2±3");
    }

    @Test
    void insertProducingPlusMinusOnTheSide() throws BadLocationException {
        setupBypass("23+23");
        replace(3, 0, "-3");
        expectText("23+-323");
    }

    @Test
    void producePlusMinus() throws BadLocationException {
        setupBypass("23+; 5; ");
        replace(3, 0, "-");
        expectText("23±; 5; ");
    }

    @Test
    void insertComma() throws BadLocationException {
        setupBypass("2");
        replace(1, 0, ",");
        expectText("2.");
    }

    @Test
    void insertMeasuresWithCommas() throws BadLocationException {
        setupBypass("");
        replace(0, 0, "1,3; 4,5; ");
        expectText("1.3; 4.5; ");
    }

    /*@Test
    void spaceMovingCaret() throws BadLocationException {
        setupBypass("56; ");
        replace(0, 0, "15; ");
        expectText("15; 56; ");
        assertEquals(4, pane.getCaret().getDot());
        assertEquals(4, pane.getCaret().getMark());
    }*/
}