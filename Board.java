package src;

import java.util.*;

public class Board {

    /**
     **Representation of chess board with a 2d piece array. Empty spaces are null
     * TODO: Maybe keep a list of all pieces instead of a 2d matrix
     */
    public Piece[][] board;

    /**
     **A map containing all movement patterns for each piece. Key/Value pairs are the name of the piece and a list
     **of targets that correspond to how that piece can move.
     *
     */
    public final Map<String, List<Piece>> MOVEMENT;

    public boolean WHITETURN;

    public boolean GAMEOVER;

    public Board(String fenPosition){
        board = new Piece[8][8];
        setPosition(fenPosition);

        MOVEMENT = new HashMap<>();
        MOVEMENT.put("PAWN", Arrays.asList(new Piece(0,-1),new Piece(0,-2),new Piece(-1,-1),new Piece(1,-1)));
        MOVEMENT.put("pawn", Arrays.asList(new Piece(0,1),new Piece(0,2),new Piece(-1,1),new Piece(1,1)));
        MOVEMENT.put("knight", Arrays.asList( new Piece(1,-2),new Piece(2,-1), new Piece(2,1),new Piece(1,2), new Piece(-1,2),new Piece(-2,1), new Piece(-2,-1), new Piece(-1,-2)));
        MOVEMENT.put("rook", Arrays.asList(new Piece(1,0),new Piece(-1,0),new Piece(0,1),new Piece(0,-1)));
        MOVEMENT.put("bishop", Arrays.asList(new Piece(1,1), new Piece(1,-1),new Piece(-1,1),new Piece(-1,-1)));
        MOVEMENT.put("king", Arrays.asList(new Piece(1,1), new Piece(1,-1),new Piece(-1,1),new Piece(-1,-1),new Piece(1,0),new Piece(-1,0),new Piece(0,1),new Piece(0,-1), new Piece(2,0), new Piece(-2,0)));
        MOVEMENT.put("queen",MOVEMENT.get("rook"));

        WHITETURN = true; //should be set in setPosition function by the fen string
        GAMEOVER = false;
    }

    /**
     **Sets the board according to the fenPosition string
     * @param fenPosition
     */
    public void setPosition(String fenPosition){
        int row = 0;
        int col = 0;
        for(int i=0;i<fenPosition.length();i++){
            String piece = fenPosition.substring(i,i+1);
            String name = null;
            String color = null;
            int ind = 0;
            switch(piece){
                case "r":
                    name = "rook";
                    ind = 10;
                    color = "black";
                    break;
                case "n":
                    name = "knight";
                    ind = 9;
                    color = "black";
                    break;
                case "b":
                    name = "bishop";
                    ind = 8;
                    color = "black";
                    break;
                case "q":
                    name = "queen";
                    ind = 7;
                    color = "black";
                    break;
                case "k":
                    name = "king";
                    ind = 6;
                    color = "black";
                    break;
                case "p":
                    name = "pawn";
                    ind = 11;
                    color = "black";
                    break;
                case "R":
                    name = "ROOK";
                    ind = 4;
                    color = "white";
                    break;
                case "N":
                    name = "KNIGHT";
                    ind = 3;
                    color = "white";
                    break;
                case "B":
                    name = "BISHOP";
                    ind = 2;
                    color = "white";
                    break;
                case "Q":
                    name = "QUEEN";
                    ind = 1;
                    color = "white";
                    break;
                case "K":
                    name = "KING";
                    ind = 0;
                    color = "white";
                    break;
                case "P":
                    name = "PAWN"; 
                    ind = 5;
                    color = "white";
                    break;
                case "/":
                    name = null;
                    col = -1;
                    row++;
                    break;
                default:
                    name = null;
                    col += Integer.valueOf(piece)-1;
            }
            if(name != null){ 
                board[row][col] = new Piece(name,color,col*100,row*100);
                board[row][col].imageIndex = ind;
            }
            col++;
            if(col >=8) col=0;       
                
        }
    }

    /**
     **Checks to see if the selected piece has a valid move at this target
     * @param selectedPiece
     * @param targx
     * @param targy
     * @return True if the selected piece can move to this target. False otherwise.
     */
    public boolean isMoveValid(Piece selectedPiece, int targx, int targy){
        Piece target = new Piece(targx,targy);
        HashSet<Piece> validMoves = calculateAllLegalMoves(selectedPiece);
        return validMoves.contains(target);
    }

    public Piece getPiece(int targx, int targy){
        return board[targy][targx];
    }

    public Piece getPiece(Piece target){
        return board[target.y][target.x];
    }

    public HashSet<Piece> getPieces(String color){
        HashSet<Piece> pieces = new HashSet<>();
        for(int row=0;row<8;row++){
            for(int col=0;col<8;col++){
                if(board[row][col] == null) continue;

                if(color.equals("all")){
                    pieces.add(board[row][col]);
                    continue;
                }

                if(board[row][col].color.equals(color)){
                    pieces.add(board[row][col]);
                }
            }
        }
        return pieces;
    }
    
    /**
     * 
     * @param color
     * @return Returns the king on this colors' team
     * @throws IllegalStateException if the king is not found. If the king is not found this is very, very bad.....
     */
    public Piece getKing(String color){
        for(Piece piece: getPieces(color)){
            if(piece.name.equals("king")) return piece;
        }
        throw new IllegalStateException(color + " king cannot be found.");
    }

    public boolean isTurn(Piece piece){
        return piece.color.equals("white") == WHITETURN;
    }

    /**
     **Calculates all legal moves. Starts by calculating each psuedo legal move
     **and then checks if that space is valid, if a piece is pinned, and if the king is
     **in checkmate
     * 
     * @param piece
     * @return A set of all possible targets for this piece
     */
    public HashSet<Piece> calculateAllLegalMoves(Piece piece){
        HashSet<Piece> legalMoves = new HashSet<>();
        List<Piece> movementDirections = MOVEMENT.get(piece.name);
        for(Piece dir:movementDirections){

        }
        return legalMoves;
    }

    

    /**
     **Representation of a chess piece. Holds the information of the piece including name, color, pixel position and more
     */
    public class Piece{
        public String name;
        public String color;

        public int[] directionOfPin;

        public int x;
        public int y;

        public int col;
        public int row;

        public int imageIndex;

        public boolean firstMove;
        public boolean enPassant;
        public boolean isPinned;
        public boolean canSlide;

        public boolean isRay = false; // temporary variable for drawing

        public Piece(String name, String color, int x, int y){
            this.name = name;
            this.color = color;
            this.x = x;
            this.y = y;
            this.col = x/100;
            this.row = y/100;

            directionOfPin = new int[2];
            char firstLetter = name.toLowerCase().charAt(0);

            firstMove = true;
            enPassant = false;
            isPinned = false;
            canSlide = (firstLetter == 'k' || firstLetter == 'p') ? false:true;

        }

        public Piece(int col, int row){
            this.row = row;
            this.col = col;
            this.x = col;
            this.y = row;
        }

        public boolean areEnemies(Piece other){
            if(other==null) return false;
            return !this.color.equals(other.color);
        }

        public boolean equals(Piece other){
            return this.x==other.x && this.y==other.y;
        }

        public boolean equals(String name){
            return this.name.equals(name);
        }
    }


}
