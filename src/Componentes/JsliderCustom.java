package Componentes;

import java.awt.Color;
import java.awt.Cursor;
import javax.swing.JSlider;

public class JsliderCustom extends JSlider {

    public JsliderCustom() {
        setOpaque(false);
        setBackground(new Color(180, 180, 180));
        setForeground(new Color(69, 124, 235));
        setUI(new JSliderUI(this));
        setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
}
