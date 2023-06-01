package com.example.coursework;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class FightController implements Initializable {
    @FXML
    public Pane resultPane;
    public Label resultLabel;
    @FXML
    private GridPane enemy_field;
    @FXML
    private GridPane client_field;
    @FXML
    private Label l_wait;
    @FXML
    private Label l_turn_enemy;
    @FXML
    private Label l_turn_client;
    @FXML
    private Label l_win;
    @FXML
    private Label l_lose;
    @FXML
    private Label l_exist_deck;
    @FXML
    private Label yourLabel;
    @FXML
    private Label EnemyLabel;

    private Client user;
    private String name;
    private String enemyName;
    private Connect connect;
    private Message messageToSend;

    private int[][] matrix;
    private boolean isPlaying;
    private boolean isStarted;

    public void setConnect(Connect connect) {
        this.connect = connect;
    }

    public void setMatrix(int[][] matrix) {
        this.matrix = matrix;
    }

    public void setEnemyName(String txt) {
        enemyName = txt;
    }

    public void setName(String txt) {
        name = txt;
    }

    private String findColor(int ship_numb) {
        switch (ship_numb) {
            case 1: {
                return "#a0bfe8;";
            }
            case 2: {
                return "#2670cb;";
            }
            case 3: {
                return "#033171;";
            }
            case 4: {
                return "#1a2741;";
            }
        }

        return "";
    }

    private void placeClientShips() {
        int x, y;
        ObservableList<Node> childrens = client_field.getChildren();

        for (int i = 0; i < ListOfShip.getInstance().getShips().size(); i++) {
            for (int j = 0; j < ListOfShip.getInstance().getShips().get(i).getCoordinates().size(); j++) {
                x = ListOfShip.getInstance().getShips().get(i).getCoordinates().get(j).getX();
                y = ListOfShip.getInstance().getShips().get(i).getCoordinates().get(j).getY();

                Button button = (Button) childrens.get(x * 10 + y);
                button.setStyle("-fx-background-color: " + findColor(ListOfShip.getInstance().getShips().get(i).getDecks()));
            }
        }
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        yourLabel.setText(name);
        EnemyLabel.setText(enemyName);
        isStarted = false;
        enemy_field.setDisable(true);
        l_wait.setVisible(false);
        resultPane.setVisible(false);
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                Button button = new Button();
                button.setPrefHeight(35);
                button.setPrefWidth(35);

                GridPane.setConstraints(button, j, i);
                client_field.getChildren().add(button);
            }
        }


        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                Button button = new Button();
                button.setPrefHeight(35);
                button.setPrefWidth(35);

                GridPane.setConstraints(button, j, i);
                enemy_field.getChildren().add(button);

                button.setOnAction(event -> {
                    int x = GridPane.getRowIndex(button);
                    int y = GridPane.getColumnIndex(button);
                    button.setDisable(true);
                    Point point = new Point(x, y);
                    messageToSend = new Message(Message.MessageType.HIT, point);
                    new Thread(this::handleMessage).start();
                    enemy_field.setDisable(true);
                    l_turn_enemy.setVisible(false);
                    l_turn_client.setVisible(false);
                });
            }
        }

        placeClientShips();

        isPlaying = true;
        new

                Thread(this::handleMessages).start();
        try {
            connect.send(new Message(Message.MessageType.GAME_START, null));
        } catch (
                IOException e) {
            disconect();
        }

    }

    private void handleMessage() {
        try {
            connect.send(messageToSend);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    private void handleMessages() {
        try {
            while (isPlaying) {
                Message message = connect.receive();

                switch (message.getType()) {
                    case HIT -> {
                        Point point = (Point) message.getData();
                        checkHit(point);
                    }
                    case SHIP_WOUNDED -> {
                        Point point = (Point) message.getData();
                        hitEnemy(point.getX(), point.getY());
                        enemy_field.setDisable(false);
                        l_turn_client.setVisible(true);
                    }
                    case SHIP_DESTROYED -> {
                        Ship ship = (Ship) message.getData();
                        for (Point point : ship.getCoordinates()) {
                            hitEnemy(point.getX(), point.getY());
                        }
                        paintEnemyDisablePoints(ship.getDisablePoints());
                        enemy_field.setDisable(false);
                        l_turn_client.setVisible(true);
                    }
                    case MISS -> {
                        Point point = (Point) message.getData();
                        no_hitEnemy(point.getX(), point.getY());
                        l_turn_enemy.setVisible(true);
                    }
                    case GAME_START -> {
                        if (!isStarted) {
                            if (message.getData() != null) {
                                isStarted = true;
                                if ((int) message.getData() == 0) {
                                    enemy_field.setDisable(false);
                                    l_turn_client.setVisible(true);
                                    l_turn_enemy.setVisible(false);
                                } else {
                                    l_turn_client.setVisible(false);
                                    l_turn_enemy.setVisible(true);
                                }
                            }
                        }
                    }
                    case VICTORY, LOOS -> {
                        resultPane.setVisible(true);
                        resultLabel.setText(message.getType() == Message.MessageType.VICTORY ? "YOU WIN" : "YOU LOOS");
                    }
                }
            }
        } catch (IOException e) {
            disconect();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private void ShowResult() {
    }

    private void markDisablePoints(Point point) {
        int i = 0;
        int x = point.getX();
        int y = point.getY();
        boolean isNotFind = true;
        while (isNotFind && i < ListOfShip.getInstance().getShips().size()) {
            int j = 0;
            while (isNotFind && j < ListOfShip.getInstance().getShips().get(i).getCoordinates().size()) {
                if ((ListOfShip.getInstance().getShips().get(i).getCoordinates().get(j).getX() == x) &&
                        (ListOfShip.getInstance().getShips().get(i).getCoordinates().get(j).getY() == y)) {
                    isNotFind = false;
                }
                j++;
            }
            i++;
        }
    }

    private void checkHit(Point point) {
        try {
            if (matrix[point.getX()][point.getY()] == 0) {
                connect.send(new Message(Message.MessageType.MISS, point));
                no_hitClient(point.getX(), point.getY());
                Button button = (Button) client_field.getChildren().get(point.getX() * 10 + point.getY());
                button.setDisable(true);
                enemy_field.setDisable(false);
                l_turn_client.setVisible(true);
                l_turn_enemy.setVisible(false);
            } else {
                isHit(point.getX(), point.getY());
            }
        } catch (IOException e) {
            disconect();
        }
    }

    void isHit(int x, int y) throws IOException {
        int i = 0;
        boolean isNotFind = true;
        while (isNotFind && i < ListOfShip.getInstance().getShips().size()) {
            int j = 0;
            while (isNotFind && j < ListOfShip.getInstance().getShips().get(i).getCoordinates().size()) {
                if ((ListOfShip.getInstance().getShips().get(i).getCoordinates().get(j).getX() == x) &&
                        (ListOfShip.getInstance().getShips().get(i).getCoordinates().get(j).getY() == y)) {
                    isNotFind = false;
                    int health = ListOfShip.getInstance().getShips().get(i).getHealth();
                    ListOfShip.getInstance().getShips().get(i).setHealth(health - 1);
                    Point point = new Point(x, y);
                    Button button = (Button) client_field.getChildren().get(point.getX() * 10 + point.getY());
                    button.setDisable(true);
                    hitClient(point.getX(), point.getY());
                    if (ListOfShip.getInstance().getShips().get(i).getHealth() == 0) {
                        ListOfShip.getInstance().setShip_count();
                        paintClientDisablePoints(ListOfShip.getInstance().getShips().get(i).getDisablePoints());
                        connect.send(new Message(Message.MessageType.SHIP_DESTROYED, ListOfShip.getInstance().getShips().get(i)));
                    } else {
                        connect.send(new Message(Message.MessageType.SHIP_WOUNDED, point));
                    }


                }
                j++;
            }
            i++;
        }
    }

    private void disconect() {
    }

    public void setEnableEnemyField() {
        enemy_field.setDisable(false);
        l_wait.setVisible(false);
        l_turn_client.setVisible(true);
        l_turn_enemy.setVisible(false);
    }

    public void setDisableEnemyField() {
        enemy_field.setDisable(true);
        l_wait.setVisible(false);
        l_turn_enemy.setVisible(true);
        l_turn_client.setVisible(false);
    }

    public void hitEnemy(int x, int y) {
        Button button = (Button) enemy_field.getChildren().get(x * 10 + y);
        button.setStyle("-fx-background-color: #ff0000;");
    }

    public void no_hitEnemy(int x, int y) {
        Button button = (Button) enemy_field.getChildren().get(x * 10 + y);
        button.setStyle("-fx-background-color: #000000;");

    }

    public void hitClient(int x, int y) {
        Button button = (Button) client_field.getChildren().get(x * 10 + y);
        button.setStyle("-fx-background-color: #ff0000;");
    }

    public void no_hitClient(int x, int y) {
        Button button = (Button) client_field.getChildren().get(x * 10 + y);
        button.setStyle("-fx-background-color: #000000;");
    }

    public void paintEnemyDisablePoints(List<Point> disable_points) {
        for (Point disable_point : disable_points) {
            Button button = (Button) enemy_field.getChildren().get(disable_point.getX() * 10 + disable_point.getY());
            button.setStyle("-fx-background-color: #000000;");
            button.setDisable(true);
        }
    }

    public void paintClientDisablePoints(List<Point> disable_points) {
        for (Point disable_point : disable_points) {
            Button button = (Button) client_field.getChildren().get(disable_point.getX() * 10 + disable_point.getY());
            button.setStyle("-fx-background-color: #000000;");
            button.setDisable(true);
        }
    }

    public void endGame(String status) {
        enemy_field.setDisable(true);
        client_field.setDisable(true);
        l_turn_client.setVisible(false);
        l_turn_enemy.setVisible(false);
        l_wait.setVisible(false);

        if (status.equals("Win"))
            l_win.setVisible(true);
        else
            l_lose.setVisible(true);
    }
    @FXML
    public void toMenu(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("CreateWindow.fxml"));
            Parent part = loader.load();
            Stage stage = new Stage();
            Scene scene = new Scene(part);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.getIcons().add(new Image("file:src/com/example/coursework/icons/island-with-palm-trees.png"));
            stage.setResizable(false);
            stage.setTitle("Battle Ship");
            stage.setScene(scene);
            stage.show();
            stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.close();
        } catch (IOException e) {
            System.out.println("MainController - " + e);
            e.printStackTrace();
        }
    }
}