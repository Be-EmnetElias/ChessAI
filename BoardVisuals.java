package src;

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.util.*;
import java.io.*;
import javax.imageio.ImageIO;

public class BoardVisuals {

    public static JFrame FRAME;
    public static JButton reset;
    public static JButton showSpacesInCheckButton;
    public static boolean showSpacesInCheck;
    

    public static int SIZE = 800;

    public static Image IMGS[];
    public static Image BG;

    public static Board BOARD;

    public static Board.Piece selectedPiece;
    public static int[] dragCoordinates;
    public static int[] prevMove;
    public static int[] currMove;

    public static HashSet<Board.Piece> moveHint;
    static HashSet<Board.Piece> spacesInCheck;
    

    public static String FENPOSITION = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR"; 
    
    public static Color highlightColor = new Color(255,255,30,127), highlightRed = new Color(235,97,80,150),
                        outlineColor = new Color(255,255,255,100), moveHintColor = new Color(0,0,0,25),
                        captureColor = new Color(153,255,153,204), highlightDarkRed = new Color(90,7,0,204),
                        pinnedColor = new Color(255, 87, 51,204);

    public static void main(String[] args) throws IOException, InterruptedException{
        FRAME = new JFrame();
        dragCoordinates = new int[2];
        prevMove = new int[2];
        currMove = new int[2];

        int PANELWIDTH = 300;
        FRAME.setBounds(10,10,SIZE+PANELWIDTH,SIZE);
        FRAME.setUndecorated(true);

        
        BG = ImageIO.read(new File("imgs\\bg_green.png"));
        BG = BG.getScaledInstance(800,800,BufferedImage.SCALE_SMOOTH);
        BufferedImage all = ImageIO.read(new File("imgs\\pieces.png"));

        IMGS = new Image[12];
        int ind = 0;
        for(int y=0;y<400;y+=200){
            for(int x=0;x<1200;x+=200){
                IMGS[ind] = all.getSubimage(x,y,200,200).getScaledInstance(95,95,BufferedImage.SCALE_SMOOTH);
                ind++;
            }
        }

        //scrambled positions (choose one)
        FENPOSITION = "r1b1k1nr/p2p1pNp/n2B4/1p1NP2P/6P1/3P1Q2/P1P1K3/q5b1";
        //FENPOSITION = "r1bqkbnr/ppp2ppp/2np4/4p3/2B1P3/5Q2/PPPP1PPP/RNB1K1NR";
        //FENPOSITION = "r1b1k1nr/p2n1p1p/2p1p3/qpbp2p1/2B1PB2/2NP1N2/PPP1QPPP/R4RK1";
        BOARD = new Board(FENPOSITION);
       
        reset = new JButton("RESET GAME");
        showSpacesInCheckButton = new JButton("SHOW SPACES \n IN CHECK");

        reset.setBounds(810,400,100,100);
        showSpacesInCheckButton.setBounds(910,400,100,100);



        reset.addActionListener(new ActionListener() { 
            public void actionPerformed(ActionEvent e) {
                BOARD=null;
                selectedPiece=null;
                moveHint=null;
                spacesInCheck=null;
                BOARD = new Board(FENPOSITION);
                prevMove = new int[2];
                currMove = new int[2];

                FRAME.repaint();
            }
          }); 
        showSpacesInCheckButton.addActionListener(new ActionListener() { 
            public void actionPerformed(ActionEvent e) {
                showSpacesInCheck = !showSpacesInCheck;
            }
          }); 
        FRAME.add(reset);
        FRAME.add(showSpacesInCheckButton);
        
         JPanel pn = new JPanel(){
            @Override
            public void paint(Graphics g){
                //bg
                g.drawImage(BG,0,0,this);

                //info panel
                g.setColor(Color.DARK_GRAY);
                g.fillRect(800, 0, 800+PANELWIDTH, 800);

                g.setColor(Color.white);
                if(BOARD.GAMEOVER){
                    String message = "";
                    int score = BOARD.gameOver();
                    if(score==1) message = "CHECKMATE";
                    if(score==2) message = "STALEMATE";
                    if(score==3) message = "INSUFFICIENT MATERIAL";
                    g.drawString(message,810,190);    
                }
                else g.drawString("CURRENT BOARD",810,30);
                for(int row=0;row<8;row++){
                    for(int col=0;col<8;col++){
                        if(BOARD.board[row][col]==null){
                            g.drawString("\u25AB",810+col*17,50+row*17);
                        }else{
                            g.drawString(BOARD.board[row][col].name.charAt(0)+"",810+col*17,50+row*17);
                        }
                    }
                }

                //spaces in check for selected pieces' team
                if(spacesInCheck != null && showSpacesInCheck){
                    for(Board.Piece square:spacesInCheck){
                        g.setColor(highlightRed);
                        g.fillRect(square.x*100,square.y*100,100,100);
                    }
                }
                //outline squares that are being hovered over
                g.setColor(outlineColor);
               
                if(dragCoordinates[0]<800 && dragCoordinates[1]<800) g.fillRect(dragCoordinates[0],dragCoordinates[1],100,100);
                
                //highlight squares to show move history
                g.setColor(highlightColor);
                g.fillRect(prevMove[0]/100 * 100,prevMove[1]/100 * 100,100,100);
                g.fillRect(currMove[0]/100 * 100,currMove[1]/100 * 100,100,100);


                //possible moves and capture moves
                if(moveHint != null && selectedPiece != null){
                    for(Board.Piece move:moveHint){
                        if(BOARD.getPiece(move.x,move.y) != null && selectedPiece.areEnemies(BOARD.getPiece(move.x,move.y)) || (selectedPiece.is("pawn") && Math.abs(selectedPiece.col-move.x)==1)){
                            g.setColor(captureColor);
                            g.fillOval(move.x*100, move.y*100, 100, 100);
                        }else{
                            g.setColor(moveHintColor);
                            g.fillArc(move.x*100+37, move.y*100+37, 25, 25 ,0,360);    
                        }

                    }
                }

                //draws pieces
                for(Board.Piece piece:BOARD.getPieces("all")){
                    if(piece == selectedPiece) continue; //skip the selected piece
                    g.drawImage(IMGS[piece.imageIndex],piece.x+5,piece.y+3,this);
                    if(piece.isRay){
                        g.setColor(highlightRed);
                        g.fillRect(piece.col*100,piece.row*100,100,100);
                    }
                }

                //draw selected piece on top
                if(selectedPiece != null) g.drawImage(IMGS[selectedPiece.imageIndex],selectedPiece.x+5,selectedPiece.y+3,this);
    
            }
        };
        FRAME.add(pn);
        FRAME.addMouseMotionListener(new MouseMotionListener(){

            @Override
            public void mouseDragged(MouseEvent e) {

                dragCoordinates[0] = e.getX()/100 * 100;
                dragCoordinates[1] = e.getY()/100 * 100;

                if(selectedPiece != null){
                    selectedPiece.x = e.getX()-45;
                    selectedPiece.y = e.getY()-45;
                }

                FRAME.repaint();
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                dragCoordinates[0] = e.getX()/100 * 100;
                dragCoordinates[1] = e.getY()/100 * 100;

                FRAME.repaint();
            }

        });
        FRAME.addMouseListener(new MouseListener(){

            @Override
            public void mouseClicked(MouseEvent e) {
                
            }

            @Override
            public void mousePressed(MouseEvent e) {
                if(BOARD.GAMEOVER) return;
                selectedPiece = BOARD.getPiece(e.getX()/100, e.getY()/100);

                prevMove[0] = e.getX()/100 * 100;
                prevMove[1] = e.getY()/100 * 100;

                if(selectedPiece != null){
                    moveHint = BOARD.allMoves(selectedPiece);
                    spacesInCheck = BOARD.calculateSpacesInCheck(selectedPiece.color);
                   
                }
                FRAME.repaint();

            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if(selectedPiece==null) return;
                currMove[0] = e.getX()/100 * 100;
                currMove[1] = e.getY()/100 * 100;
                if(BOARD.isMoveValid(selectedPiece, e.getX()/100, e.getY()/100)){
                    BOARD.move(selectedPiece,e.getX()/100,e.getY()/100);
                    selectedPiece = null;
                    moveHint = null;
                    spacesInCheck = null;
                }else{
                    selectedPiece.x = selectedPiece.col*100;
                    selectedPiece.y = selectedPiece.row*100;

                }
                


                FRAME.repaint();

                
            }

            @Override
            public void mouseEntered(MouseEvent e) {}

            @Override
            public void mouseExited(MouseEvent e) {}
                

        });
        FRAME.setDefaultCloseOperation(3);
        FRAME.setVisible(true);
        
    }

    
    
}
