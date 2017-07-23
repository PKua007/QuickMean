// QuickMean - Quantity.java
//---------------------------------------------------------------------
// Klasa reprezentująca wielkość fizyczną, wraz z jej niepewnościami.
// Niemutualna
//---------------------------------------------------------------------
// Utworzono 12:31 11.06.2017 w IntelliJ IDEA
// (C)PKua, wszystkie prawa zastrzeżone
//---------------------------------------------------------------------

package pl.edu.uj.student.kubala.piotr.qm.lab;

import pl.edu.uj.student.kubala.piotr.qm.FormattedMeasureFactory;

import java.io.Serializable;

public class Quantity implements Serializable
{
    private double value;
    private double standardError;
    private double maxError;

    public Quantity()
    {
        this(0, 0, 0);
    }

    public Quantity(double value, double standardError, double maxError) {
        if (!Double.isFinite(value))
            throw new IllegalArgumentException("Wartość nie jest skończona: " + value);
        if (!Double.isFinite(standardError) || standardError < 0)
            throw new IllegalArgumentException("Niepewność standardowa nie jest skończona i nieujemna: " + standardError);
        if (!Double.isFinite(maxError) || maxError < 0)
            throw new IllegalArgumentException("Błąd maksymalny nie jest skończony i nieujemna: " + maxError);

        this.value = value;
        this.standardError = standardError;
        this.maxError = maxError;
    }

    public double getValue() {
        return value;
    }

    public double getStandardError() {
        return standardError;
    }

    public double getMaxError() {
        return maxError;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Quantity quantity = (Quantity) o;

        return Double.compare(quantity.value, value) == 0
                && Double.compare(quantity.standardError, standardError) == 0
                && Double.compare(quantity.maxError, maxError) == 0;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        temp = Double.doubleToLongBits(value);
        result = (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(standardError);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(maxError);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    @Override
    public String toString() {
        FormattedMeasureFactory factory = new FormattedMeasureFactory();
        factory.setSeparateErrors(true);
        return factory.format(this).toString();
    }
}
