package Server;

public class Posizione {
    private final int MAX = 8;
    private int x;
    private int y;
    
    public Posizione(int x, int y){
        this.x = x;
        this.y = y;
    }

    public int getY() {
        return this.y;
    }
    public void setY(int y) {
        this.y = y;
    }

    public int getX() {
        return this.x;
    }
    public void setX(int x) {
        this.x = x;
    }

    @Override
    public String toString() {
        return this.x+","+this.y;
    }
    //Potrebbe essere necessario reversare le coordinate
    public String getString(Boolean reverse){
        if(reverse)
            return (MAX - x - 1) + "," + (MAX - y - 1);
        return x + "," + y;
    }
}
