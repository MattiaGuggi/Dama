package Server;

public class Posizione {
    private int x;
    private int y;
    
    public Posizione(int x,int y){
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
    public String toString(){
        return "{x:"+this.x+";y:"+this.y+"}";
    }
}
