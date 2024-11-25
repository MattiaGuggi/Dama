package Server;

public class Mossa {
    private Posizione targetPosition;
    private Boolean isEatingMove;

    public Mossa(Posizione targetPosition, boolean isEatingMove) {
        this.targetPosition = targetPosition;
        this.isEatingMove = isEatingMove;
    }

    public Posizione getTargetPosition() {
        return targetPosition;
    }

    public boolean isEatingMove() {
        return isEatingMove;
    }

    @Override
    public String toString() {
        return "Move to " + targetPosition + ", eating: " + isEatingMove;
    }
}
