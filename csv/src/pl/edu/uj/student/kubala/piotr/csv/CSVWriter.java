// CW 2016.12.21 - [nazwa pliku]
//---------------------------------------------------------------------
// [opis pliku]
//---------------------------------------------------------------------
// Utworzono 13:19 28.12.2016
// (C)PKua, wszystkie prawa zastrzeżone
//---------------------------------------------------------------------

package pl.edu.uj.student.kubala.piotr.csv;

import java.io.*;
import java.util.*;

/**
 * <p>{@code Writer} umożliwiający zapisanie danych CSV zgodnych ze standardem RFC 4180 do przekazanego strumienia
 * znakowego. Dane mogą posiadać nagłówek bądź nie, a także można wybrać inny separator i cudzysłów niż domyślne
 * ({@link CSVWriter#DEFAULT_SEPARATOR}, {@link CSVWriter#DEFAULT_QUOTATION_MARK}).
 *
 * <p>Przykładowe użycie:
 * <blockquote><pre>
 * String [] columnNames = {"name", "surname", "age"};
 * try {
 *     CSVWriter writer = new CSVWriter(new OutputStreamWriter(System.out), columnNames);
 *     writer.writeRow(new Object[]{"Piotr", "Kubala", 20});
 *     writer.writeRow(new Object[]{"Jan", "Kowalski", 25});
 * } catch (IOException e) {
 *     e.printStackTrace();
 * }</pre></blockquote>
 *
 * <p> wypisze na standardowe wyjście:
 * <blockquote><pre>
 * name,surname,age
 * Piotr,Kubala,20
 * Jan,Kowalski,25
 * </pre></blockquote>
 */
public class CSVWriter implements Closeable
{
    /** Domyślny separator pól w rzędzie - przecinek {@code ,}*/
    public static final char    DEFAULT_SEPARATOR = ',';
    /** Domyślny cudzysłów do otaczania pól - znak cudzysłowu {@code "} */
    public static final char    DEFAULT_QUOTATION_MARK = '"';

    private char        separator;
    private char        quotationMark;
    private int         numColumns = 0;
    private String []   columnNames;

    private Writer textWriter;

    /**
     * Konstruktor przyjmujące wszystkie parametry specyfikujące wyjściowe dane.
     * @param textWriter strumień znakowy, do którego dane mają zostać zapisane
     * @param columnNames tablica z nazwami kolumn w nagłówku lub null, jeśli nagłówka ma nie być. Nagłówek zostaje
     *                    zapisany do strumienia w konstruktorze
     * @param separator znak separatora pól
     * @param quotationMark znak cudzysłowu
     * @throws IllegalArgumentException jeśli:
     * <ul>
     *     <li>znak separatora jest identyczny jak cudzysłowu</li>
     *     <li>znak separatora lub cudzysłowu nie jest nie-kontrolnym znakiem z podstawowej tablicy ASCII (znaki 32 - 126 włącznie)</li>
     *     <li>nie wszystkie nazwy kolumn są unikalne i niepuste</li>
     * </ul>
     * @throws IOException jeśli podczas zapisywania nagłówka textWriter zrzuci wyjątek IO
     */
    public CSVWriter(Writer textWriter, String [] columnNames, char separator, char quotationMark) throws IOException
    {
        if (separator == quotationMark)
            throw new IllegalArgumentException("Separator and quotation mark should be different characters");

        if (separator < 32 || separator > 126)
            throw new IllegalArgumentException("Separator should be basic-ASCII non-control character");

        if (quotationMark < 32 || quotationMark > 126)
            throw new IllegalArgumentException("Quotation mark should be basic-ASCII non-control character");

        constructWriter(textWriter, columnNames, separator, quotationMark);
    }

    /* Wewnętrzna metoda tworząca CSVWritera */
    private void constructWriter(Writer textWriter, String [] columnNames, char separator, char quotationMark) throws IOException
    {
        if (columnNames != null)
        {
            TreeSet<String> columnSet = new TreeSet<>(Arrays.asList(columnNames));
            // Sprawdź, czy nie podano pustej kolumny
            if (columnSet.contains(""))
                throw new IllegalArgumentException("Header must note have empty columns");
            // Sprawdź, czy wszystkie nazwy są unikalne
            if (columnSet.size() != columnNames.length)
                throw new IllegalArgumentException("Column names aren't unique");
        }

        this.textWriter = textWriter;
        this.columnNames = columnNames;
        this.separator = separator;
        this.quotationMark = quotationMark;

        // Jeśli plik ma nagłówek, zapisz go
        if (columnNames != null)
        {
            this.numColumns = columnNames.length;
            this.writeRow(columnNames);
        }
    }

    /**
     * Konstruktor przyjmujące tylko strumień znakowy do zapisania danych, zakładający domyślne wartości - brak
     * nagłówka, domyślny separator ({@link CSVWriter#DEFAULT_SEPARATOR}), domyślny znak cudzysłowu
     * ({@link CSVWriter#DEFAULT_QUOTATION_MARK}).
     * @param textWriter strumień znakowy, do którego dane mają zostać zapisane
     */
    public CSVWriter(Writer textWriter)
    {
        try {
            constructWriter(textWriter, null, DEFAULT_SEPARATOR, DEFAULT_QUOTATION_MARK);
        } catch (IOException e) {
            // Wyjątek nie powinien zostać zrzucony
            throw new RuntimeException("CSVWriter: unknown error");
        }
    }

    /**
     * Konstruktor przyjmujący strumień znakowy do zapisania danych i nazwy kolumn w nagłówku. Zakłada domyślny
     * separator ({@link CSVWriter#DEFAULT_SEPARATOR}) i domyślny znak cudzysłowu
     * {@link CSVWriter#DEFAULT_QUOTATION_MARK}).
     * @param textWriter strumień znakowy, do którego dane majązostać zapisane
     * @param columnNames tablica z nazwami kolumn w nagłówku lub null, jeśli nagłówka ma nie być. Nagłówek zostaje
     *                    zapisany do strumienia w konstruktorze
     * @throws IllegalArgumentException jeśli nie wszystkie nazwy kolumn są unikalne i niepuste
     * @throws IOException jeśli podczas zapisywania nagłówka textWriter zrzuci wyjątek IO
     */
    public CSVWriter(Writer textWriter, String [] columnNames) throws IOException
    {
        constructWriter(textWriter, columnNames, DEFAULT_SEPARATOR, DEFAULT_QUOTATION_MARK);
    }

    /**
     * Metoda zapisuje do strumienia danych CSV nowy rząd.
     * @param row tablica obiektów pól do zapisania. Konwertowane są na ciągi znaków za pomocą własnej metody toString()
     * @throws IllegalArgumentException jeśli row jest równe null lub przekazano pustą tablicę
     * @throws CSVFormatException jeśli w przekazanym rzędzie danych jest inna liczba elementów niż w poprzednich
     * @throws IOException jeśli podczas zapisu do strumienia zostanie zrzucony wyjątek IO
     */
    public void writeRow(Object [] row) throws IOException
    {
        if (row == null || row.length == 0)
            throw new IllegalArgumentException("Row must not be empty");

        if (this.numColumns == 0)
            this.numColumns = row.length;
        else if (this.numColumns != row.length)
            throw new CSVFormatException("Given row has differend field count than previous rows");

        for (int i = 0; i < this.numColumns - 1; i++)
        {
            this.writeField(row[i].toString());
            this.textWriter.write(this.separator);
        }

        this.writeField(row[this.numColumns - 1].toString());
        this.textWriter.write(System.getProperty("line.separator"));
        this.textWriter.flush();
    }

    /* Metoda zapisuje pole (bez przecinka) otoczone cudzysłowami i z escape'owanymi znakami (jeśli potrzeba) */
    private void writeField(String field) throws IOException
    {
        // Sprawdź, czy pola nie trzeba otoczyć cudzysłowami. Escape'uj cudzysłowy wewnątrz
        if (field.indexOf(this.separator) != -1 ||
                field.indexOf(this.quotationMark) != -1 ||
                field.indexOf('\r') != -1 ||
                field.indexOf('\n') != -1)
        {
            field = this.quotationMark + field.replace(String.valueOf(this.quotationMark), String.valueOf(new char[]{this.quotationMark, this.quotationMark})) + this.quotationMark;
        }

        this.textWriter.write(field);
    }

    /**
     * Metoda zapisuje do strumienia danych CSV nowy rząd.
     * @param row lista obiektów pól do zapisania. Konwertowane są na ciągi znaków za pomocą własnej metody toString()
     * @throws IllegalArgumentException jeśli row jest równe null lub przekazano pustą listę
     * @throws CSVFormatException jeśli w przekazanym rzędzie danych jest inna liczba elementów niż w poprzednich
     * @throws IOException jeśli podczas zapisu do strumienia zostanie zrzucony wyjątek IO
     */
    public void writeRow(List<?> row) throws IOException
    {
        if (row == null || row.size() == 0)
            throw new IllegalArgumentException("Row must not be empty");

        if (this.numColumns == 0)
            this.numColumns = row.size();
        else if (this.numColumns != row.size())
            throw new CSVFormatException("Given row has differend field count than previous rows");

        Object [] rowArray = new Object[row.size()];
        for (int i = 0; i < this.numColumns; i++)
            rowArray[i] = row.get(i);
        this.writeRow(rowArray);
    }

    /**
     * Metoda zapisuje do strumienia danych CSV zawierających nagłówek nowy rząd.
     * @param row mapa obiektów pól do zapisania. Jeśli pole z kolumny o danej nazwie nie zostanie w niej znalezione,
     *            zapisywane jest jako puste. Obiekty konwertowane są na ciągi znaków za pomocą własnej metody
     *            toString()
     * @throws IllegalArgumentException jeśli row jest równe null lub przekazano pustą mapę
     * @throws CSVFormatException jeśli strumień danych nie posiada nagłówka
     * @throws IOException jeśli podczas zapisu do strumienia zostanie zrzucony wyjątek IO
     */
    public void writeRow(Map<? extends String, ?> row) throws IOException
    {
        if (row == null || row.size() == 0)
            throw new IllegalArgumentException("Row must not be empty");

        if (!this.hasHeader())
            throw new CSVFormatException("CSV stream declared as headerless; cannot write mapped row");

        Object [] rowArray = new Object[this.numColumns];
        Object field;

        for (int i = 0; i < this.numColumns; i++)
        {
            field = row.get(this.columnNames[i]);
            if (field == null)
                field = "";
            rowArray[i] = field;
        }
        this.writeRow(rowArray);
    }

    /**
     * Metoda zwraca tablicę z nazwami kolumn w nagłówku.
     * @return tablica z nazwami kolumn
     */
    public String[] getColumnNames()
    {
        if (this.columnNames == null)
            return null;
        return columnNames.clone();
    }

    /**
     * Metoda zwraca liczbę kolumn w danych.
     * @return liczba kolumn w danych lub 0, jeśli jeszcze nie sprecyzowane
     */
    public int getNumColumns()
    {
        return this.numColumns;
    }

    /**
     * Metoda pozwala sprawdzić, czy dane mają nagłówek.
     * @return true, jeśli dane posiadają nagłówek, false w przeciwnym wypadku
     */
    public boolean hasHeader()
    {
        return this.columnNames != null;
    }

    /**
     * Metoda zwraca znak separatora danych.
     * @return znak separatora danych
     */
    public char getSeparator()
    {
        return this.separator;
    }

    /**
     * Metoda zwraca znak cudzysłowu.
     * @return znak cudzysłowu
     */
    public char getQuotationMark()
    {
        return this.quotationMark;
    }

    /**
     * Metoda zamyka przekazany w konstruktorze strumień znakowy.
     * @throws IOException zrzucone przez {@link Writer#close()}
     */
    public void close() throws IOException {
        textWriter.close();
    }
}
