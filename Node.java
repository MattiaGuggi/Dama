package Server;

import java.util.ArrayList;

/**
 * Rappresenta un nodo di un albero.
 * Uso l'albero per rappresentare i percorsi che una pedina puo fare.
 */

public class Node {
    public int x;
    public int y;

    public Node ul;
    public Node ur;
    public Node dr;
    public Node dl;

    public Pedina pieceEaten;

    public Node(int x, int y, Pedina pieceEaten) {
        this.x = x;
        this.y = y;
        this.ul = null;
        this.ur = null;
        this.dl = null;
        this.dr = null;

        this.pieceEaten = pieceEaten;
    }

    static public String convertTreeToString(Node node) {
        if (node == null) {
            return "";
        }
        // Creazione della rappresentazione della stringa
        StringBuilder sb = new StringBuilder();
        sb.append("(" + node.x + ":" + node.y + ":" + node.pieceEaten + ")"); // Formato: (x:y:pieceEaten)

        // Aggiunta dei figli, se esistono
        if (node.dl != null || node.dr != null || node.ul != null || node.ur != null) {
            sb.append(" (");
            if (node.dl != null) {
                sb.append("dl: ").append(convertTreeToString(node.dl)).append(" ");
            }
            if (node.dr != null) {
                sb.append("dr: ").append(convertTreeToString(node.dr)).append(" ");
            }
            if (node.ul != null) {
                sb.append("ul: ").append(convertTreeToString(node.ul)).append(" ");
            }
            if (node.ur != null) {
                sb.append("ur: ").append(convertTreeToString(node.ur)).append(" ");
            }
            sb.append(")");
        }

        return sb.toString();
    }

    // Funzione per parsare la stringa e costruire l'albero
    public static Node fromString(String str) {
        // Rimuoviamo gli spazi bianchi all'inizio e alla fine
        str = str.trim();

        System.out.println("str: " + str);
        // Estraiamo la parte del nodo principale, che è tra parentesi tonde
        int start = str.indexOf("(") + 1;
        int end = str.indexOf(")", start);
        String nodePart = str.substring(start, end).trim(); // (x: {x_value}, y: {y_value}, pieceEaten: {pieceEaten})

        // Separiamo i valori di x, y e pieceEaten
        String[] parts = nodePart.split(":");

        int x = Integer.parseInt(parts[0]);
        int y = Integer.parseInt(parts[1]);
        String pieceEatenStr = parts[2];
        Pedina pieceEaten;
        if (pieceEatenStr.equals("null"))
            pieceEaten = null;
        else
            // Creiamo l'oggetto Piece
            pieceEaten = new Pedina((int) (pieceEatenStr.charAt(1)) - 48, (int) (pieceEatenStr.charAt(3)) - 48,
                    pieceEatenStr.charAt(0) == 'w' ? "white" : "black");

        // Creiamo il nodo principale
        Node root = new Node(x, y, pieceEaten);

        // Ora dobbiamo parsare i figli, che sono dopo la chiusura delle parentesi tonde
        // del nodo principale
        int childrenStart = str.indexOf("(", end);
        if (childrenStart != -1) {
            // Rimuoviamo la parentesi iniziale e la chiusura finale
            String childrenPart = str.substring(childrenStart + 1, str.length() - 1).trim();

            // Separiamo le direzioni (dl, dr, ul, ur)
            root.dl = parseChild(childrenPart, "dl");
            root.dr = parseChild(childrenPart, "dr");
            root.ul = parseChild(childrenPart, "ul");
            root.ur = parseChild(childrenPart, "ur");
        }

        return root;
    }

    // Funzione per parsare un figlio da una parte della stringa
    public static Node parseChild(String childrenPart, String direction) {
        int start = childrenPart.indexOf(direction + ": ");
        if (start == -1) {
            return null; // Nessun figlio in questa direzione
        }

        int end = childrenPart.indexOf(")", start) + 1;
        if (end == -1) {
            end = childrenPart.length(); // Fino alla fine della stringa se non c'è una parentesi chiusa
        }
        // Estraiamo la parte del figlio
        String childStr = childrenPart.substring(start + direction.length() + 2, end).trim();

        // Se il figlio è presente, parsarlo ricorsivamente
        if (!childStr.isEmpty()) {
            return fromString(childStr);
        } else {
            return null;
        }
    }

    // Converte un arrayList in stringa
    public static ArrayList<Node> convertStringToArray(String stringa, Boolean reverse, int max) {
        // Tolgo la quadra iniziale e finale
        stringa = stringa.substring(1, stringa.length() - 1);
        String[] elements = stringa.split(", ");

        System.out.println("Stringa bella:" + stringa);
        ArrayList<Node> path = new ArrayList<>();

        for (String s : elements) {
            String[] singleEl = s.split(";");
            Integer x = Integer.parseInt(singleEl[0]);
            Integer y = Integer.parseInt(singleEl[1]);
            if (reverse) {
                x = max - 1 - x;
                y = max - 1 - y;
            }
            // Ora analizzo il pieceEaten (colorx-y)
            // Se presente
            Pedina ped;
            System.out.println("sdadasdsadasdasd: " + s);
            if (singleEl[2].equals("null")) {
                ped = null;
            } else {
                String color = singleEl[2].charAt(0) == 'b' ? "white" : "black";
                Integer pieceX = (int) singleEl[2].charAt(1) - 48;
                Integer pieceY = (int) singleEl[2].charAt(3) - 48;
                if (reverse) {
                    pieceX = max - 1 - pieceX;
                    pieceY = max - 1 - pieceY;
                }
                ped = new Pedina(pieceX, pieceY, color);
            }
            path.add(new Node(x, y, ped));
        }

        return path;
    }

    @Override
    public String toString() {
        return this.x + ";" + this.y + ";" + this.pieceEaten;
    }
}