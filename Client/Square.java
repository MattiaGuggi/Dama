package Client;

import javax.swing.*;
import java.awt.*;

public class Square extends JComponent{
    private int x;
    private int y;
    private Color color = ColorsDama.MOVIMENTO_DISPONIBILE;

    public Square(int x,int y){
        this.x = x;
        this.y = y;
    }
    
    public int getX() {
        return this.x;
    }

    public int getY(){
        return this.y;
    }
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(new Color(this.color.getRed(), this.color.getGreen(), this.color.getBlue(), Math.round(0.5f * 255)));
        g.fillOval(10, 10, getWidth() - 30, getHeight() - 30);
    }
}