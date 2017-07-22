// QuickMean - GroupDialog.java
//---------------------------------------------------------------------
// [opis pliku]
//---------------------------------------------------------------------
// Utworzono 13:50 03.07.2017 w IntelliJ IDEA
// (C)PKua, wszystkie prawa zastrzeżone
//---------------------------------------------------------------------

package pl.edu.uj.student.kubala.piotr.qm.dialog;

import javax.swing.*;
import java.awt.*;

public class GroupDialog extends AbstractDialog
{
    private static final String TITLE = "Edycja grupy";

    public enum Mode {
        ADD,
        EDIT
    }

    private Frame   parent;
    private String  chosenName;

    public GroupDialog(Frame owner)
    {
        super(owner, TITLE);
        this.parent = owner;

        GroupDialogController controller = new GroupDialogController(this);
    }



    @Override
    public void init()
    {
        this.setModal(true);
        this.setLocationRelativeTo(this.parent);
        this.setVisible(true);
    }

    @Override
    public String getElementName() {
        return "GroupDialog";
    }

    public String getChosenName() {
        return this.chosenName;
    }
}
