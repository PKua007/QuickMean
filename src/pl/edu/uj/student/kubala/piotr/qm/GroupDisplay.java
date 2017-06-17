// QuickMean - GroupDisplay.java
//---------------------------------------------------------------------
// Widok części panelu głównego z tabelką grupy pomiarów oraz listą
// rozwijaną. Tworzy i zarządza prezentacją danych grupy
//---------------------------------------------------------------------
// Utworzono 20:39 02.05.2017 w IntelliJ IDEA
// (C)PKua, wszystkie prawa zastrzeżone
//---------------------------------------------------------------------

package pl.edu.uj.student.kubala.piotr.qm;

import pl.edu.uj.student.kubala.piotr.qm.lab.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Objects;

public class GroupDisplay implements View
{
    private static final int        LIST_BUTTONS_GAP = 10;          // Odstęp miedzy listą z grupami a przyciskami
    private static final int        BUTTONS_GAP = 5;                // Odstęp miedzy przyciskami
    private static final String     DEFAULT_LABEL_HEADER = "Nazwa serii";   // Teskt w nagłówku kolumny z nazwami serii
    private static final String     MEAN_OF_MEASURES = "X\u0305 ± σ(X\u0305) ± ΔX\u0305";
    public static final int         LIST_TABLE_GAP = 10;            // Odstęp między listą z gupami, a tabelką
    private static final int        BUTTONS_HORIZONTAL_MARGIN = 5;  // Prawy i lewy margines w przyciskach
    private static final int        LABEL_COLUMN_WIDTH = 30;        // Szerokość kolumny z nazwami serii
    private static final int        MEAN_COLUMN_WIDTH = 150;         // Szerokość kolumny z nazwami serii

    private static final String     DELETE_GROUP_MESSAGE = "Czy na pewno chcesz usunąć wskazaną GRUPĘ POMIARÓW? Operacji "
            + "nie da się cofnąć.\n\nJeżeli chcesz usunać zaznaczone serie pomiarów, kliknij PPM na zaznaczeniu.";
    private static final String     DELETE_GROUP_TITLE = "Usuwanie grupy";


    private QuickFrame      parentFrame;
    private LabProject      labProject;
    private JTable          groupTable;
    private JScrollPane     groupTablePanel;
    private JPanel          groupListPanel;
    private JComboBox<String> groupList;
    private JButton         addButton;
    private JButton         deleteButton;

    /**
     * Konstruktor okna z grupami
     * @param parentFrame główna ramka z modułami
     * @param labProject projekt laboratorium (widok)
     */
    public GroupDisplay(QuickFrame parentFrame, LabProject labProject) {
        this.parentFrame = parentFrame;
        this.labProject = labProject;
    }

    @Override
    public void init()
    {
        if (this.groupTable != null)
            throw new RuntimeException("GroupDisplay::init wywołane drugi raz");

        /*new Object[]{"Seria 1", "3.09 ± 0.16 ± 0.12"},
                new Object[]{"Seria 2", "4.12 ± 0.58 ± 0.15"},
                new Object[]{"Seria 3", "5.53 ± 0.65 ± 0.72"},
                new Object[]{"Seria 4", "8.06 ± 1.27 ± 0.92"},
                new Object[]{"Seria 5", "10.46 ± 0.02 ± 0.25"},
                new Object[]{"Seria 6", "12.23 ± 1.56 ± 0.12"},
                new Object[]{"Seria 7", "14.42 ± 3.64 ± 0.17"},
                new Object[]{"Seria 8", "18.14 ± 5.23 ± 0.52"},
                new Object[]{"Seria 9", "20.85 ± 5.57 ± 0.92"}*/

        // Utwórz tabelę z grupami - kolumna ze średnimi nieedytowalna
        DefaultTableModel tableModel = new DefaultTableModel(new Object[][]{}, new Object[]{
                DEFAULT_LABEL_HEADER,
                MEAN_OF_MEASURES
        }) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 0;
            }
        };

        this.groupTable = new JTable(tableModel);
        this.groupTable.getColumnModel().getColumn(0).setPreferredWidth(LABEL_COLUMN_WIDTH);
        this.groupTable.getColumnModel().getColumn(1).setPreferredWidth(MEAN_COLUMN_WIDTH);

        // Utwórz listę z grupami
        this.groupList = new JComboBox<>(/*new String[]{"Grupa 1", "Grupa 2", "Grupa 3", "Grupa 4"}*/);
        this.groupList.setEditable(false);

        // Utwórz przyciski dodawania i usuwania grup
        this.addButton = new JButton("+");
        this.deleteButton = new JButton("X");
        Insets bMargin = this.addButton.getMargin();
        bMargin.left = bMargin.right = BUTTONS_HORIZONTAL_MARGIN;
        this.addButton.setMargin(bMargin);
        this.deleteButton.setMargin(bMargin);
        this.deleteButton.setEnabled(false);

        // Upakuj w panelach
        JPanel buttonsPanel = new JPanel();
        BoxLayout bpLayout = new BoxLayout(buttonsPanel, BoxLayout.LINE_AXIS);
        buttonsPanel.setLayout(bpLayout);
        buttonsPanel.add(this.addButton);
        buttonsPanel.add(Box.createRigidArea(new Dimension(BUTTONS_GAP, 0)));
        buttonsPanel.add(this.deleteButton);

        this.groupListPanel = new JPanel(new BorderLayout(LIST_BUTTONS_GAP, 0));
        this.groupListPanel.add(this.groupList, BorderLayout.CENTER);
        this.groupListPanel.add(buttonsPanel, BorderLayout.LINE_END);

        this.groupTablePanel = new JScrollPane(this.groupTable);
        this.groupTablePanel.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        this.groupTablePanel.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        // Nasłuchuj projektu
        this.labProject.addPropertyChangeListener(new LabChangeListener());
    }

    public JScrollPane getGroupTablePanel()
    {
        return Objects.requireNonNull(this.groupTablePanel);
    }

    public JPanel getGroupListPanel()
    {
        return Objects.requireNonNull(this.groupListPanel);
    }

    public JComboBox<String> getGroupList() {
        return Objects.requireNonNull(groupList);
    }

    public JTable getGroupTable() {
        return Objects.requireNonNull(groupTable);
    }

    public JButton getAddButton() {
        return Objects.requireNonNull(addButton);
    }

    public JButton getDeleteButton() {
        return Objects.requireNonNull(deleteButton);
    }

    public QuickFrame getParentFrame() {
        return parentFrame;
    }

    /**
     * Metoda pokazuje dialog pytajacy użytkownika, czy na pewno chce usunąć grupę.
     * @return {@code true}, jeśli użytkownik sie zgodził
     */
    public boolean deleteGroupConfirmationDialog()
    {
        if (this.groupTable == null)
            throw new RuntimeException("GroupDisplay::init jeszcze nie wywołane");

        return JOptionPane.showConfirmDialog(this.parentFrame, DELETE_GROUP_MESSAGE, DELETE_GROUP_TITLE, JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE)
                == JOptionPane.OK_OPTION;
    }

    /*
     * Wewnętrzna klasa nasłuchująca projektu
     */
    private class LabChangeListener implements PropertyChangeListener {

        /* Dodanie nowej grupy - uzupełnij listę */
        private void handleNewGroup(PropertyChangeEvent evt) {
            int idx;
            SeriesGroup newGroup = (SeriesGroup) evt.getNewValue();
            idx = labProject.getChildIdx(newGroup);
            if (idx == -1)
                throw new AssertionError();

            DefaultComboBoxModel<String> comboBoxModel = (DefaultComboBoxModel<String>) groupList.getModel();
            comboBoxModel.insertElementAt(newGroup.getName(), idx);
        }

        /* Usuwanie grupy - załaduj listę od nowa */
        private void handleDelGroup(PropertyChangeEvent evt) {
            DefaultComboBoxModel<String> comboBoxModel = (DefaultComboBoxModel<String>) groupList.getModel();
            comboBoxModel.removeAllElements();
            for (int i = 0; i < labProject.getNumberOfChildren(); i++)
                comboBoxModel.addElement(labProject.getChild(i).getName());
        }

        /* Zmiana wybranej grupy - zmień wybraną pozycję na liście i zaktualizuj tabelę */
        private void handleSelectedGroupChange(PropertyChangeEvent evt) {
            // Zmień pozycję na liście i status przycisków
            SeriesGroup selectedGroup = (SeriesGroup) evt.getNewValue();
            groupList.setSelectedIndex(labProject.getChildIdx(selectedGroup));
            System.out.println("Tu jest: " + selectedGroup);
            deleteButton.setEnabled(selectedGroup != null);
            groupList.setEditable(selectedGroup != null);

            // Zmień nagłówek w tabeli
            TableColumn seriesHeader = groupTable.getColumnModel().getColumn(0);
            if (selectedGroup == null)
                seriesHeader.setHeaderValue("");
            else
                seriesHeader.setHeaderValue(selectedGroup.getLabelHeader());

            // Zmień model tabeli
            DefaultTableModel tableModel = (DefaultTableModel) groupTable.getModel();
            tableModel.setRowCount(0);
            if (selectedGroup == null)
                return;

            Series series;
            Quantity quantity;
            FormattedMeasureFactory factory = new FormattedMeasureFactory();
            FormattedMeasure formattedMeasure;

            for (int i = 0; i < selectedGroup.getNumberOfChildren(); i++) {
                series = selectedGroup.getChild(i);
                factory.setSeparateErrors(series.isSeparateErrors());
                factory.setErrorSignificantDigits(series.getSignificantDigits());
                quantity = series.getMeanQuantity();
                formattedMeasure = factory.format(quantity);

                tableModel.addRow(new String[]{
                        series.getLabel(),
                        formattedMeasure.toHTMLCompact()
                });
            }

            int highlighted = selectedGroup.getHighlightedSeriesIdx();
            if (highlighted != -1)
                groupTable.setRowSelectionInterval(highlighted, highlighted);
        }

        /* Zmiana opcji lub wartości średniej serii - zaktualizuj dane w tabeli */
        private void handleSeriesMeanChange(PropertyChangeEvent evt) {
            // Odrzuć zdarzenia niedotyczące bieżącej grupy
            SeriesGroup selectedGroup = labProject.getSelectedSeriesGroup();
            if (selectedGroup == null)
                return;
            Series changedSeries = (Series) evt.getSource();
            int idx = selectedGroup.getChildIdx(changedSeries);
            if (idx == -1)
                return;

            Quantity quantity;
            FormattedMeasureFactory factory = new FormattedMeasureFactory();
            FormattedMeasure formattedMeasure;

            factory.setSeparateErrors(changedSeries.isSeparateErrors());
            factory.setErrorSignificantDigits(changedSeries.getSignificantDigits());
            quantity = changedSeries.getMeanQuantity();
            formattedMeasure = factory.format(quantity);

            DefaultTableModel model = (DefaultTableModel) groupTable.getModel();
            model.setValueAt(formattedMeasure.toHTMLCompact(), idx, 1);
        }

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            System.out.println(evt);

            switch (evt.getPropertyName()) {
                // Dodanie nowej grupy - uzupełnij listę
                case LabProject.NEW_GROUP:
                    this.handleNewGroup(evt);
                    break;

                // Usuwanie grupy - załaduj listę od nowa
                case LabProject.DEL_GROUP:
                    this.handleDelGroup(evt);
                    break;

                // Zmiana wybranej grupy - zmień wybraną pozycję na liście i zaktualizuj tabelę
                case LabProject.SELECTED_GROUP:
                    this.handleSelectedGroupChange(evt);
                    break;

                // Zmiana opcji lub wartości średniej serii - zaktualizuj dane w tabeli
                case Series.MEAN_ERR:
                case Series.SIGNIFICANT_DIGITS:
                case Series.SEPARATE_ERRORS:
                    this.handleSeriesMeanChange(evt);
                    break;

                case SeriesGroup.NEW_SERIES:
                    break;
            }
        }
    }
}
