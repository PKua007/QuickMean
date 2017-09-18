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
import pl.edu.uj.student.kubala.piotr.qm.QuickFrame;
import pl.edu.uj.student.kubala.piotr.qm.lab.LabProject;
import pl.edu.uj.student.kubala.piotr.qm.lab.Series;
import pl.edu.uj.student.kubala.piotr.qm.lab.SeriesGroup;
import pl.edu.uj.student.kubala.piotr.qm.utils.Utils;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.event.ActionEvent;

public class GroupDialogController implements Controller
{
    private static final String GROUP_NAME_ALREADY_EXISTS_MESSAGE =
            "Istenieje już grupa o podanej nazwie. Nazwy grup muszą być unikatowe.";
    private GroupDialog groupDialog;

    private AbstractAction addAction;
    private AbstractAction changeAction;

    /* Akcja zatwierdzenia nowej grupy do dodania */
    private class AddAction extends AbstractAction
    {
        public AddAction(String name) {
            super(name);
            this.enabled = false;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            LabProject project = groupDialog.getLabProject();
            JTextField field = groupDialog.getNameInput();
            String new_name = field.getText();
            if (new_name == null)
                return;
            if (new_name.isEmpty())
                return;
            if (project.getSeriesGroupByName(new_name) != null) {
                JOptionPane.showMessageDialog(groupDialog,
                        GROUP_NAME_ALREADY_EXISTS_MESSAGE,
                        QuickFrame.ERROR_TITLE,
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            // Utwórz nową grupę, a w niej pierwszą serię
            SeriesGroup group = new SeriesGroup(new_name);
            Series first_series = new Series();
            group.addElement(first_series);
            group.setHighlightedSeries(0);
            int idx = project.addElement(group);
            project.setSelectedSeriesGroup(idx);
            groupDialog.setVisible(false);
        }
    }

    /* Akcja zatwierdzenia nowej nazwy grupy */
    private class ChangeAction extends AbstractAction
    {
        public ChangeAction(String name) {
            super(name);
            this.enabled = false;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            LabProject project = groupDialog.getLabProject();
            SeriesGroup sel_group = groupDialog.getEditedGroup();

            // Pobierz nową nazwę i sprawdź, czy poprawna
            String new_name = groupDialog.getNameInput().getText();
            if (new_name == null)
                return;
            if (new_name.isEmpty())
                return;
            // Sprawdź, czy nieużywana
            SeriesGroup found_group = project.getSeriesGroupByName(new_name);
            if (found_group != null && found_group != sel_group) {
                JOptionPane.showMessageDialog(groupDialog,
                        GROUP_NAME_ALREADY_EXISTS_MESSAGE,
                        QuickFrame.ERROR_TITLE,
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            // Wszystko ok - zmień nazwę
            sel_group.setName(new_name);
            groupDialog.setVisible(false);
        }
    }

    /* Akcja anulowania */
    private class CancelAction extends AbstractAction
    {
        public CancelAction(JButton source)
        {
            Utils.copyButtonAction(source, this);
        }

        @Override
        public void actionPerformed(ActionEvent e)
        {
            groupDialog.setVisible(false);
        }
    }


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
    public void init()
    {
        Handler handler = new Handler();
        this.addAction = new AddAction(GroupDialog.ADD);
        this.changeAction = new ChangeAction(GroupDialog.CHANGE);
        CancelAction cancel_action = new CancelAction(this.groupDialog.getCancelButton());

        this.groupDialog.setAddAction(this.addAction);
        this.groupDialog.setChangeAction(this.changeAction);
        this.groupDialog.getCancelButton().setAction(cancel_action);
        this.groupDialog.getNameInput().getDocument().addDocumentListener(handler);
    }

    private class Handler implements DocumentListener
    {
        public void fieldChanged()
        {
            JTextField field = groupDialog.getNameInput();
            switch (groupDialog.getMode()) {
                case ADD:
                    addAction.setEnabled(!field.getText().isEmpty());
                    break;
                case EDIT:
                    changeAction.setEnabled(!field.getText().isEmpty());
                    break;
            }
        }

        @Override
        public void insertUpdate(DocumentEvent e) {
            fieldChanged();
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            fieldChanged();
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            fieldChanged();
        }
    }
}
