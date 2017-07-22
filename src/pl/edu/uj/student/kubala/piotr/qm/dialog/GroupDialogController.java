// QuickMean - GroupDialogController.java
//---------------------------------------------------------------------
// [opis pliku]
//---------------------------------------------------------------------
// Utworzono 13:58 03.07.2017 w IntelliJ IDEA
// (C)PKua, wszystkie prawa zastrzeżone
//---------------------------------------------------------------------

package pl.edu.uj.student.kubala.piotr.qm.dialog;

import pl.edu.uj.student.kubala.piotr.qm.Controller;
import pl.edu.uj.student.kubala.piotr.qm.EDTInitializationManager;

public class GroupDialogController implements Controller
{
    private GroupDialog groupDialog;

    public GroupDialogController(GroupDialog groupDialog) {
        this.groupDialog = groupDialog;
        EDTInitializationManager manager = EDTInitializationManager.getInstance();
        manager.registerElement(this);
        manager.addDependency(this, groupDialog);
    }

    @Override
    public String getElementName() {
        return "GroupDialogController";
    }

    @Override
    public void init() {

    }
}
