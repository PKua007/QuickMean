// QuickMean - PropagatingModel.java
//---------------------------------------------------------------------
// Rozszerzenie modelu, które implementuje PropartyChangeListener
// i wyrzuca "w górę" odebrane zdarzenia
//---------------------------------------------------------------------
// Utworzono 13:12 28.05.2017 w IntelliJ IDEA
// (C)PKua, wszystkie prawa zastrzeżone
//---------------------------------------------------------------------

package pl.edu.uj.student.kubala.piotr.qm.lab;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public abstract class PropagatingModel extends Model implements PropertyChangeListener
{
    @Override
    public void propertyChange(PropertyChangeEvent evt)
    {
        this.propertyFirer.firePropertyChange(evt);
    }
}
