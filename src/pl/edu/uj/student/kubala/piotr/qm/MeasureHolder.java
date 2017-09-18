// QuickMean - MeasureHolder.java
//---------------------------------------------------------------------
// [opis pliku]
//---------------------------------------------------------------------
// Utworzono 22:59 25.07.2017 w IntelliJ IDEA
// (C)PKua, wszystkie prawa zastrzeżone
//---------------------------------------------------------------------

package pl.edu.uj.student.kubala.piotr.qm;

import pl.edu.uj.student.kubala.piotr.qm.lab.Measure;
import pl.edu.uj.student.kubala.piotr.qm.utils.Utils;

public class MeasureHolder extends Model
{
    /**
     * Znak plus minus - przydatne przy wyświetlaniu
     */
    public static final char PLUS_MINUS = '±';

    private String      text;
    private Measure     boundMeasure;
    private boolean     focused;
    private int         errorIdx;
    private Utils.Range valueRange;

    public MeasureHolder() {
        this.errorIdx = -1;
    }

    public MeasureHolder(Measure boundMeasure) {
        this.boundMeasure = boundMeasure;

        StringBuilder builder = new StringBuilder();
        builder.append(boundMeasure.getValue());
        this.valueRange = new Utils.Range(0, builder.length());
        if (boundMeasure.getStandardError() != 0) {
            builder.append(PLUS_MINUS).append(boundMeasure.getCalibrationError())
                    .append(PLUS_MINUS).append(boundMeasure.getHumanError())
                    .append(PLUS_MINUS).append(boundMeasure.getStandardError());
        } else if (boundMeasure.getHumanError() != 0) {
            builder.append(PLUS_MINUS).append(boundMeasure.getCalibrationError())
                .append(PLUS_MINUS).append(boundMeasure.getHumanError());
        } else if (boundMeasure.getCalibrationError() != 0) {
            builder.append(PLUS_MINUS).append(boundMeasure.getCalibrationError());
        }
        this.text = builder.toString();

        this.errorIdx = -1;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Measure getBoundMeasure() {
        return boundMeasure;
    }

    public void setBoundMeasure(Measure boundMeasure) {
        this.boundMeasure = boundMeasure;
    }

    public boolean isFocused() {
        return focused;
    }

    public void setFocused(boolean focused) {
        this.focused = focused;
    }

    public int getErrorIdx() {
        return errorIdx;
    }

    public void setErrorIdx(int errorIdx) {
        this.errorIdx = errorIdx;
    }

    public Utils.Range getValueRange() {
        return valueRange;
    }

    public void setValueRange(Utils.Range valueRange) {
        this.valueRange = valueRange;
    }
}
