package Server;

import java.util.ArrayList;

/**
 * Rappresenta un nodo di un albero.
 * Uso l'albero per rappresentare i percorsi che una pedina può fare.
 */

public class Node{
    public int x;
    public int y;

    public Node ul;
    public Node ur;
    public Node dr;
    public Node dl;

    public Pedina pieceEaten = null;
    public Node(int x,int y,Pedina pieceEaten){
        this.x = x;
        this.y = y;
        this.ul = null;
        this.ur = null;
        this.dl = null;
        this.dr = null;
        if(pieceEaten != null)
            this.pieceEaten = new Pedina(pieceEaten.getPosizione().getX(), pieceEaten.getPosizione().getY(),pieceEaten.getColor());
    }

    //Ritorna una stringa che rappresenta un albero
    public static String convertTreeToString(Node root){
        //Come prima cosa rappresento l'albero come vettore
        ArrayList<Node> albero = new ArrayList<>();
        Node.convertTreeToArray(root, albero, 0);

        //Ora devo rappresentare il vettore come stringa (Uso quella di default degli alberi)
        return albero + "";
    }

    //Converte una stringa in albero
    public static Node convertStringToTree(String stringa){
        //Come prima cosa devo ricavare il vettore dalla stringa
        ArrayList<Node> root = Node.convertStringToArray(stringa, false, 8);

        //Ora che ho il vettore devo convertirlo in albero
        return Node.convertArrayToTree(root, 0);
    }

    private static void convertTreeToArray(Node nodo, ArrayList<Node> albero, int indice) {
        //Se l'indice sborda dal vettore, aggiungo null
        while (albero.size() <= indice) {
            albero.add(null); 
        }

        //Inserisco il nodo corrente nell'indice specificato
        albero.set(indice, nodo);

        if (nodo == null)
            return; 

        //Inserisco ricorsivamente i figli nei loro indici calcolati
        convertTreeToArray(nodo.dl, albero, 4 * indice + 1); // giu-sx
        convertTreeToArray(nodo.dr, albero, 4 * indice + 2); // giu-dx
        convertTreeToArray(nodo.ul, albero, 4 * indice + 3); // su-sx
        convertTreeToArray(nodo.ur, albero, 4 * indice + 4); // su-dx
    }
    
    //Converti un vettore in albero
    private static Node convertArrayToTree(ArrayList<Node> albero, int indice) {
        if (indice >= albero.size() || albero.get(indice) == null) {
            return null;
        }

        //Prendi il nodo corrente dall'ArrayList
        Node nodo = albero.get(indice);

        //Ricorsivamente costruisci i figli
        nodo.dl = convertArrayToTree(albero, 4 * indice + 1); // giu-sx
        nodo.dr = convertArrayToTree(albero, 4 * indice + 2); // giu-dx
        nodo.ul = convertArrayToTree(albero, 4 * indice + 3); // su-sx
        nodo.ur = convertArrayToTree(albero, 4 * indice + 4); // su-dx

        return nodo;
    }

    //Converte un arrayList in stringa
    public static ArrayList<Node> convertStringToArray(String stringa,Boolean reverse,int max){
        // Tolgo la quadra iniziale e finale
        stringa = stringa.substring(1, stringa.length() - 1);
        String[] elements = stringa.split(", ");

        //System.out.println("Stringa bella:"+stringa);
        ArrayList<Node> path = new ArrayList<>();

        for (String s : elements) {
            if(!s.equals("null")){
                String[] singleEl = s.split(";");
                Integer x = Integer.parseInt(singleEl[0]);
                Integer y = Integer.parseInt(singleEl[1]);
                if(reverse){
                    x = max-1-x;
                    y = max-1-y;
                }
                // Ora analizzo il pieceEaten (colorx-y)
                // Se presente
                Pedina ped;
                //System.out.println("sdadasdsadasdasd: "+s);
                if(singleEl[2].equals("null")){
                    ped = null;
                }
                else{
                    String color = singleEl[2].charAt(0) == 'b' ? "black" : "white";
                    Integer pieceX = (int) singleEl[2].charAt(1) - 48;
                    Integer pieceY = (int) singleEl[2].charAt(3) - 48;
                    if(reverse){
                        pieceX = max - 1 - pieceX;
                        pieceY = max - 1 - pieceY;
                    }
                    ped = new Pedina(pieceX, pieceY, color);
                }
                path.add(new Node(x, y, ped));
            }
            else
                path.add(null);
        }
        
        return path;
    }

    @Override
    public String toString(){
        return this.x+";"+this.y+";"+this.pieceEaten;
    }
}
