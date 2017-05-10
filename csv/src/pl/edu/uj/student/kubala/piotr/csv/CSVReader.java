// CW 2016.12.21 - CSVReader.java
//---------------------------------------------------------------------
// Utworzono 13:18 28.12.2016
// (C)PKua, wszystkie prawa zastrzeżone
//---------------------------------------------------------------------

package pl.edu.uj.student.kubala.piotr.csv;

import java.io.*;
import java.util.*;

/**
 * <p> {@code Reader} plików CSV spełniających standard RFC 4180 z podanego strumienia znakowego. Pozwala na ustalenie
 * wybranego znaku separatora i cudzysłowa - w domyśle to przecinek {@code ,} i cudzysłów {@code "}. Odczytywane dane
 * mogą posiadać nagłówek, lub nie.
 *
 * <p> Przykładowe użycie:
 * <blockquote><pre>
 * CSVReader reader = null;
 * try {
 *    reader = new CSVReader(new FileReader("xyz.pl.edu.uj.student.kubala.piotr.csv", true));    // plik CSV z nagłówkiem
 *
 *     CSVRow row;
 *     while ((row = reader.readRow()) != null)
 *         System.out.println(row.get("person") + ": " + row.get("occupation");
 * } catch (IOException e) {
 *     e.printStackTrace();
 * } finally {
 *     if (reader != null)
 *         reader.close();
 * }</pre></blockquote>
 *
 * <p> Jeśli xyz.pl.edu.uj.student.kubala.piotr.csv zawiera następujące dane:
 * <blockquote><pre>
 * person,occupation
 * "Piotr ""PKua"" Kubala",programista
 * Jan Kowalski,grafik</pre></blockquote>
 *
 * <p> to na standardowym wyjściu zostanie wypisane:
 * <blockquote><pre>
 * Piotr "PKua" Kubala: programista
 * Jan Kowalski: grafik</pre></blockquote>
 */
public class CSVReader implements Closeable
{
    /** Domyślny separator pól w rzędzie - przecinek {@code ,}*/
    public static final char    DEFAULT_SEPARATOR = ',';
    /** Domyślny cudzysłów do otaczania pól - znak cudzysłowu {@code "} */
    public static final char    DEFAULT_QUOTATION_MARK = '"';

    private char        separator;
    private char        quotationMark;
    private int         numColumns = 0;         // Liczba kolumn w nagłówku
    private boolean     hasHeader = false;
    private String []   columnNames;            // Tablica z nazwami kolumn podanymi w nagłówku

    private BufferedReader textReader;          // Przekazany klasie reader do odczytu danych CSV

    /**
     * Konstruktor przyjmujący pełną specyfikację strumienia CSV.
     * @param textReader {@code Reader}, z którego mają zostać odczytane dane CSV
     * @param hasHeader {@code true}, jeśli dane posiadają nagłówek z nazwami kolumn, {@code false} w przeciwnym wypadku.
     *        Jeśli jest nagłówek, zostanie odczytany podczas konstrukcji instancji klasy.
     * @param separator znak separatora pól
     * @param quotationMark znak cudzysłowa do otaczania pól
     * @throws IllegalArgumentException jeśli znak separatora jest identyczny jak cudzysłowu, bądź któryś z nich nie
     *         jest nie-kontrolnym znakiem z podstawowej tablicy ASCII (znaki 32 - 126 włącznie)
     * @throws IOException zrzucone przez textReader, jeśli wystąpi błąd podczas odczytu nagłówka ze strumienia
     * @throws CSVFormatException gdy podczas parsingu nagłówka wystąpi błąd, bądź nagłówek ma niepoprawny format - nazwy
     *         kolumn muszą być niepowtarzalne i niepuste
     */
    public CSVReader(Reader textReader, boolean hasHeader, char separator, char quotationMark) throws IOException
    {
        if (separator == quotationMark)
            throw new IllegalArgumentException("Separator and quotation mark should be different characters");

        if (separator < 32 || separator > 126)
            throw new IllegalArgumentException("Separator should be basic-ASCII non-control character");

        if (quotationMark < 32 || quotationMark > 126)
            throw new IllegalArgumentException("Quotation mark should be basic-ASCII non-control character");

        constructReader(textReader, hasHeader, separator, quotationMark);
    }

    /**
     * Konstruktor z minimalną ilością parametrów zakładający domyślne wartości reszty - brak nagłówka, domyślny
     * separator ({@link CSVReader#DEFAULT_SEPARATOR}), domyślny cudzysłów ({@link CSVReader#DEFAULT_QUOTATION_MARK}).
     * @param textReader strumień znakowy, z którego mają zostać odczytane dane
     */
    public CSVReader(Reader textReader)
    {
        try {
            constructReader(textReader, false, DEFAULT_SEPARATOR, DEFAULT_QUOTATION_MARK);
        } catch (IOException e) {
            // Wyjątek nie powinien zostać zrzucony
            throw new RuntimeException("CSVReader: unknown error");
        }
    }

    /**
     * Konstruktor zakładający domyślny separator ({@link CSVReader#DEFAULT_SEPARATOR}) i domyślny cudzysłów
     * ({@link CSVReader#DEFAULT_QUOTATION_MARK}).
     * @param textReader strumień znakowy, z którego mają zostać odczytane dane
     * @param hasHeader {@code true}, jeśli strumień danych ma nagłówek, {@code false} w przeciwnym wypadku
     */
    public CSVReader(Reader textReader, boolean hasHeader)
    {
        try {
            constructReader(textReader, hasHeader, DEFAULT_SEPARATOR, DEFAULT_QUOTATION_MARK);
        } catch (IOException e) {
            // Wyjątek nie powinien zostać zrzucony
            throw new RuntimeException("CSVReader: unknown error");
        }
    }

    /* Wewnętrzna metoda tworząca CSVReadera o podanych parametrach */
    private void constructReader(Reader textReader, boolean hasHeader, char separator, char quotationMark) throws IOException
    {
        this.textReader = new BufferedReader(textReader);
        this.hasHeader = hasHeader;
        this.separator = separator;
        this.quotationMark = quotationMark;

        // Jeśli plik ma domniemany nagłówek, wczytaj go
        if (hasHeader)
        {
            ArrayList<String> headerList = readAndParseRow();
            if (headerList == null || headerList.isEmpty())
                throw new CSVFormatException("Empty header");

            // Sprawdź, czy któraś kolumna w nagłówku się nie powtarza
            TreeSet<String> headerSet = new TreeSet<>();
            headerSet.addAll(headerList);

            // Sprawdź, czy nazwy wszyskich kolumn są niepuste
            if (headerSet.contains(""))
                throw new CSVFormatException("Header must note have empty columns");

            if (headerSet.size() != headerList.size())
                throw new CSVFormatException("Column names in header aren't unique");

            // Ustaw nazwy kolumn i ich liczbę
            this.columnNames = headerList.toArray(new String[headerList.size()]);
            this.numColumns = headerList.size();
        }
    }

    /**
     * Metoda odczytuje następny rząd ze strumienia danych.
     * @return instancję {@link CSVRow} z odczytanymi danymi lub {@code null}, jeśli osiagnięto koniec strumienia
     * @throws IOException jeśli wystąpi błąd odczytu ze strumienia, bądź dane nie pasują do formatu CSV
     */
    public CSVRow readRow() throws IOException
    {
        // Zparsuj kolejny rząd
        ArrayList<String> row = readAndParseRow();
        if (row == null)
            return null;

        // Jeśli liczba kolumn jeszcze nieustalona, ustal. Jeśli już jest sprawdź, czy jest zachowana
        if (this.numColumns == 0)
            this.numColumns = row.size();
        else if (this.numColumns != row.size())
            throw new CSVFormatException("Number of fields in current row is different to previous rows");

        // Zrwóć CSVRow z wynikiem
        if (this.hasHeader())
            return new CSVRow(this.columnNames, row);
        else
            return new CSVRow(row);
    }

    /* Metoda odczytuje ze strumienia rząd i próbuje go sparsować. Zwraca ArrayListę z polami, null, jeśli osiągnięto
     * koniec strumianie, bądź zrzuca wyjątek, jeśli wystąpił bład parsingu.
     */
    private ArrayList<String> readAndParseRow() throws IOException
    {
        String szLine, szAppendLine;
        String szQuotedField;
        ArrayList<String> fieldList;

        // Utwórz ArrayListę - jeśli już odczytano pierwszy rząd, znana jest (prawidłowa) liczba pól
        if (this.numColumns != 0)
            fieldList = new ArrayList<>(this.numColumns);
        else
            fieldList = new ArrayList<>();

        int iIndex = 0;
        int iStartIndex;

        szLine = this.textReader.readLine();
        if (szLine == null)
            return null;

        // Pętla wczytująca kolejne pola - wczytuj, dopóki indeks nie wskazuje na drugą pozycję po końcu rzędu.
        // Pusty rząd jest interpretowany jako jedno pole zawierające pusty String.
        //--------------------------------------------------------------------------------------------------------------
        while (iIndex < szLine.length() + 1) {
            // Pole zaczynające się od cudzysłowu. Jego koniec determinuje drugi cudzysłów i separator lub koniec rzędu
            // po nim. W tym polu mogą się znajdować separatory, znaki końca linii, a także cudzysłowy (escape'owane
            // w stylu SQL, tzn. pojedynczy cudzysłów reprezentowany jest przez dwa cudzysłowy obok siebie).
            //----------------------------------------------------------------------------------------------------------
            if (iIndex < szLine.length() && szLine.charAt(iIndex) == this.quotationMark)
            {
                szQuotedField = "";
                iIndex++;
                iStartIndex = iIndex;
                while (true) {
                    // Koniec linii w niezamkniętych cudzysłowach - wczytaj następną ze strumienia
                    if (iIndex == szLine.length()) {
                        szAppendLine = this.textReader.readLine();
                        if (szAppendLine == null) {
                            throw new CSVFormatException("Unclosed quoted field - unexpected end of stream");
                        }
                        szLine += "\n";
                        szLine += szAppendLine;
                    } else if (szLine.charAt(iIndex) == this.quotationMark) {
                        iIndex++;
                        // Escape'owany cudzysłów - przeskocz i dopisz fragment do pola
                        if (iIndex != szLine.length() && szLine.charAt(iIndex) == this.quotationMark) {
                            szQuotedField += szLine.substring(iStartIndex, iIndex);
                            iStartIndex = iIndex + 1;
                        }
                        // Cudzysłów kończący - dodaj polę do listy i przerwij pęltę
                        else {
                            szQuotedField += szLine.substring(iStartIndex, iIndex - 1);
                            fieldList.add(szQuotedField);
                            break;
                        }
                    }
                    iIndex++;
                }
                // Po cudzysłowie zamykającym powinien być przecinek (lub koniec rzędu). Pomiń go
                if (iIndex != szLine.length() && szLine.charAt(iIndex) != this.separator) {
                    throw new CSVFormatException("Quoted field should be followed by separator or (CR)LF");
                }
                iIndex++;
            }
            // Pole niezaczynające się od cudzysłowu. Jego koniec determinuje koniec rzędu lub separator.
            //----------------------------------------------------------------------------------------------------------
            else
            {
                iStartIndex = iIndex;
                // Szukaj końca pola (przecinek lub koniec rzędu). Cudzysłowy niedozwolone
                while (iIndex != szLine.length() && szLine.charAt(iIndex) != this.separator) {
                    if (szLine.charAt(iIndex) == this.quotationMark) {
                        throw new CSVFormatException("Fields with quotation mark literals should be quoted");
                    }
                    iIndex++;
                }
                fieldList.add(szLine.substring(iStartIndex, iIndex));
                iIndex++;
            }
        }

        return fieldList;
    }

    /**
     * Metoda zwraca tablicę z nazwami kolumn.
     * @return tablica {@code String}ów ze sprecyzowanymi nazwami kolumn w nagłówku lub {@code null}, gdy brak nagłówka
     */
    public String[] getColumnNames()
    {
        if (this.columnNames == null)
            return null;
        return this.columnNames.clone();
    }

    /**
     * Metoda pozwala sprawdzić, czy zadeklarowano, że dane posiadają nagłówek.
     * @return {@code true}, jeśli dane posiadają nagłówek, {@code false} w przeciwnym wypadku
     */
    public boolean hasHeader()
    {
        return this.hasHeader;
    }

    /**
     * Metoda zwraca znak separatora w strumieniu danych.
     * @return używany znak separatora w strumieniu danych
     */
    public char getSeparator()
    {
        return this.separator;
    }

    /**
     * Metoda zwraca znak cudzysłowu w strumieniu danych.
     * @return używany znak cudzysłowu w strumieniu danych
     */
    public char getQuotationMark()
    {
        return this.quotationMark;
    }

    /**
     * Metoda zamyka strumień danych podany w konstruktorze.
     * @throws IOException zrzucone przez {@link Reader#close} w przypadku blędu IO
     */
    public void close() throws IOException
    {
        this.textReader.close();
    }
}
