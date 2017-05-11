// QuickMean - Model.java
//---------------------------------------------------------------------
// [opis pliku]
//---------------------------------------------------------------------
// Utworzono 14:54 11.05.2017 w IntelliJ IDEA
// (C)PKua, wszystkie prawa zastrzeżone
//---------------------------------------------------------------------

package pl.edu.uj.student.kubala.piotr.qm;

import javax.swing.event.SwingPropertyChangeSupport;

public class Model
{
    protected SwingPropertyChangeSupport    propertyFirer;

    protected Model()
    {
        propertyFirer = new SwingPropertyChangeSupport(this);
    }

    public boolean test(Model model) {
        return this == model;
    }
}
