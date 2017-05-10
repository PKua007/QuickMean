// CW 2016.12.21 - [nazwa pliku]
//---------------------------------------------------------------------
// [opis pliku]
//---------------------------------------------------------------------
// Utworzono 15:39 02.01.2017
// (C)PKua, wszystkie prawa zastrzeżone
//---------------------------------------------------------------------

package pl.edu.uj.student.kubala.piotr.csv;

import java.io.IOException;

/**
 * Wyjątek zrzucany przez {@link CSVRow}, gdy występuje próba dostępu do nieistniejącego pola
 */
public class CSVNoSuchFieldException extends IOException
{
    /**
     * Bezargumentowy konstruktor tworzący pusty wyjątek.
     */
    public CSVNoSuchFieldException()
    {
        super();
    }

    /**
     * Konstruktor przyjmujący przyczynę wyjątku.
     * @param cause przyczyna zrzucenia wyjątku
     */
    public CSVNoSuchFieldException(String cause)
    {
        super(cause);
    }
}
