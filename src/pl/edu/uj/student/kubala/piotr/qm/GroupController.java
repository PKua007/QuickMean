// QuickMean - GroupController.java
//---------------------------------------------------------------------
// Kontroler tabelki z grupami oraz listy rozwijanej w głównym panelu.
//---------------------------------------------------------------------
// Utworzono 20:40 02.05.2017 w IntelliJ IDEA
// (C)PKua, wszystkie prawa zastrzeżone
//---------------------------------------------------------------------

package pl.edu.uj.student.kubala.piotr.qm;

import pl.edu.uj.student.kubala.piotr.qm.lab.LabProject;
import pl.edu.uj.student.kubala.piotr.qm.lab.Series;
import pl.edu.uj.student.kubala.piotr.qm.lab.SeriesGroup;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import java.awt.event.*;
import java.util.stream.IntStream;

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

        EDTInitializationManager manager = EDTInitializationManager.getInstance();
        manager.registerElement(this);
        manager.addDependency(this, groupDisplay);
    }

    @Override
    public void init() {
        Handler handler = new Handler();
        JButton btn;

        // Ustaw nasłuchiwanie na listę grup
        this.groupDisplay.getGroupList().addItemListener(handler);
        // Ustaw nasłuchiwanie na zaznaczanie w tabelce
        this.groupDisplay.getGroupTable().getSelectionModel().addListSelectionListener(handler);
        // Ustaw nasłuchiwanie na przycisk edycji
        btn = this.groupDisplay.getEditButton();
        btn.setAction(new EditAction(btn.getText()));
        // Ustaw nasłuchiwanie na przycisk usunięcia
        btn = this.groupDisplay.getDeleteButton();
        btn.setAction(new DeleteAction(btn.getText()));
        // Ustaw nasłuchiwanie na przycisk dodawania
        btn = this.groupDisplay.getAddButton();
        btn.setAction(new AddAction(btn.getText()));
        // Ustaw nasłuchiwanie na model tabeli (edycja nazw serii)
        this.groupDisplay.getGroupTable().getModel().addTableModelListener(handler);
    }

    @Override
    public String getEDTInitializableName() {
        return "GroupController";
    }

    /* Akcja usuwania grupy - przycisk "X" */
    private class DeleteAction extends AbstractAction
    {
        public DeleteAction(String name) {
            super(name);
            this.enabled = false;
        }

        @Override
        public void actionPerformed(ActionEvent e) {

            if (!groupDisplay.deleteGroupConfirmationDialog())
                return;
            int idx = labProject.getSelectedSeriesGroupIdx();
            SeriesGroup selectedGroup = labProject.getElement(idx);
            if (selectedGroup != null)
                labProject.deleteElement(selectedGroup);
            if (idx - 1 >= 0)
                labProject.setSelectedSeriesGroup(idx - 1);
            else if (labProject.getNumberOfElements() > 0)
                labProject.setSelectedSeriesGroup(0);
        }
    }

    /* Akcja dodawania grupy - przycisk "+" */
    private class AddAction extends AbstractAction
    {
        public AddAction(String name) {
            super(name);
        }

        @Override
        public void actionPerformed(ActionEvent e)
        {
            groupDisplay.getParentFrame().getGroupDialog().showAddDialog();
        }
    }

    /* Akcja edycji nazwy grupy - przycisk z piórem */
    private class EditAction extends AbstractAction
    {
        public EditAction(String name) {
            super(name);
            this.enabled = false;
        }

        @Override
        public void actionPerformed(ActionEvent e)
        {
            SeriesGroup group = labProject.getSelectedSeriesGroup();
            if (group != null)
                groupDisplay.getParentFrame().getGroupDialog().showEditDialog(group);
        }
    }

    /* Klasa wewnętrzna obsługująca zdarzenia */
    private class Handler implements ItemListener, ListSelectionListener, TableModelListener {

        /* Wywoływane, gdy zostani wybrana nowa grupa */
        @Override
        public void itemStateChanged(ItemEvent e) {
            JComboBox<String> list = groupDisplay.getGroupList();

            if (e.getSource() != list)
                return;

            if (e.getStateChange() == ItemEvent.SELECTED) {         // Wybrano inną grupę
                int idx = list.getSelectedIndex();
                if (idx == -1)
                    return;
                labProject.setSelectedSeriesGroup(list.getSelectedIndex());
            }
        }

        @Override
        public void tableChanged(TableModelEvent e) {
            // TODO zmienić na nasłuchiwanie edytora tabeli (żeby zmiana średniej niepotrzebnie nie wywoływała)
            // Zmiana nazwy serii
            if (e.getType() == TableModelEvent.UPDATE) {
                int idx = e.getFirstRow();
                DefaultTableModel model = (DefaultTableModel) e.getSource();
                String newName = (String) model.getValueAt(idx, 0);
                SeriesGroup group = labProject.getSelectedSeriesGroup();
                if (group == null)
                    return;
                Series sel_series = group.getElement(idx);
                sel_series.setLabel(newName);
            }
        }

        /* Wywoływane, gdy zmieni się zaznaczenie w tabelce */
        @Override
        public void valueChanged(ListSelectionEvent e) {
            Object source = e.getSource();
            if (source == groupDisplay.getGroupTable().getSelectionModel()) {
                DefaultListSelectionModel selectionModel = (DefaultListSelectionModel)source;

                if (Main.DEBUG) {
                    /*System.out.println("JTable selection:");
                    System.out.println("isAdjusting: " + selectionModel.getValueIsAdjusting() + "; leadIndex: " + selectionModel.getLeadSelectionIndex());
                    for (int i = selectionModel.getMinSelectionIndex(); i <= selectionModel.getMaxSelectionIndex(); i++)
                        System.out.println(i + " selected: " + selectionModel.isSelectedIndex(i));*/
                }

                // Zignoruj usunięcie zaznaczenia
                if (selectionModel.getLeadSelectionIndex() == -1 || selectionModel.getMinSelectionIndex() == -1)
                    return;

                SeriesGroup selectedGroup = labProject.getSelectedSeriesGroup();
                // Jeżeli pierwsze kliknięcie przy zaznaczaniu, albo puszczenie myszki, zaktualizuj podświetloną serię
                if (!selectedGroup.isSelectingNow() || !selectionModel.getValueIsAdjusting())
                    selectedGroup.setHighlightedSeries(selectionModel.getLeadSelectionIndex());

                // Zaktualizuj zaznaczone serie w grupie
                int [] selectedIndices = IntStream.rangeClosed(selectionModel.getMinSelectionIndex(), selectionModel.getMaxSelectionIndex())
                        .filter(selectionModel::isSelectedIndex)
                        .toArray();
                selectedGroup.setSelectedSeries(selectedIndices);

                // Zaktualizuj informację czy właśnie trwa zaznaczanie
                selectedGroup.setSelectingNow(selectionModel.getValueIsAdjusting());
            }
        }
    }
}
