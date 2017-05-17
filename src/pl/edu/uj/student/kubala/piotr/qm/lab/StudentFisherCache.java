// QuickMean - FisherCache.java
//---------------------------------------------------------------------
// Klasa obliczająca i przechowująca zapamiętane wartości współczynnika
// Studenta dla poziomu ufności odpowiadającemu 1 sigma dla
// poszczególnych ilości stopni swobody
//---------------------------------------------------------------------
// Utworzono 17:02 12.05.2017 w IntelliJ IDEA
// (C)PKua, wszystkie prawa zastrzeżone
//---------------------------------------------------------------------

package pl.edu.uj.student.kubala.piotr.qm.lab;

import java.util.HashMap;

/**
 * Klasa umożliwiająca obliczanie współczynników Studenta-Fishera dla poziomu tolerancji 1sigma. Przechowuje wartości
 * dla obliczonych już ilości stopni swobody w swojej pamięci cache.
 */
public class StudentFisherCache
{
    private static final double     INT_STEP = 0.0001;                  // Krok przy całkowaniu funkcji gęstości
    private static final double     SIGMA_PROB = 0.6826894921370859;    // Prawdopodobieństwo dla 1sigma
    private static final int        MAX_DEGREES = 320;                  // Maksymalna liczba stopni, dla których jest liczony

    private static HashMap<Integer, Double>    fisherMap;   // Mapa [ilość stopni swobody] -> [wartość współczynnika]

    static
    {
        // Wstaw 10 pierwszych wartości współczynnika (wyliczonych przy INT_STEP = 0.00000002) do mapy
        fisherMap = new HashMap<>();
        fisherMap.put(1, 1.8373372091072506);
        fisherMap.put(2, 1.3212773868467476);
        fisherMap.put(3, 1.1968813689960478);
        fisherMap.put(4, 1.141626638066803);
        fisherMap.put(5, 1.1105065964486036);
        fisherMap.put(6, 1.0905690718116319);
        fisherMap.put(7, 1.0767133903947363);
        fisherMap.put(8, 1.0665284236501942);
        fisherMap.put(9, 1.0587276709587363);
        fisherMap.put(10, 1.0525624299619243);
    }

    /**
     * Zwraca współczynnik Studenta-Fishera - taką liczbę {@code n}, że całka oznaczona w granicach {@code [-n, n]}
     * pod funkcją gęstości prawdopodobieństwa rozkładu t-Studenta dla {@code degreesOfFreedom} stopni swobody
     * jest równa około 0.682
     * @param degreesOfFreedom liczba stopni swobody, równa liczbie pomiarów minus 1
     * @return wyliczony współczynnik Studenta-Fishera
     */
    public static double get(int degreesOfFreedom)
    {
        if (degreesOfFreedom < 1)
            throw new IllegalArgumentException("Niedodatnia liczba stopni swobody: " + degreesOfFreedom);
        else if (degreesOfFreedom > 320)
            return 1;
        Double coeff = fisherMap.get(degreesOfFreedom);
        if (coeff == null)
            coeff = calculate(degreesOfFreedom);
        return coeff;
    }

    /* Pomocnicza metoda obliczająca współczynnik - wywoływana, jeśli nie ma go w pamieci podręcznej */
    private static double calculate(int degreesOfFreedom)
    {
        double x = INT_STEP;
        double integral = 0;
        double prev_prob = 0;
        double new_prob = probDensity(0, degreesOfFreedom);
        // Całkuj, aż przekroczysz poszukiwane prawdopodobieństwo
        while (integral < SIGMA_PROB) {
            prev_prob = new_prob;
            new_prob = probDensity(x, degreesOfFreedom);
            integral += (prev_prob + new_prob) * INT_STEP;
            x += INT_STEP;
        }

        // Interpoluj x do wartości krytycznej
        double A = (new_prob - prev_prob) / 2 / INT_STEP;
        x = x - (-new_prob + Math.sqrt(new_prob * new_prob + 4 * A * (integral - SIGMA_PROB))) / 2 / A;

        fisherMap.put(degreesOfFreedom, x);
        return x;
    }

    /* Pomocnicza metoda obliczająca wartość funkcji gęstości w x dla degrees stopni swobody */
    private static double probDensity(double x, double degrees)
    {
        double gamma1 = gamma((degrees + 1) / 2);
        double gamma2 = gamma(degrees / 2);
        double power = Math.pow(1 + x * x / degrees, -(degrees + 1) / 2);
        return gamma1 / gamma2 / Math.sqrt(degrees * Math.PI) * power;
    }

    /* Copyright © 2000–2011, Robert Sedgewick and Kevin Wayne.  */
    private static double logGamma(double x)
    {
        double tmp = (x - 0.5) * Math.log(x + 4.5) - (x + 4.5);
        double ser = 1.0 + 76.18009173    / (x + 0)   - 86.50532033    / (x + 1)
                + 24.01409822    / (x + 2)   -  1.231739516   / (x + 3)
                +  0.00120858003 / (x + 4)   -  0.00000536382 / (x + 5);
        return tmp + Math.log(ser * Math.sqrt(2 * Math.PI));
    }

    private static double gamma(double x)
    {
        return Math.exp(logGamma(x));
    }
}
