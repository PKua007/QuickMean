// QuickMean - Model.java
//---------------------------------------------------------------------
// [opis pliku]
//---------------------------------------------------------------------
// Utworzono 14:54 11.05.2017 w IntelliJ IDEA
// (C)PKua, wszystkie prawa zastrzeżone
//---------------------------------------------------------------------

package pl.edu.uj.student.kubala.piotr.qm;

import javax.swing.event.SwingPropertyChangeSupport;
import java.beans.PropertyChangeListener;

public abstract class Model
{
    protected SwingPropertyChangeSupport    propertyFirer;

    protected Model()
    {
        propertyFirer = new SwingPropertyChangeSupport(this);
    }

    public void addPropertyChangeListener(PropertyChangeListener listener)
    {
        propertyFirer.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener)
    {
        propertyFirer.removePropertyChangeListener(listener);
    }
}
