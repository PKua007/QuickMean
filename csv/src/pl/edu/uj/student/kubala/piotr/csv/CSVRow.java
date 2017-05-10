// CW 2016.12.21 - [nazwa pliku]
//---------------------------------------------------------------------
// [opis pliku]
//---------------------------------------------------------------------
// Utworzono 15:20 02.01.2017
// (C)PKua, wszystkie prawa zastrzeżone
//---------------------------------------------------------------------

package pl.edu.uj.student.kubala.piotr.csv;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Odczytany ze strumienia danych CSV rząd. Umożliwia dostęp do pól poprzez indeks bądź nazwę kolumny, jeśli dane
 * posiadają nagłówek.
 */
public class CSVRow implements Iterable<String>
{
    private ArrayList<String> fields;       // ArrayLista z polami
    private HashMap<String, Integer> columnNamesMap;    // Mapa przypisująca nazwom kolumn indeksy w powyższej liście

    /* Konstruktor tworzący rząd bez nagłówka */
    CSVRow(ArrayList<String> fields)
    {
        this.fields = fields;
        this.columnNamesMap = null;
    }

    /* Konstruktor tworzący rząd z nagłówkiem. Nazwy kolumn w odpowiedniej kolejności znajdują się w columNames */
    CSVRow(String [] columnNames, ArrayList<String> fields)
    {
        this.fields = fields;

        // Efektywna pojemność początkowa. Nie powinna nastąpić realokacja
        // http://blog.scottlogic.com/2012/08/14/efficient-map-initialization-in-java.html
        this.columnNamesMap = new HashMap<>((int)Math.ceil(columnNames.length / 0.75));
        for (int i = 0; i < columnNames.length; i++)
            columnNamesMap.put(columnNames[i], i);
    }

    /**
     * Metoda pobiera zawartość pola o podanym indeksie.
     * @param index indeks pola z przedziału [0, size() - 1]
     * @return zawartość pola
     * @throws CSVNoSuchFieldException jeśli indeks jest spoza dozwolonego zakresu
     */
    public String get (int index) throws CSVNoSuchFieldException
    {
        if (index < 0 || index >= this.fields.size())
            throw new CSVNoSuchFieldException("No such field of index " + index + " in CSVRow");

        return fields.get(index);
    }

    /**
     * Metoda pobiera zawartość pola o podanej nazwie.
     * @param fieldName nazwa pola sprecyzowana w nagłówku danych
     * @return zawartość pola
     * @throws CSVNoSuchFieldException jeśli nie istnieje pole o podanej nazwie
     */
    public String get (String fieldName) throws CSVNoSuchFieldException
    {
        Integer iFieldIndex;
        if (this.columnNamesMap == null || (iFieldIndex = this.columnNamesMap.get(fieldName)) == null)
            throw new CSVNoSuchFieldException("No such field in CSVRow: " + fieldName);

        return this.fields.get(iFieldIndex);
    }

    /**
     * Metoda zwraca ilość pól w rzędzie.
     * @return ilość pól w rzędzie
     */
    public int size()
    {
        return this.fields.size();
    }

    /**
     * Metoda zwraca iterator po wszystkich polach.
     * @return iterator po wszystkich polach
     */
    public Iterator<String> iterator()
    {
        return this.fields.iterator();
    }

    /**
     * Metoda sprawdza, czy w rzędzie występują pola o wszystkich podanych w tablicy nazwach.
     * @param fields tablica z polami
     * @return {@code true}, jeśli wszystkie podane pola występują, {@code false} w przeciwnym wypadku
     */
    public boolean containsFields(String [] fields)
    {
        if (this.columnNamesMap == null || fields.length == 0)
            return false;

        for (String fieldName : fields)
            if (!this.columnNamesMap.containsKey(fieldName))
                return false;

        return true;
    }

    /**
     * Metoda sprawdza, czy w rzędzie występuje pole o podanej nazwie.
     * @param field nazwa pola
     * @return {@code true}, jeśli pole występuje, {@code false} w przeciwnym wypadku
     */
    public boolean containsField(String field)
    {
        return this.columnNamesMap != null && this.columnNamesMap.containsKey(field);
    }
}