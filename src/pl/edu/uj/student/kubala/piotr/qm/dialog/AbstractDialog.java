// QuickMean - AbstractDialog.java
//---------------------------------------------------------------------
// [opis pliku]
//---------------------------------------------------------------------
// Utworzono 14:14 03.07.2017 w IntelliJ IDEA
// (C)PKua, wszystkie prawa zastrzeżone
//---------------------------------------------------------------------

package pl.edu.uj.student.kubala.piotr.qm.dialog;

import pl.edu.uj.student.kubala.piotr.qm.EDTInitializable;
import pl.edu.uj.student.kubala.piotr.qm.EDTInitializationManager;

import javax.swing.*;
import java.awt.*;

public abstract class AbstractDialog extends JDialog implements EDTInitializable
{
    private Frame parentFrame;

    public AbstractDialog(Frame owner, String title)
    {
        super(owner, title);
        this.parentFrame = owner;
        EDTInitializationManager manager = EDTInitializationManager.getInstance();
        manager.registerElement(this);
        if (parentFrame != null && parentFrame instanceof EDTInitializable)
            manager.addDependency(this, (EDTInitializable)parentFrame);
    }

    public abstract void init();

    public Frame getParentFrame() {
        return parentFrame;
    }
}
