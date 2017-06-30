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
    private static final String GROUP_NAME_ALREADY_EXISTS_MESSAGE =
            "Istenieje już grupa o podanej nazwie. Nazwy grup muszą być unikatowe.";
    private static final String GROUP_NAME_EMPTY_MESSAGE =
            "Nazwa grupy musi być niepusta.";

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
    public void setup() {
        Handler handler = new Handler();

        // Ustaw nasłuchiwanie na listę grup
        this.groupDisplay.getGroupList().addItemListener(handler);
        this.groupDisplay.getGroupList().getEditor().addActionListener(handler);
        // Ustaw nasłuchiwanie na zaznaczanie w tabelce
        this.groupDisplay.getGroupTable().getSelectionModel().addListSelectionListener(handler);
        // Ustaw nasłuchiwanie na przycisk usunięcia
        JButton deleteButton = this.groupDisplay.getDeleteButton();
        deleteButton.setAction(new DeleteAction(deleteButton.getText()));
        // Ustaw nasłuchiwanie na model tabeli (edycja nazw serii)
        this.groupDisplay.getGroupTable().getModel().addTableModelListener(handler);
    }

    /* Akcja usuwania grupy - przycisk "X" */
    private class DeleteAction extends AbstractAction
    {
        public DeleteAction(String name) {
            super(name);
        }

        @Override
        public void actionPerformed(ActionEvent e) {

            if (!groupDisplay.deleteGroupConfirmationDialog())
                return;
            SeriesGroup selectedGroup = labProject.getSelectedSeriesGroup();
            if (selectedGroup != null)
                labProject.deleteChild(selectedGroup);
        }
    }

    /* Klasa wewnętrzna obsługująca zdarzenia */
    private class Handler implements ItemListener, ListSelectionListener, TableModelListener, ActionListener {
        private SeriesGroup oldSelected = null;

        /* Wywoływane, gdy zostani wybrana nowa grupa */
        @Override
        public void itemStateChanged(ItemEvent e) {
            // TODO rozwiązanie tymczasowe zmiany nazwy grupy - przemyśleć i przerobić
            JComboBox<String> list = groupDisplay.getGroupList();

            System.out.println(e);
            if (e.getSource() != list)
                return;

            if (e.getStateChange() == ItemEvent.SELECTED) {         // Wybrano inną grupę lub zmieniono nazwę starej
                int idx = list.getSelectedIndex();
                if (idx == -1)
                    return;
                labProject.setSelectedSeriesGroup(list.getSelectedIndex());
            }  else if (e.getStateChange() == ItemEvent.DESELECTED) {    // Odznaczenie grupy
                // Zapamiętaj starą zaznaczoną grupę
                for (int i = 0; i < labProject.getNumberOfChildren(); i++) {
                    if (labProject.getChild(i).getName().equals(e.getItem())) {
                        this.oldSelected = labProject.getChild(i);
                    }
                }

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
                Series sel_series = group.getChild(idx);
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

        @Override
        public void actionPerformed(ActionEvent e)
        {
            // Zmiana nazwy grupy w edytorze
            int sg_idx = labProject.getSelectedSeriesGroupIdx();
            if (sg_idx == -1)
                return;
            SeriesGroup sel_group = labProject.getChild(sg_idx);
            DefaultComboBoxModel<String> model = (DefaultComboBoxModel<String>) groupDisplay.getGroupList().getModel();

            // Pobierz nową nazwę i sprawdź, czy poprawna
            String new_name = e.getActionCommand();
            // Sprawdź, czy niepusta
            if (new_name.isEmpty()) {
                JOptionPane.showMessageDialog(
                        groupDisplay.getParentFrame(),
                        GROUP_NAME_EMPTY_MESSAGE,
                        QuickFrame.ERROR_TITLE,
                        JOptionPane.ERROR_MESSAGE);
                SwingUtilities.invokeLater(() -> model.setSelectedItem(sel_group.getName()));
                return;
            }
            // Sprawdź, czy nieużywana
            for (int i = 0; i < labProject.getNumberOfChildren(); i++) {
                if (i != sg_idx && labProject.getChild(i).getName().equals(new_name)) {
                    JOptionPane.showMessageDialog(
                            groupDisplay.getParentFrame(),
                            GROUP_NAME_ALREADY_EXISTS_MESSAGE,
                            QuickFrame.ERROR_TITLE,
                            JOptionPane.ERROR_MESSAGE);
                    // TODO tymczasowe rozwiązanie zmiany nazwy grupy - "na chamca" przywraca starą
                    SwingUtilities.invokeLater(() -> labProject.setSelectedSeriesGroup(sg_idx));
                    return;
                }
            }
            // Wszystko ok - zmień nazwę
            sel_group.setName(new_name);
        }
    }
}
