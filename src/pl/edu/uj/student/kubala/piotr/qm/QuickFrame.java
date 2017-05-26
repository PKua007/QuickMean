// QuickMean - QuickFrame.java
//---------------------------------------------------------------------
// Widok panelu głównego, a zarazem jego ramka. Grupuje w całość
// podwidoki MeasuresInput, MeanDisplay, GroupDisplay, OptionsPane
// i dodaje utworzone przez nie komponenty do głównego okna.
//---------------------------------------------------------------------
// Utworzono 20:38 02.05.2017 w IntelliJ IDEA
// (C)PKua, wszystkie prawa zastrzeżone
//---------------------------------------------------------------------

package pl.edu.uj.student.kubala.piotr.qm;

import pl.edu.uj.student.kubala.piotr.qm.lab.LabProject;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.Objects;

public class QuickFrame extends JFrame implements View
{
    /** Kolor standardowej zaokrąglonej ramki używanej przez kilka widoków */
    public static final Color       BORDER_COLOR = new Color(0xB6B6B6);

    /** Kolor tytułu standardowej zaokrąglonej ramki używanej przez kilka widoków */
    public static final Color       TITLE_COLOR = new Color(0x565656);

    /** Promień zaokrąglonej ramki używanej przez kilka widoków */
    public static final int         BORDER_RADIUS = 14;

    private static final int        FRAME_WIDTH = 360;
    private static final int        FRAME_HEIGHT = 640;
    private static final int        PANELS_GAP = 25;
    private static final int        FRAME_PADDING = 10;

    private MeasuresInput           measuresInput;      // Widok okna z pomiarami
    private MeanDisplay             meanDisplay;        // Widok okna ze średnią
    private GroupDisplay            groupDisplay;       // Widok okna z grupą serii
    private OptionsPane             optionsPane;        // Widok panelu z opcjami
    private View []                 allViews;           // Tablica z wszystkimi widokami
    private LabProject              labProject;         // Obecny projekt laboratorium (model)

    /**
     * Konstruktor okna. Tworzy odpowiednie podwidoki
     * @param title tytuł okna
     * @param labProject projekt laboratorium (widok)
     */
    public QuickFrame(String title, LabProject labProject)
    {
        super(title);
        this.labProject = labProject;

        this.measuresInput = new MeasuresInput(this, this.labProject);
        this.meanDisplay = new MeanDisplay(this, this.labProject);
        this.groupDisplay = new GroupDisplay(this, this.labProject);
        this.optionsPane = new OptionsPane(this, this.labProject);

        this.allViews = new View[]{this.measuresInput, this.meanDisplay, this.groupDisplay, this.optionsPane};
    }

    /**
     * Metoda inicjuje podwidoki i umieszcza je w oknie
     */
    @Override
    public void init()
    {
        Arrays.stream(this.allViews).forEach(View::init);

        this.setSize(new Dimension(FRAME_WIDTH, FRAME_HEIGHT));
        this.setMinimumSize(new Dimension(FRAME_WIDTH, FRAME_HEIGHT));
        //this.setResizable(false);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);

        // Utwórz główny panel okna i panele na elementy powyżej i poniżej tabelki układane przez BoxLayout - elementy
        // powyżej i poniżej będą rozciągnięte na całą szerokość i upakowane w pionie. Tabelka zajmie całą pozostałą
        // przestrzeń
        JPanel mainBorderPanel = new JPanel(new BorderLayout(PANELS_GAP, 0));
        mainBorderPanel.setBorder(
                BorderFactory.createEmptyBorder(FRAME_PADDING, FRAME_PADDING, FRAME_PADDING, FRAME_PADDING)
        );
        JPanel upperContentPanel = new JPanel();
        BoxLayout ucpBoxLayout = new BoxLayout(upperContentPanel, BoxLayout.PAGE_AXIS);
        upperContentPanel.setLayout(ucpBoxLayout);
        JPanel lowerContentPanel = new JPanel();
        BoxLayout lcpBoxLayout = new BoxLayout(lowerContentPanel, BoxLayout.PAGE_AXIS);
        lowerContentPanel.setLayout(lcpBoxLayout);

        // Dodaj elementy górnego panelu - okienko pomiarów, okienko średniej i listę z grupami
        this.measuresInput.getPanel().setAlignmentX(Component.CENTER_ALIGNMENT);
        upperContentPanel.add(this.measuresInput.getPanel());
        upperContentPanel.add(Box.createRigidArea(new Dimension(0, PANELS_GAP)));
        this.meanDisplay.getPanel().setAlignmentX(Component.CENTER_ALIGNMENT);
        upperContentPanel.add(this.meanDisplay.getPanel());
        upperContentPanel.add(Box.createRigidArea(new Dimension(0, PANELS_GAP)));
        this.groupDisplay.getGroupListPanel().setAlignmentX(Component.CENTER_ALIGNMENT);
        upperContentPanel.add(this.groupDisplay.getGroupListPanel());

        // Dodaj elementy dolnego panelu - okienko opcji
        this.optionsPane.getPanel().setAlignmentX(Component.CENTER_ALIGNMENT);
        lowerContentPanel.add(this.optionsPane.getPanel());

        // Dodaj przerwy (pustą ramkę) między tabelką a listą z grupami nad i panelem opcji pod
        this.groupDisplay.getGroupTablePanel().setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createEmptyBorder(GroupDisplay.LIST_TABLE_GAP, 0, PANELS_GAP, 0),
                        BorderFactory.createLineBorder(BORDER_COLOR)
                )
        );

        // Dodaj panele do głównego panelu
        mainBorderPanel.add(upperContentPanel, BorderLayout.PAGE_START);
        mainBorderPanel.add(this.groupDisplay.getGroupTablePanel(), BorderLayout.CENTER);
        mainBorderPanel.add(lowerContentPanel, BorderLayout.PAGE_END);
        this.setContentPane(mainBorderPanel);
        this.setVisible(true);
    }

    /* Gettery solo */

    /**
     * Metoda zwraca widok okna z pomiarami
     * @return widok okna z pomiarami
     * @throws NullPointerException jeśli niezainicjowane
     */
    public MeasuresInput getMeasuresInput() {
        return Objects.requireNonNull(this.measuresInput);
    }

    /**
     * Metoda zwraca widok okna ze średnią
     * @return widok okna ze średnią
     * @throws NullPointerException jeśli niezainicjowane
     */
    public MeanDisplay getMeanDisplay() {
        return Objects.requireNonNull(this.meanDisplay);
    }

    /**
     * Metoda zwraca widok okna z grupą serii
     * @return wirok okna z grupą serii
     * @throws NullPointerException jeśli niezainicjowane
     */
    public GroupDisplay getGroupDisplay() {
        return Objects.requireNonNull(this.groupDisplay);
    }

    /**
     * Metoda zwraca widok panelu z opcjami
     * @return widok panelu z opcjami
     * @throws NullPointerException jeśli niezainicjowane
     */
    public OptionsPane getOptionsPane() {
        return Objects.requireNonNull(optionsPane);
    }

    /**
     * Metoda zwraca obecny projekt laboratorium
     * @return obecny projekt laboratorium
     */
    public LabProject getLabProject() {
        return this.labProject;
    }
}
