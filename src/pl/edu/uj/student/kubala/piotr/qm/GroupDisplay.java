// QuickMean - GroupDisplay.java
//---------------------------------------------------------------------
// Widok części panelu głównego z tabelką grupy pomiarów oraz listą
// rozwijaną. Tworzy i zarządza prezentacją danych grupy
//---------------------------------------------------------------------
// Utworzono 20:39 02.05.2017 w IntelliJ IDEA
// (C)PKua, wszystkie prawa zastrzeżone
//---------------------------------------------------------------------

package pl.edu.uj.student.kubala.piotr.qm;

import pl.edu.uj.student.kubala.piotr.qm.lab.LabProject;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
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

        // Utwórz tabelę z grupami
        this.groupTable = new JTable(new Object[][]{
                new Object[]{"Seria 1", "3.09 ± 0.16 ± 0.12"},
                new Object[]{"Seria 2", "4.12 ± 0.58 ± 0.15"},
                new Object[]{"Seria 3", "5.53 ± 0.65 ± 0.72"},
                new Object[]{"Seria 4", "8.06 ± 1.27 ± 0.92"},
                new Object[]{"Seria 5", "10.46 ± 0.02 ± 0.25"},
                new Object[]{"Seria 6", "12.23 ± 1.56 ± 0.12"},
                new Object[]{"Seria 7", "14.42 ± 3.64 ± 0.17"},
                new Object[]{"Seria 8", "18.14 ± 5.23 ± 0.52"},
                new Object[]{"Seria 9", "20.85 ± 5.57 ± 0.92"}
        }, new Object[]{
                DEFAULT_LABEL_HEADER,
                MEAN_OF_MEASURES
        });
        this.groupTable.getColumnModel().getColumn(0).setPreferredWidth(LABEL_COLUMN_WIDTH);
        this.groupTable.getColumnModel().getColumn(1).setPreferredWidth(MEAN_COLUMN_WIDTH);

        // Utwórz listę z grupami
        this.groupList = new JComboBox<>(new String[]{"Grupa 1", "Grupa 2", "Grupa 3", "Grupa 4"});
        this.groupList.setEditable(true);

        // Utwórz przyciski dodawania i usuwania grup
        this.addButton = new JButton("+");
        this.deleteButton = new JButton("X");
        Insets bMargin = this.addButton.getMargin();
        bMargin.left = bMargin.right = BUTTONS_HORIZONTAL_MARGIN;
        this.addButton.setMargin(bMargin);
        this.deleteButton.setMargin(bMargin);

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
    }

    public JScrollPane getGroupTablePanel()
    {
        return Objects.requireNonNull(this.groupTablePanel);
    }

    public JPanel getGroupListPanel()
    {
        return Objects.requireNonNull(this.groupListPanel);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {

    }
}
