// QuickMean - GroupDialog.java
//---------------------------------------------------------------------
// [opis pliku]
//---------------------------------------------------------------------
// Utworzono 13:50 03.07.2017 w IntelliJ IDEA
// (C)PKua, wszystkie prawa zastrzeżone
//---------------------------------------------------------------------

package pl.edu.uj.student.kubala.piotr.qm.dialog;

import pl.edu.uj.student.kubala.piotr.qm.EDTInitializable;
import pl.edu.uj.student.kubala.piotr.qm.EDTInitializationManager;
import pl.edu.uj.student.kubala.piotr.qm.QuickFrame;
import pl.edu.uj.student.kubala.piotr.qm.lab.LabProject;
import pl.edu.uj.student.kubala.piotr.qm.lab.SeriesGroup;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;

public class GroupDialog extends AbstractDialog {
    private static final String     TITLE = "Edycja grupy";
    private static final String     ADD_TITLE = "Dowawanie grupę";
    private static final String     EDIT_TITLE = "Edycja nazwy grupy";
    public static final String      ADD = "Dodaj";
    public static final String      CHANGE = "Zmień";
    public static final String      CANCEL = "Anuluj";
    private static final String     ADD_TEXT = "Podaj nazwę nowej grupy:";
    private static final String     CHANGE_TEXT = "Podaj nową nazwę grupy:";

    private static final Insets TEXT_FIELD_INSETS = new Insets(10, 10, 10 ,10);
    private static final int    BUTTONS_PADDING = 10;
    private static final int    SIDES_PADDING = 10;
    private static final int    WIDTH = 300;

    public enum Mode {
        NONE,
        ADD,
        EDIT
    }

    private Frame       parent;
    private SeriesGroup editedGroup;
    private Mode        mode;
    private LabProject  labProject;

    private JLabel      label;
    private JTextField  nameInput;
    private JButton     okButton;
    private JButton     cancelButton;

    private Action      addAction;
    private Action      changeAction;

    public GroupDialog(QuickFrame owner) {
        super(owner, TITLE);
        this.parent = owner;
        this.mode = Mode.NONE;
        this.labProject = owner.getLabProject();

        // Utwórz kontroler. On już sam sobie poradzi, zostaw go cholero w spokoju
        new GroupDialogController(this);
    }

    public void showAddDialog()
    {
        this.setTitle(ADD_TITLE);
        this.okButton.setAction(this.addAction);
        this.label.setText(ADD_TEXT);
        this.okButton.setText(ADD);
        this.okButton.setMnemonic(KeyEvent.VK_D);
        this.mode = Mode.ADD;
        this.nameInput.setText("");
        this.editedGroup = null;
        this.setVisible(true);
    }

    public void showEditDialog(SeriesGroup group)
    {
        this.setTitle(EDIT_TITLE);
        this.okButton.setAction(this.changeAction);
        this.label.setText(CHANGE_TEXT);
        this.okButton.setText(CHANGE);
        this.okButton.setMnemonic(KeyEvent.VK_Z);
        this.mode = Mode.EDIT;
        this.nameInput.setText(group.getName());
        this.editedGroup = group;
        this.setVisible(true);
    }

    @Override
    public void init()
    {
        GridBagLayout layout = new GridBagLayout();
        GridBagConstraints constraints;
        this.setLayout(layout);

        // Tekst informujący
        this.label = new JLabel(" ");
        constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridwidth = 2;
        constraints.weightx = 0.5;
        constraints.weighty = 0.5;
        constraints.anchor = GridBagConstraints.NORTHWEST;
        constraints.insets = new Insets(SIDES_PADDING, SIDES_PADDING, 0, 0);
        this.add(this.label, constraints);

        // Polę na nazwę
        this.nameInput = new JTextField();
        constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.gridwidth = 2;
        constraints.weightx = 0.5;
        constraints.weighty = 0.5;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.CENTER;
        constraints.insets = TEXT_FIELD_INSETS;
        this.add(this.nameInput, constraints);

        // Przycisk Dodaj/Zmień
        this.okButton = new JButton(" ");
        constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 2;
        constraints.gridwidth = 1;
        constraints.weightx = 0.5;
        constraints.weighty = 0.5;
        constraints.anchor = GridBagConstraints.SOUTHEAST;
        constraints.insets = new Insets(0, SIDES_PADDING, BUTTONS_PADDING, BUTTONS_PADDING / 2);
        constraints.fill = GridBagConstraints.NONE;
        this.add(this.okButton, constraints);

        // Przycisk Anuluj
        this.cancelButton = new JButton(CANCEL);
        this.cancelButton.setMnemonic(KeyEvent.VK_A);
        constraints = new GridBagConstraints();
        constraints.gridx = 1;
        constraints.gridy = 2;
        constraints.weightx = 0.5;
        constraints.weighty = 0.5;
        constraints.anchor = GridBagConstraints.SOUTHWEST;
        constraints.insets = new Insets(0, BUTTONS_PADDING / 2, BUTTONS_PADDING, SIDES_PADDING);
        constraints.fill = GridBagConstraints.NONE;
        this.add(this.cancelButton, constraints);

        this.getRootPane().setDefaultButton(this.okButton);

        // Upakuj i ustal sensowna szerokość
        this.pack();
        this.setMinimumSize(new Dimension(WIDTH, this.getHeight()));
        this.setModal(true);
        this.setLocationRelativeTo(this.parent);
        this.setVisible(false);
    }

    @Override
    public String getElementName() {
        return "GroupDialog";
    }

    public void setAddAction(Action addAction) {
        this.addAction = addAction;
    }

    public void setChangeAction(Action changeAction) {
        this.changeAction = changeAction;
    }

    public JTextField getNameInput() {
        return nameInput;
    }

    public JButton getOkButton() {
        return okButton;
    }

    public JButton getCancelButton() {
        return cancelButton;
    }

    public Mode getMode() {
        return mode;
    }

    public SeriesGroup getEditedGroup() {
        return editedGroup;
    }

    public LabProject getLabProject() {
        return labProject;
    }
}
