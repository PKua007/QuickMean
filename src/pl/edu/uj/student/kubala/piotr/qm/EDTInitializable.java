// QuickMean - EDTInitializable.java
//---------------------------------------------------------------------
// [opis pliku]
//---------------------------------------------------------------------
// Utworzono 16:09 22.07.2017 w IntelliJ IDEA
// (C)PKua, wszystkie prawa zastrzeżone
//---------------------------------------------------------------------

package pl.edu.uj.student.kubala.piotr.qm;

/**
 * Interfejs elementu, który ma zostać zainicjowany na EDT.
 */
public interface EDTInitializable
{
    void init();
    String getElementName();
}
