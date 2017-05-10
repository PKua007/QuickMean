// CW 2016.12.21 - CSVFormatException.java
//---------------------------------------------------------------------
// Utworzono 13:33 28.12.2016
// (C)PKua, wszystkie prawa zastrzeżone
//---------------------------------------------------------------------

package pl.edu.uj.student.kubala.piotr.csv;

import java.io.IOException;

/**
 * Wyjątek zrzucany w przypadku wystąpienia błędu podczas parsingu pliku CSV.
 */
public class CSVFormatException extends IOException
{
    /**
     * Bezargumentowy konstruktor. Tworzy pusty wyjątek.
     */
    public CSVFormatException()
    {
        super();
    }

    /**
     * Konstruktor tworzący wyjątek z podaną przyczyną.
     * @param cause opis błędu parsingu
     */
    public CSVFormatException(String cause)
    {
        super(cause);
    }
}
