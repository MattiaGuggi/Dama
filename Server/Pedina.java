package Server;

public class Pedina {
    private Posizione posizione;
    private Boolean isDama = false;
    private String color;

    public Pedina(int x, int y, String color){
        this.posizione = new Posizione(x, y);
        this.color = color;
    }

    public Boolean getIsDama(){
        return this.isDama;
    }

    public String getColor() {
        return this.color;
    }

    public Posizione getPosizione() {
        return this.posizione;
    }

    public void setPosition(Posizione posizione) {
        this.posizione = posizione;
    }

    @Override
    public String toString() {
        return color + "," + posizione;
    }
}
