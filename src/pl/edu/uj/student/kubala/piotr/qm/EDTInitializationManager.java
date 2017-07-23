// QuickMean - DialogManager.java
//---------------------------------------------------------------------
// [opis pliku]
//---------------------------------------------------------------------
// Utworzono 14:11 03.07.2017 w IntelliJ IDEA
// (C)PKua, wszystkie prawa zastrzeżone
//---------------------------------------------------------------------

package pl.edu.uj.student.kubala.piotr.qm;

import javax.swing.*;
import java.util.*;
import java.util.stream.Collectors;

public class EDTInitializationManager {

    private static boolean INIT_DEBUG = true;

    /**
     * Wewnętrzna klasa-pudełko na inicjowalny element. Zawiera listę zależności i informację, czy już jest zainicjowany
     */
    private static class EDTInitBox
    {
        EDTInitializable    initializable;
        boolean             initialized;
        ArrayList<EDTInitBox>     deps;

        public EDTInitBox(EDTInitializable initializable)
        {
            this.initializable = Objects.requireNonNull(initializable);
            this.initialized = false;
            this.deps = new ArrayList<>();
        }

        /**
         * Zainicjuj przechowywany element (a najpierw jego zależności). Wykryj pętle zależności
         * @param instance instancja EDTInitializationManager, która wywołoła metodę
         */
        public void init(EDTInitializationManager instance)
        {
            if (this.initialized)
                return;

            // Zainicjuj zależności. Umieść element na stosie i wykryj pętlę zależności
            instance.initStack.push(this);
            for (EDTInitBox box : this.deps) {
                // Sprawdź, czy nie ma pętly zależności
                if (instance.initStack.contains(box))
                    throwDepsLoop(instance, box);
                box.init(instance);
            }
            instance.initStack.pop();

            // Zainicjuj przechowywany element
            if (INIT_DEBUG) {
                if (instance.initStack.empty())
                    System.out.println("Inicjacja elementu " + this + "...");
                else
                    System.out.println("Inicjacja elementu " + this + " żądanego przez "
                            + instance.initStack.peek() + "...");
            }
            SwingUtilities.invokeLater(initializable::init);
            this.initialized = true;
        }

        /* Metoad pomocnicza produkująca wyjątek pętli zależności */
        private void throwDepsLoop(EDTInitializationManager instance, EDTInitBox box)
        {
            StringBuilder info = new StringBuilder();
            info.append("Pętla referencji na elemencie ").append(box).append(". Stos wywołań:\n");
            for (EDTInitBox stackelem : instance.initStack)
                info.append("\t").append(stackelem).append("\n");
            info.append("Elementy żądane przez ostatni:\n\t").append(this.deps.toString());
            throw new RuntimeException(info.toString());
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            EDTInitBox that = (EDTInitBox) o;

            return initializable.equals(that.initializable);
        }

        @Override
        public int hashCode() {
            return initializable.hashCode();
        }

        @Override
        public String toString() {
            return initializable.getElementName();
        }
    }

    private static EDTInitializationManager instance;  /* Instancja managera */

    /**
     * Zwraca instancję singletonu EDTInitializationManager
     * @return instancja singletonu
     */
    public static EDTInitializationManager getInstance() {
        if (instance == null)
            instance = new EDTInitializationManager();
        return instance;
    }

    private ArrayList<EDTInitBox>   initList;
    private Stack<EDTInitBox>       initStack;
    private boolean                 initialized;

    /* Prywatny konstruktor singletonu */
    private EDTInitializationManager() {
        this.initList = new ArrayList<>();
        this.initStack = new Stack<>();
    }

    /**
     * Dodaje inicjowalny element na listę
     * @param initializable element, który można zainicjować
     */
    public void registerElement(EDTInitializable initializable) {
        EDTInitBox box = new EDTInitBox(initializable);
        if (this.initList.contains(box))
            throw new RuntimeException("Element " + box + " już jest na liście");
        this.initList.add(box);
    }

    /**
     * Dodaje zależność do między elementami. Oba muszą być zarejestrowane
     * @param which element, dla którego chcemy wskazać zależność
     * @param depends_on element, od którego zależy {@code which}
     */
    public void addDependency(EDTInitializable which, EDTInitializable depends_on)
    {
        EDTInitBox which_box = this.obtainBox(which);
        EDTInitBox depends_on_box = this.obtainBox(depends_on);
        if (which_box == null)
            throw new RuntimeException("Element " + which.getElementName() + " nie jest zarejestrowany");
        if (depends_on_box == null)
            throw new RuntimeException("Element " + depends_on.getElementName() + " nie jest zarejestrowany");

        // Zarejestruj zależność, jeśli jeszcze nie zarejestrowana
        if (!which_box.deps.contains(depends_on_box))
            which_box.deps.add(depends_on_box);
    }

    /**
     * Inicjuje wszystkie zarejestrowane elementy zważajac na zależności.
     * @throws RuntimeException jeśli wykryto pętlę zależności
     */
    public void initElements()
    {
        if (initialized)
            throw new RuntimeException("Ponowne wywołanie EDTInitializationManager::init");

        // Zainicjuj wszystko biorąc pod uwagę zależności
        for (EDTInitBox initializable : this.initList)
            initializable.init(this);
        initialized = true;
    }

    /**
     * Zwraca listę zależności, od których zależy element initializable
     * @param initializable element, którego zależności są zwracane
     * @return lista zelażności elementu {@code initializable}
     */
    public List<EDTInitializable> getDependencies(EDTInitializable initializable)
    {
        EDTInitBox box = this.obtainBox(initializable);
        if (box == null)
            throw new RuntimeException("Element " + initializable.getElementName() + " nie jest zarejestrowany");
        return box.deps.stream()
                .map((d) -> d.initializable)
                .collect(Collectors.toList());
    }

    /* Prywatna metoda pobierająca pudełko z elementem initializable. Jeśli nie utworzono, zwraca null */
    private EDTInitBox obtainBox(EDTInitializable initializable)
    {
        EDTInitBox box = new EDTInitBox(initializable);
        int idx = this.initList.indexOf(box);
        if (idx == -1)
            return null;
        else return this.initList.get(idx);
    }
}
