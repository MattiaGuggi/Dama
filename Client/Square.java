package Client;

import javax.swing.*;
import java.awt.*;

public class Square extends JComponent{
    private int x;
    private int y;

    public Square(int x,int y){
        this.x = x;
        this.y = y;
        this.setBackground(Color.RED);
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
        g.setColor(Color.RED);
        g.fillOval(10, 10, getWidth() - 20, getHeight() - 20);
    }
}