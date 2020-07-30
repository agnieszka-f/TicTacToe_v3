package TTT;

import java.io.DataOutputStream;
import java.io.*;
import java.util.*;

public class Board {
    private static int size = 3;
    private int activePlayer;
    private int board[][];
    private List<Coordy> list;
    public static final int HUMAN = 1;
    public static final int COMPUTER = -1;
    public static final int EMPTYFIELD = 0;
    private int countOfGame = 0;
    private int countOfHumanWin = 0;
    private int countOfComputerWin = 0;
    private File rankingFilePath;
    private File ranking;

    public Board(){
        board = new int[size][size];
        activePlayer = HUMAN;
        list = new ArrayList<>();
        rankingFilePath = new File("c:\\TTT\\GameRanking");
        ranking = new File(rankingFilePath+"\\Ranking.ttt");
    }

    public void initBoard(){
        for(int row = 0; row < board.length; row++){
            for(int column = 0; column < board[row].length; column++){
                board[row][column] = EMPTYFIELD;
            }
        }
    }
    public int getActivePlayer(){ return activePlayer; }
    public void switchActivePlayer() { activePlayer = -activePlayer; }
    public void setActivePlayer(int activePlayer){ this.activePlayer = activePlayer;}
    public int getSize() { return size; }
    public boolean checkMove(int row, int column){
        if(board[row][column] == EMPTYFIELD) return true;
        else return false;
    }
    public void putMove(Coordy coordy, int activePlayer){
        board[coordy.getX()][coordy.getY()] = activePlayer;
    }
    public boolean playerMove(Coordy coordy){
        if(checkMove(coordy.getX(), coordy.getY())) {
            putMove(coordy,activePlayer);
            return true;
        }
        else return false;
    }
    public boolean checkWin(int player){

        if(checkWinInRow(player) || checkWinInColumns(player) || checkWinInFirstDiagonal(player) || checkWinInSecondDiagonal(player)){
            return true;
        }
        return false;
    }
    public boolean checkWinInRow(int player){
        int sum;
        for(int x = 0; x < size; x++){
            sum = 0;
            for(int y =0; y < size; y++){
                if(board[x][y] == player) sum++;
                if(sum == size) {
                    for(int j=0; j < size; j++){
                        list.add(new Coordy(x,j));
                    }
                    return true;
                }
            }
        }
        return false;
    }
    public boolean checkWinInColumns(int player){
        int sum;
        for(int y = 0; y < size; y++){
            sum = 0;
            for(int x =0; x < size; x++){
                if(board[x][y] == player) sum++;
                if(sum == size) {
                    for(int j=0; j < size; j++){
                        list.add(new Coordy(j,y));
                    }
                    return true;
                }
            }
        }
        return false;
    }
    public boolean checkWinInFirstDiagonal(int player){
        int sum =0;
        for(int x =0; x < size; x++){
            if(board[x][x] == player) sum++;
            if(sum == size) {
                for(int j=0; j < size; j++){
                    list.add(new Coordy(j,j));
                }
                return true;
            }
        }
        return false;
    }
    public boolean checkWinInSecondDiagonal(int player){
        int sum = 0;
        for(int x =0; x < size; x++){
            if(board[x][size - 1 - x] == player) sum++;
            if(sum == size) {
                for(int j=0; j < size; j++){
                    list.add(new Coordy(j,size-1-j));
                }
                return true;
            }
        }
        return false;
    }
    public List<Coordy> getWinnerList(){
        return list;
    }
    public Coordy computerRandomMove(){
        Random random = new Random();
        boolean check = false;
        int row;
        int column;

        do {
            row = random.nextInt(getSize());
            column = random.nextInt(getSize());
            check = checkMove(row, column);
        } while(check == false);

        Coordy coordy = new Coordy(row, column);
        putMove(coordy, COMPUTER);
        return coordy;
    }
    public Coordy computerBlockMove(){
        Coordy coordy;
        for(int x = 0; x < size; x++){
            for(int y = 0; y < size; y++){
                if(checkMove(x,y)){
                    board[x][y] = HUMAN;
                    if(checkWin(HUMAN)){
                        coordy = new Coordy(x,y);
                        putMove(coordy,COMPUTER);
                        list.removeAll(list);
                        return coordy;
                    } else { board[x][y] = EMPTYFIELD; }
                }
            }
        }
        coordy = computerRandomMove();
        return coordy;
    }
    public void saveGame(String filePath) throws IOException {
        File file = new File(filePath);
        DataOutputStream outputStream = null;

        if(file.exists() == false){
            file.createNewFile();
        }

        try{
            outputStream = new DataOutputStream(new FileOutputStream(filePath));
            for(int x =0; x<3; x++){
                for(int y=0; y<3; y++){
                    outputStream.writeInt(board[x][y]);
                }
            }
        }
        finally{
            if(outputStream!=null){
                outputStream.close();
            }
        }
    }
    public int[][] restoreGame(String filePath) throws IOException{

        DataInputStream inputStream = null;
        File file = new File(filePath);
        if(file.length() == 0) return null;
        try{
            inputStream = new DataInputStream(new FileInputStream(filePath));
            for(int x = 0; x < size; x++){
                for(int y = 0; y < size; y++){
                    board[x][y] = inputStream.readInt();
                }
            }
        }
        finally{
            if(inputStream!=null){
                inputStream.close();
                return board;
            }
        }
        return board;
    }
    public void saveRanking(boolean win){

        checkIfFolderAndFileExist();

        DataOutputStream outputStream = null;
        countOfGame++;
        if(win) {
            if (getActivePlayer() == HUMAN) countOfHumanWin++;
            else countOfComputerWin++;
        }
        try{
            outputStream = new DataOutputStream(new FileOutputStream(ranking));
            outputStream.writeInt(countOfGame);
            outputStream.writeInt(countOfHumanWin);
            outputStream.writeInt(countOfComputerWin);
            outputStream.close();
        } catch(IOException e){
            e.printStackTrace();
        }

    }
    public void loadRanking(){

        checkIfFolderAndFileExist();

        DataInputStream inputStream = null;
        try{
            inputStream  = new DataInputStream(new FileInputStream(ranking));
            countOfGame = inputStream.readInt();
            countOfHumanWin = inputStream.readInt();
            countOfComputerWin = inputStream.readInt();
            inputStream.close();
        } catch(IOException e){
            //e.printStackTrace();
            countOfGame = 0;
            countOfHumanWin = 0;
            countOfComputerWin = 0;
        }
    }
    private void checkIfFolderAndFileExist(){

        if(rankingFilePath.exists() == false) rankingFilePath.mkdirs();
        else if(ranking.exists() == false) {
            try{
                ranking.createNewFile();
            } catch(IOException o){
                o.printStackTrace();
            }
        }
    }

    public int getCountOfGame() {
        return countOfGame;
    }

    public int getCountOfHumanWin() {
        return countOfHumanWin;
    }

    public int getCountOfComputerWin() {
        return countOfComputerWin;
    }
}
