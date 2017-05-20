// QuickMean - RoundedBorder.java
//---------------------------------------------------------------------
// Klasa implementująca ramkę Swing z zaokrąglonymi narożnikami
//---------------------------------------------------------------------
// Utworzono 16:35 20.05.2017 w IntelliJ IDEA
// (C)PKua, wszystkie prawa zastrzeżone
//---------------------------------------------------------------------

package pl.edu.uj.student.kubala.piotr.qm.utils;

import javax.swing.border.AbstractBorder;
import java.awt.*;
import java.util.Objects;

public class RoundedBorder extends AbstractBorder
{
    private int     width;
    private int     radius;
    private Color   color;
    private Stroke  stroke;

    /**
     * Tworzy ramkę o określonej szerokości linii, promieniu i kolorze
     * @param width szerokość ramki
     * @param radius promień zaookrąglenia
     * @param color kolor ramki różny od null
     * @throws NullPointerException jeśli {@code color == null}
     */
    public RoundedBorder(int width, int radius, Color color) {
        this.width = width;
        this.radius = radius;
        this.color = Objects.requireNonNull(color);
        this.stroke = new BasicStroke(width);
    }

    /**
     * Tworzy ramkę o określonej szerokości, promiemiu i czarnym kolorze
     * @param width szerokość ramki
     * @param radius promień zaokrąglenia
     */
    public RoundedBorder(int width, int radius)
    {
        this(width, radius, Color.BLACK);
    }

    /**
     * Tworzy ramkę o szerokości 1px, określonym promieniu i określonym kolorze
     * @param radius promień zaokrąglenia
     * @param color kolor ramki różny od null
     * @throws NullPointerException jeśli {@code color == null}
     */
    public RoundedBorder(int radius, Color color) {
        this(1, radius, color);
    }

    /**
     * Tworzy ramkę o szerokości piksela, określonym promieniu i czarnym kolorze
     * @param radius szerokość ramki
     */
    public RoundedBorder(int radius)
    {
        this(1, radius, Color.BLACK);
    }

    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height)
    {
        Graphics2D graphics2D = (Graphics2D)g;
        graphics2D.setStroke(this.stroke);
        graphics2D.setColor(this.color);
        graphics2D.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
    }
}
