// QuickMean - GroupController.java
//---------------------------------------------------------------------
// Kontroler tabelki z grupami oraz listy rozwijanej w głównym panelu.
//---------------------------------------------------------------------
// Utworzono 20:40 02.05.2017 w IntelliJ IDEA
// (C)PKua, wszystkie prawa zastrzeżone
//---------------------------------------------------------------------

package pl.edu.uj.student.kubala.piotr.qm;

import pl.edu.uj.student.kubala.piotr.qm.lab.LabProject;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class GroupController implements Controller
{
    private LabProject      labProject;
    private GroupDisplay    groupDisplay;

    /**
     * Konstruktor kontrolera okna z grupami przyjmujący model i widok
     * @param labProject - projekt laboratorium (model)
     * @param groupDisplay - widok okna z grupami
     */
    public GroupController(LabProject labProject, GroupDisplay groupDisplay) {
        this.labProject = labProject;
        this.groupDisplay = groupDisplay;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {

    }
}
