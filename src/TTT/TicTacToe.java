package TTT;

import java.io.File;
import java.io.IOException;
import java.util.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import javafx.stage.Popup;
import javafx.stage.Stage;
import java.time.*;
import java.time.format.DateTimeFormatter;

public class TicTacToe extends Application {
    private VBox vBox;
    private MenuBar menuBar;
    private Menu menu1;
    private Menu menu2;
    private MenuItem newGameMenuItem;
    private MenuItem saveGameMenuItem;
    private MenuItem restoreGameMenuItem;
    private MenuItem closeMenuItem;
    private MenuItem aboutMenuItem;
    private Button [][] buttons;
    private GridPane gridPane;
    private Label labelInfo;
    private Label firstMove;
    private Button buttonFirstHuman;
    private Button buttonFirstComputer;
    private BorderPane borderPane;
    private FileChooser fileChooser;
    private File fileData;
    private Board board;
    private int countOfMove;
    private File gameStateFolder;
    private Popup popup;

    @Override
    public void start(Stage primaryStage) throws Exception {
        board = new Board();
        board.initBoard();
        board.loadRanking();
        countOfMove = board.getSize() * board.getSize();
        initFileChooser();
        Scene scene = new Scene(initVBox(), 300,360);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Kółko i krzyżyk");
        primaryStage.show();
        initEventMenuItem(primaryStage);
        firstMoveSlection();
    }
    public static void main(String[] args){
        launch(args);
    }
    private void buttonsAction(){
        for(int i = 0; i < buttons.length; i++){
            for(int j = 0; j < buttons[i].length; j++){
                final int x = i, y = j;
                buttons[i][j].setOnAction( event -> playerAction(x,y));
            }
        }
    }
    private void playerAction(int x, int y){
        if(board.playerMove(new Coordy(x, y))){
            if(board.getActivePlayer() == board.HUMAN && countOfMove > 0) {
                buttons[x][y].setText("X");
                buttons[x][y].setDisable(true);
                countOfMove--;
                if (board.checkWin(board.getActivePlayer()) == false && countOfMove >0) {
                    labelInfo.setText("Ruch komputera");
                    board.switchActivePlayer();
                    computerAction();
                } else {
                    reflectWinningStateOnBoard();
                }
            }
        }
    }
    private void computerAction(){
        if(board.getActivePlayer() == board.COMPUTER ) {
            Coordy coordy = board.computerBlockMove();

            buttons[coordy.getX()][coordy.getY()].setText("O");
            buttons[coordy.getX()][coordy.getY()].setDisable(true);
            countOfMove--;

            if(board.checkWin(board.getActivePlayer()) == false && countOfMove>0) {
                labelInfo.setText("Twój ruch");
                board.switchActivePlayer();
            } else { reflectWinningStateOnBoard(); }
        }
    }
    private void buttonsClean(){
        for(int i =0; i<buttons.length; i++) {
            for (int j = 0; j < buttons[i].length; j++) {
                buttons[i][j].setText("");
                buttons[i][j].setDisable(false);
                buttons[i][j].setTextFill(Color.BLACK);
            }
        }
    }
    private void reflectWinningStateOnBoard(){
        disableBoardButtons();
        highlightWinningLine();
    }
    private void disableBoardButtons(){
        for(int i =0; i<buttons.length; i++) {
            for (int j = 0; j < buttons[i].length; j++) {
                buttons[i][j].setDisable(true);
            }
        }
    }
    private void availableBoardButtons(){
        for(int i =0; i<buttons.length; i++) {
            for (int j = 0; j < buttons[i].length; j++) {
                buttons[i][j].setDisable(false);
            }
        }
    }
    private void highlightWinningLine(){
        List temp = board.getWinnerList();
        if(temp.isEmpty()){
            labelInfo.setText("Remis!!!");
            board.saveRanking(false);
            generatePopup(0);
        } else {
            for(int i=0; i<temp.size(); i++){
                Coordy t = (Coordy) temp.get(i);
                buttons[t.getX()][t.getY()].setTextFill(Color.GREEN);
            }
            labelInfo.setText("Wygrana!!!");
            board.saveRanking(true);
            generatePopup(board.getActivePlayer());
        }
    }
    private MenuBar initMenuBar(){
        menuBar = new MenuBar();
        menu1 = new Menu("Edycja");
        menu2 = new Menu("Pomoc");
        newGameMenuItem = new MenuItem("Nowa gra");
        saveGameMenuItem = new MenuItem("Zapisz stan gry");
        restoreGameMenuItem = new MenuItem("Przywróć stan gry");
        closeMenuItem = new MenuItem("Zamknij");
        menu1.getItems().addAll(newGameMenuItem,saveGameMenuItem,restoreGameMenuItem,closeMenuItem);
        aboutMenuItem = new MenuItem("O grze");
        menu2.getItems().addAll(aboutMenuItem);
        menuBar.getMenus().addAll(menu1,menu2);

        return menuBar;
    }
    private GridPane initGridPane(){
        gridPane=new GridPane();
        initButtons();
        gridPane.setPadding(new Insets(20));
        gridPane.setVgap(5);
        gridPane.setHgap(5);
        gridPane.setPrefHeight(Region.USE_COMPUTED_SIZE);
        gridPane.setMaxHeight(Region.USE_COMPUTED_SIZE);
        gridPane.setMinHeight(Region.USE_COMPUTED_SIZE);

        return gridPane;
    }
    private void initButtons(){
        buttons = new Button[board.getSize()][board.getSize()];
        for(int i =0; i<buttons.length; i++){
            for(int j=0; j<buttons[i].length; j++){
                buttons[i][j] = new Button("");
                buttons[i][j].setMinWidth(Region.USE_COMPUTED_SIZE);
                buttons[i][j].setMaxWidth(Region.USE_COMPUTED_SIZE);
                buttons[i][j].setMinHeight(Region.USE_COMPUTED_SIZE);
                buttons[i][j].setMaxHeight(Region.USE_COMPUTED_SIZE);
                buttons[i][j].setPrefWidth(100);
                buttons[i][j].setPrefHeight(100);
                buttons[i][j].setFont(Font.font("Arial", FontWeight.BOLD, 40));
                buttons[i][j].setTextFill(Color.BLACK);
                buttons[i][j].setDisable(true);
                gridPane.addRow(i, buttons[i][j]);
            }
        }
    }
    private VBox initVBox(){
        vBox = new VBox();
        vBox.getChildren().addAll(initMenuBar(),initBorderPane(),initGridPane(),initLabelInfo());

        return vBox;
    }
    private void initEventMenuItem(Stage primaryStage){
        newGameMenuItem.addEventHandler(ActionEvent.ACTION,  event -> newGame());

        closeMenuItem.addEventHandler(ActionEvent.ACTION,  event -> Platform.exit());

        saveGameMenuItem.addEventHandler(ActionEvent.ACTION,  event -> {
            saveGameState(primaryStage);
            Platform.exit();
        });
        restoreGameMenuItem.addEventHandler(ActionEvent.ACTION, event -> restoresThePreviousGameState(primaryStage));

    }
    private void newGame(){
        labelInfo.setText("");
        board = new Board();
        board.initBoard();
        board.loadRanking();
        countOfMove = board.getSize() * board.getSize();
        buttonsClean();
        disableBoardButtons();
        setAvailableButtonsFirstMove();
        firstMoveSlection();
    }
    private Label initLabelInfo(){
        labelInfo = new Label("");
        return labelInfo;
    }
    private void firstMoveSlection(){

        buttonFirstHuman.setOnAction(event -> {
            availableBoardButtons();
            buttonsAction();
            labelInfo.setText("Twój ruch");
            setDisableButtonsFirstMove(Board.HUMAN);
        });
        buttonFirstComputer.setOnAction(event -> {
            board.setActivePlayer(board.COMPUTER);
            labelInfo.setText("Ruch komputera");
            availableBoardButtons();
            computerAction();
            buttonsAction();
            setDisableButtonsFirstMove(Board.COMPUTER);
        });
    }
    private BorderPane initBorderPane(){
        borderPane = new BorderPane();
        borderPane.setPadding(new Insets(15, 20, 10, 10));
        buttonFirstHuman = new Button("Ja");
        buttonFirstComputer = new Button("Komputer");
        firstMove = new Label("Kto ma pierwszy ruch?");
        borderPane.setLeft(firstMove);
        borderPane.setCenter(buttonFirstHuman);
        borderPane.setRight(buttonFirstComputer);
        return borderPane;
    }
    private void setAvailableButtonsFirstMove(){
        buttonFirstHuman.setDisable(false);
        buttonFirstComputer.setDisable(false);
        buttonFirstComputer.setTextFill(Color.BLACK);
        buttonFirstHuman.setTextFill(Color.BLACK);
    }
    private void setDisableButtonsFirstMove(int player){
        buttonFirstHuman.setDisable(true);
        buttonFirstComputer.setDisable(true);
        if(player == Board.HUMAN){
            buttonFirstHuman.setTextFill(Color.RED);
        } else if(player == Board.COMPUTER) { buttonFirstComputer.setTextFill(Color.RED); }
    }
    private void saveGameState(Stage primaryStage){

        fileChooser.setInitialFileName(fileName()+".ttt");
        fileData = fileChooser.showSaveDialog(primaryStage);
        if(fileData != null) {
            String path = fileData.getAbsolutePath();
            try {
                board.saveGame(path);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private void restoresThePreviousGameState(Stage primaryStage){
        newGame();
        int temp[][] = null;

        fileData = fileChooser.showOpenDialog(primaryStage);
        if(fileData !=null) {
            String filePath = fileData.getAbsolutePath();
            try {
                temp = board.restoreGame(filePath);
                if(temp!= null) {
                    restoreButtons(temp);
                    restoreReflectWinningStateBoard();
                } else{
                    labelInfo.setText("Stan gry nie może zostać odtworzony");
                    disableBoardButtons();
                    setDisableButtonsFirstMove(100);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private void restoreButtons(int temp[][]){
        for (int i = 0; i < buttons.length; i++) {
            for (int j = 0; j < buttons[i].length; j++) {
                if (temp[i][j] == board.HUMAN) {
                    buttons[i][j].setText("X");
                    buttons[i][j].setDisable(true);
                    countOfMove--;
                } else if (temp[i][j] == board.COMPUTER) {
                    buttons[i][j].setText("O");
                    buttons[i][j].setDisable(true);
                    countOfMove--;
                } else buttons[i][j].setDisable(false);
            }
        }
        if (countOfMove % 2 == 0) {
            setDisableButtonsFirstMove(board.COMPUTER);
        } else setDisableButtonsFirstMove(board.HUMAN);
    }
    private void restoreReflectWinningStateBoard(){
        if(board.checkWin(board.HUMAN) == true || board.checkWin(board.COMPUTER) == true){
            reflectWinningStateOnBoard();
        } else if(countOfMove != 0){
            labelInfo.setText("Twój ruch");
            buttonsAction();
        } else reflectWinningStateOnBoard();
    }
    private String fileName(){
        LocalDateTime date = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMyyyyHHmmss");

        return date.format(formatter);
    }
    private void initFileChooser(){
        fileChooser = new FileChooser();
        gameStateFolder = new File("c:\\TTT\\GameState");
        if(gameStateFolder.exists() == false) gameStateFolder.mkdirs();
        fileChooser.setInitialDirectory(gameStateFolder);
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("TicTacToe Files", "*.ttt"));
    }
    private void generatePopup(int status){
        Stage secondStage = new Stage();
        TilePane tilepane = new TilePane();
        TextArea textArea = new TextArea();
        textArea.setPrefHeight(150);
        textArea.setPrefWidth(200);
        popup = new Popup();
        popup.setX(100);
        popup.setY(100);
        popup.getContent().add(textArea);
        String a = String.valueOf(board.getCountOfGame());
        String b = String.valueOf(board.getCountOfHumanWin());
        String c = String.valueOf(board.getCountOfComputerWin());
        String s;
        if(status == board.HUMAN) s = "Wygrałeś!!!";
        else if(status == board.COMPUTER) s="Wygrał komputer!";
        else s="Remis!!!";
        textArea.setText("\n"+s+"\n\nRanking:\nLiczba rozgrywek: " + a + "\nLiczba wygranych człowieka: " + b + "\nLiczba wygranych komputera: " + c);
        textArea.setEditable(false);
        popup.show(secondStage);
        tilepane.getChildren().add(textArea);
        Scene secondScene = new Scene(tilepane,200,150);
        secondStage.setTitle("Ranking");
        secondStage.setScene(secondScene);
        secondStage.show();
    }
}
