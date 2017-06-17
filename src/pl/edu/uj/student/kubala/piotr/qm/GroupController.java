// QuickMean - GroupController.java
//---------------------------------------------------------------------
// Kontroler tabelki z grupami oraz listy rozwijanej w głównym panelu.
//---------------------------------------------------------------------
// Utworzono 20:40 02.05.2017 w IntelliJ IDEA
// (C)PKua, wszystkie prawa zastrzeżone
//---------------------------------------------------------------------

package pl.edu.uj.student.kubala.piotr.qm;

import pl.edu.uj.student.kubala.piotr.qm.lab.LabProject;
import pl.edu.uj.student.kubala.piotr.qm.lab.SeriesGroup;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.stream.IntStream;

public class GroupController implements Controller, ItemListener, ListSelectionListener
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

    /* Pomocnicza funkcja obsługujaca zdarzenia dla listy grup */
    private void groupListActionPerformed(ActionEvent e)
    {
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        /*// Obsłuż zdarzenia dla listy grup
        if (e.getSource() == this.groupDisplay.getGroupList())
            groupListActionPerformed(e);*/
    }

    /* Wywoływane, gdy zostani wybrana nowa grupa */
    @Override
    public void itemStateChanged(ItemEvent e) {
        JComboBox<String> list = this.groupDisplay.getGroupList();

        // Wybrano inną grupę
        if (e.getSource() == list && e.getStateChange() == ItemEvent.SELECTED) {
            this.labProject.setSelectedSeriesGroup(list.getSelectedIndex());
            //System.out.println(e);
            //System.out.println(Thread.currentThread().getStackTrace());
        }
    }

    @Override
    public void setup() {
        // Ustaw nasłuchiwanie na CheckBoxa
        this.groupDisplay.getGroupList().addItemListener(this);
        // Ustaw nasłuchiwanie na zaznaczanie w tabelce
        this.groupDisplay.getGroupTable().getSelectionModel().addListSelectionListener(this);
        // Ustaw nasłuchiwanie na przycisk usunięcia
        JButton deleteButton = this.groupDisplay.getDeleteButton();
        deleteButton.setAction(new DeleteAction(deleteButton.getText()));
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
                labProject.deleteSeriesGroup(selectedGroup);
        }
    }

    /* Wywoływane, gdy zmieni się zaznaczenie w tabelce */
    @Override
    public void valueChanged(ListSelectionEvent e) {
        Object source = e.getSource();
        if (source == this.groupDisplay.getGroupTable().getSelectionModel()) {
            DefaultListSelectionModel selectionModel = (DefaultListSelectionModel)source;

            if (Main.DEBUG) {
                System.out.println("JTable selection:");
                System.out.println("isAdjusting: " + selectionModel.getValueIsAdjusting() + "; leadIndex: " + selectionModel.getLeadSelectionIndex());
                for (int i = selectionModel.getMinSelectionIndex(); i <= selectionModel.getMaxSelectionIndex(); i++)
                    System.out.println(i + " selected: " + selectionModel.isSelectedIndex(i));
            }

            // Zignoruj usunięcie zaznaczenia
            if (selectionModel.getLeadSelectionIndex() == -1 || selectionModel.getMinSelectionIndex() == -1)
                return;

            SeriesGroup selectedGroup = this.labProject.getSelectedSeriesGroup();
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
