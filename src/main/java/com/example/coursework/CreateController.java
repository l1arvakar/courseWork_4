package com.example.coursework;

import com.example.coursework.Message.MessageType;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class CreateController implements Initializable {
    @FXML
    private Label count4ship;
    @FXML
    private Label nameLabel;
    @FXML
    private Label count3ship;
    @FXML
    private Label count2ship;
    @FXML
    private Label count1ship;
    @FXML
    private GridPane field;
    @FXML
    private Label c_decks;
    @FXML
    private Label l_selected_ship;
    @FXML
    private Label l_count_decks;
    @FXML
    private Label l_select_ship;
    @FXML
    private Label l_info;
    @FXML
    private Label waitingLabel;
    @FXML
    private Button continueBtn;

    private Connect connect;
    private String name;
    private int ship_count = 0;
    private boolean step = true, remove_bool = false;
    private int[][] matrix = new int[10][10];
    private List<Point> coordinates;
    private List<Point> disable_points;

    private int selected_ship, count_decks = 0, last_ship = 0;
    private String orientation;
    private String color;
    private boolean isEnemyReady;
    private String enemyName;
    private boolean isPlaning;
    private boolean isReady;
    private boolean isSended;
    private Stage myStage;

    private boolean isDeckExist(int row, int column) {
        if (matrix[row][column] == 0)
            return true;

        l_info.setVisible(true);
        l_info.setText("Error: the deck is already standing");
        return false;
    }

    private boolean checkXY(String xy, int rc) {
        int tmp;

        for (Point coordinate : coordinates) {
            if (xy.equals("y"))
                tmp = coordinate.getY();
            else
                tmp = coordinate.getX();

            if (Math.abs(tmp - rc) == 1)
                return true;
        }

        return false;
    }

    private boolean canPlaceShipPosition(int row, int column) {
        if (coordinates.size() == 0) {
            orientation = "horizontal";
            return true;
        } else {
            if (coordinates.size() == 1) {
                if ((row == coordinates.get(0).getX()) && (Math.abs(column - coordinates.get(0).getY()) == 1)) {
                    orientation = "horizontal";
                    return true;
                }

                if ((column == coordinates.get(0).getY()) && (Math.abs(row - coordinates.get(0).getX()) == 1)) {
                    orientation = "vertical";
                    return true;
                }
            }

            if (coordinates.size() > 1) {
                if (coordinates.get(0).getX() == coordinates.get(1).getX()) {
                    if ((coordinates.get(0).getX() == row) && (checkXY("y", column))) {
                        orientation = "horizontal";
                        return true;
                    }
                }

                if (coordinates.get(0).getY() == coordinates.get(1).getY()) {
                    if ((coordinates.get(0).getY() == column) && (checkXY("x", row))) {
                        orientation = "vertical";
                        return true;
                    }
                }
            }
        }

        l_info.setVisible(true);
        l_info.setText("Error: there can be no deck");
        return false;
    }

    private int findIndex() {
        int index = 0;
        int x = coordinates.get(0).getX();
        int y = coordinates.get(0).getY();

        for (int i = 1; i < coordinates.size(); i++) {
            if ((coordinates.get(i).getX() <= x) && (coordinates.get(i).getY() <= y)) {
                x = coordinates.get(i).getX();
                y = coordinates.get(i).getY();
                index = i;
            }
        }

        return index;
    }

    private void setDisablePoint(Button button) {
        Point point = new Point(GridPane.getRowIndex(button), GridPane.getColumnIndex(button));
        disable_points.add(point);
    }

    private void updateField(int ship_size) {
        int index = findIndex();
        int row = coordinates.get(index).getX(), column = coordinates.get(index).getY();
        int position;
        Button button;

        if (orientation.equals("horizontal")) {
            for (int i = 0; i < ship_size + 2; i++) {
                position = (row - 1) * 10 + (column - 1) + i;

                if ((position < 0) || (Math.abs((position / 10) - row) != 1))
                    continue;

                button = (Button) field.getChildren().get(position);
                button.setDisable(true);
                setDisablePoint(button);
            }

            position = row * 10 + column - 1;
            if ((position >= 0) && (row * 10 - 1 < position)) {
                button = (Button) field.getChildren().get(position);
                button.setDisable(true);
                setDisablePoint(button);
            }

            for (int i = 0; i < ship_size + 2; i++) {
                position = (row + 1) * 10 + (column - 1) + i;

                if ((position > 99) || (position < 0) || (Math.abs((position / 10) - row) != 1))//((row + 1) * 10 > position))
                    continue;

                button = (Button) field.getChildren().get(position);
                button.setDisable(true);
                setDisablePoint(button);
            }

            position = row * 10 + column + ship_size;
            if ((position >= 0) && ((row + 1) * 10 > position)) {
                button = (Button) field.getChildren().get(position);
                button.setDisable(true);
                setDisablePoint(button);
            }
        } else {
            for (int i = 0; i < ship_size + 2; i++) {
                position = (row - 1) * 10 + (column - 1) + i * 10;

                if ((position < 0) || (Math.abs((position % 10) - column) != 1) || (position >= 100))
                    continue;

                button = (Button) field.getChildren().get(position);
                button.setDisable(true);
                setDisablePoint(button);
            }

            position = (row - 1) * 10 + column;
            if ((position >= 0)) {
                button = (Button) field.getChildren().get(position);
                button.setDisable(true);
                setDisablePoint(button);
            }

            for (int i = 0; i < ship_size + 2; i++) {
                position = (row - 1) * 10 + (column + 1) + i * 10;

                if ((position < 0) || (Math.abs((position % 10) - column) != 1) || (position >= 100))
                    continue;

                button = (Button) field.getChildren().get(position);
                button.setDisable(true);
                setDisablePoint(button);
            }

            position = (row + ship_size) * 10 + column;
            if ((position >= 0) && (position < 100)) {
                button = (Button) field.getChildren().get(position);
                button.setDisable(true);
                setDisablePoint(button);
            }
        }
    }

    private void changeShipCount(int ship) {
        switch (ship) {
            case 1: {
                String value = count1ship.getText();
                count1ship.setText(String.valueOf((Integer.parseInt(value) - 1)));
                break;
            }
            case 2: {
                String value = count2ship.getText();
                count2ship.setText(String.valueOf((Integer.parseInt(value) - 1)));
                break;
            }
            case 3: {
                String value = count3ship.getText();
                count3ship.setText(String.valueOf((Integer.parseInt(value) - 1)));
                break;
            }
            case 4: {
                String value = count4ship.getText();
                count4ship.setText(String.valueOf((Integer.parseInt(value) - 1)));
                break;
            }
        }
    }

    private void buttonHandler(Button button) {
        if (!remove_bool) {
            if (step) {
                coordinates = new ArrayList<>();
                disable_points = new ArrayList<>();
                step = false;
            }

            if (canPlaceShipPosition(GridPane.getRowIndex(button), GridPane.getColumnIndex(button))
                    && isDeckExist(GridPane.getRowIndex(button), GridPane.getColumnIndex(button))) {

                l_info.setVisible(false);

                if (count_decks != 0 && count_decks-- > 0) {
                    Point point = new Point(GridPane.getRowIndex(button), GridPane.getColumnIndex(button));
                    coordinates.add(point);

                    matrix[GridPane.getRowIndex(button)][GridPane.getColumnIndex(button)] = selected_ship;

                    button.setStyle("-fx-background-color: " + color);
                    c_decks.setText(String.valueOf(count_decks));

                    if (count_decks == 0) {
                        updateField(selected_ship);
                        ++ship_count;

                        Ship ship = new Ship();
                        ship.setHealth(selected_ship);
                        ship.setDecks(selected_ship);
                        ship.setCoordinates(coordinates);
                        ship.setDisablePoints(disable_points);
                        ship.setOrientation(orientation);
                        ListOfShip.getInstance().addShip(ship);

                        step = true;
                        changeShipCount(selected_ship);
                        l_selected_ship.setVisible(false);
                        l_count_decks.setVisible(false);
                        c_decks.setVisible(false);
                        l_select_ship.setVisible(true);
                    }
                }
            }

            if (Integer.parseInt(count1ship.getText()) == 0 &&
                    Integer.parseInt(count2ship.getText()) == 0 &&
                    Integer.parseInt(count3ship.getText()) == 0 &&
                    Integer.parseInt(count4ship.getText()) == 0) {
                l_select_ship.setVisible(false);
                l_info.setVisible(true);
                l_info.setText("All ships on display");
            }
        } else {
            removeShip(GridPane.getRowIndex(button), GridPane.getColumnIndex(button));
            remove_bool = false;
        }
    }

    public void setName(String txt) {
        name = txt;
    }

    private void initButtonsHandler() {
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                Button button = new Button();
                button.setPrefHeight(35);
                button.setPrefWidth(35);

                GridPane.setConstraints(button, j, i);
                field.getChildren().add(button);

                button.setOnAction(event -> buttonHandler(button));
            }
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        waitingLabel.setVisible(false);
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++)
                matrix[i][j] = 0;
        }
        isReady = false;
        isEnemyReady = false;
        nameLabel.setText(name);
        initButtonsHandler();
        isPlaning = true;
        new Thread(this::handleMessages).start();
    }

    private void handleMessages() {
        try {
            while (isPlaning) {
                Message message = connect.receive();
                if (message.getType() != MessageType.CONNECT) {
                    switch (message.getType()) {
                        case PLANNING_START -> {
                            isEnemyReady = false;
                        }
                        case PLANNING_FINISH -> {
                            isEnemyReady = true;
                            enemyName = (String) message.getData();
                            if (isReady) {
                                if (isPlaning) {
                                    Platform.runLater(() -> {
                                        loadWindow(new ActionEvent());

                                    });
                                }
                                isPlaning = false;
                                if (!isSended) {
                                    connect.send(new Message(MessageType.PLANNING_FINISH, name));
                                    isSended = true;
                                } else {
                                    isSended = false;
                                }
                            }
                        } case GAME_START -> {
                            if (isPlaning) {
                                Platform.runLater(() -> {
                                    loadWindow(new ActionEvent());

                                });
                            }
                            isPlaning = false;
                        }
                    }
                }
            }
        } catch (IOException e) {
            disconnect();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private void handleMessage() throws IOException {
        connect.send(new Message(MessageType.PLANNING_FINISH, name));
    }

    private boolean canPlaceShipCount(int ship) {
        switch (ship) {
            case 1: {
                return Integer.parseInt(count1ship.getText()) != 0;
            }
            case 2: {
                return Integer.parseInt(count2ship.getText()) != 0;
            }
            case 3: {
                return Integer.parseInt(count3ship.getText()) != 0;
            }
            case 4: {
                return Integer.parseInt(count4ship.getText()) != 0;
            }
        }

        return true;
    }

    private void shipClickedHandler(int ship_numb, String color) {
        if (!l_info.getText().equals("All ships on display")) {
            if ((count_decks == 0) || ((last_ship != 0) && (last_ship == count_decks))) {
                if (canPlaceShipCount(ship_numb)) {
                    l_selected_ship.setVisible(true);
                    l_count_decks.setVisible(true);
                    c_decks.setVisible(true);
                    l_select_ship.setVisible(false);
                    l_info.setVisible(false);
                    l_selected_ship.setText("Selected ship - " + ship_numb);
                    c_decks.setText(String.valueOf(ship_numb));
                    count_decks = ship_numb;
                    selected_ship = ship_numb;
                    this.color = color;
                    last_ship = ship_numb;
                } else {
                    c_decks.setVisible(false);
                    l_count_decks.setVisible(false);
                    l_select_ship.setVisible(false);
                    l_selected_ship.setVisible(false);
                    l_info.setVisible(true);
                    l_info.setText("Error: 0 ships of this type left");
                }
            } else {
                l_info.setVisible(true);
                l_info.setText("Error: deliver the remaining decks");
            }
        }
    }

    @FXML
    void ship1Clicked() {
        shipClickedHandler(1, "#a0bfe8;");
    }

    @FXML
    void ship2Clicked() {
        shipClickedHandler(2, "#2670cb;");
    }

    @FXML
    void ship3Clicked() {
        shipClickedHandler(3, "#033171;");
    }

    @FXML
    void ship4Clicked() {
        shipClickedHandler(4, "#1a2741;");
    }


    @FXML
    void setOnShow() {

    }

    @FXML
    void continueClicked(ActionEvent event) {
        myStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        if (ship_count == 10) {
            try {
                isReady = true;
                if (isEnemyReady) {
                    if (isPlaning) {
                        loadWindow(event);
                        isPlaning = false;
                    }
                } else {
                    waitingLabel.setVisible(true);
                    continueBtn.setVisible(false);
                }
                new Thread(() -> {
                    try {
                        handleMessage();
                    } catch (IOException e) {
                        disconnect();
                    }
                }).start();
            } catch (Exception e) {
                disconnect();
            }
        } else {
            l_info.setVisible(true);
            l_info.setText("Error: Need to set all ships");
        }
    }

    private void loadWindow(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("FightWindow.fxml"));
            loader.setControllerFactory(controllerClass -> {
                FightController controller = new FightController();
                controller.setName(name);
                controller.setConnect(connect);
                controller.setMatrix(matrix);
                controller.setEnemyName(enemyName);
                return controller;
            });

            Parent part = loader.load();
            Stage stage = new Stage();
            Scene scene = new Scene(part);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.getIcons().add(new Image("file:src/com/example/coursework/icons/island-with-palm-trees.png"));
            stage.setResizable(false);
            stage.setTitle("Battle Ship");
            stage.setScene(scene);
            stage.show();
            myStage.close();
        } catch (IOException e) {
            System.out.println("MainController - " + e);
            e.printStackTrace();
        }
    }

    private void removeAlgorithm() {
        ObservableList<Node> childrens = field.getChildren();
        while (childrens.size() != 0) {
            for (int i = 0; i < childrens.size(); i++) {
                Button button = (Button) childrens.get(i);
                field.getChildren().remove(button);
            }

            childrens = field.getChildren();
        }

        initButtonsHandler();
    }

    @FXML
    void removeallClicked() {
        removeAlgorithm();

        ship_count = count_decks = 0;
        coordinates.clear();
        disable_points.clear();
        l_select_ship.setVisible(true);
        l_count_decks.setVisible(false);
        l_selected_ship.setVisible(false);
        c_decks.setVisible(false);
        count1ship.setText("4");
        count2ship.setText("3");
        count3ship.setText("2");
        count4ship.setText("1");
        l_info.setText("Error: 0 ships of this type left");
        l_info.setVisible(false);

        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                matrix[i][j] = 0;
            }
        }
        ListOfShip.getInstance().removeShips();
        notReady();
    }

    private void notReady() {
        try {
            connect.send(new Message(MessageType.PLANNING_START, null));
            isReady = false;
            waitingLabel.setVisible(false);
            continueBtn.setVisible(true);
        } catch (IOException e) {
            disconnect();
        }

    }

    private int getIndexRemoveShip(int row, int column) {
        for (int i = 0; i < ListOfShip.getInstance().getShips().size(); i++) {
            Ship ship = ListOfShip.getInstance().getShips().get(i);

            for (int j = 0; j < ship.getCoordinates().size(); j++) {
                if ((ship.getCoordinates().get(j).getX() == row) && (ship.getCoordinates().get(j).getY() == column))
                    return i;
            }
        }

        return -1;
    }

    private void changeColor(int ship_numb) {
        switch (ship_numb) {
            case 1: {
                color = "#a0bfe8;";
                break;
            }
            case 2: {
                color = "#2670cb;";
                break;
            }
            case 3: {
                color = "#033171;";
                break;
            }
            case 4: {
                color = "#1a2741;";
                break;
            }
        }
    }

    private void incCount(int ship_numb) {
        switch (ship_numb) {
            case 1: {
                String value = count1ship.getText();
                count1ship.setText(String.valueOf((Integer.parseInt(value) + 1)));
                break;
            }
            case 2: {
                String value = count2ship.getText();
                count2ship.setText(String.valueOf((Integer.parseInt(value) + 1)));
                break;
            }
            case 3: {
                String value = count3ship.getText();
                count3ship.setText(String.valueOf((Integer.parseInt(value) + 1)));
                break;
            }
            case 4: {
                String value = count4ship.getText();
                count4ship.setText(String.valueOf((Integer.parseInt(value) + 1)));
                break;
            }
        }
    }

    private void paintPoints(List<Point> points) {
        ObservableList<Node> childrens = field.getChildren();

        for (int i = 0; i < points.size(); i++) {
            Button button = (Button) childrens.get(points.get(i).getX() * 10 + points.get(i).getY());
            button.setStyle("-fx-background-color: " + color);
        }
    }

    private void updateMatrix(int index) {
        int x, y;

        for (int i = 0; i < ListOfShip.getInstance().getShips().get(index).getCoordinates().size(); i++) {
            x = ListOfShip.getInstance().getShips().get(index).getCoordinates().get(i).getX();
            y = ListOfShip.getInstance().getShips().get(index).getCoordinates().get(i).getY();

            matrix[x][y] = 0;
        }
    }

    private void removeShip(int row, int column) {
        int index = getIndexRemoveShip(row, column);

        if (index != -1) {
            --ship_count;
            Ship ship = ListOfShip.getInstance().getShips().get(index);
            incCount(ship.getDecks());
            removeAlgorithm();
            updateMatrix(index);
            ListOfShip.getInstance().getShips().remove(index);

            for (int i = 0; i < ListOfShip.getInstance().getShips().size(); i++) {
                coordinates = ListOfShip.getInstance().getShips().get(i).getCoordinates();
                orientation = ListOfShip.getInstance().getShips().get(i).getOrientation();
                updateField(ListOfShip.getInstance().getShips().get(i).getDecks());
                changeColor(ListOfShip.getInstance().getShips().get(i).getDecks());
                paintPoints(coordinates);
            }
            notReady();
        } else {
            l_info.setVisible(true);
            l_info.setText("Error: There is no ship");
        }
    }


    @FXML
    void removeshipClicked() {
        if (l_select_ship.isVisible() || (l_info.getText().equals("All ships on display"))) {
            l_select_ship.setVisible(true);
            remove_bool = true;
            l_info.setVisible(false);
            l_info.setText("Error: 0 ships of this type left");
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Can't delete");
            alert.setHeaderText("Problem with deleting");
            alert.setContentText("First of all you need to place all decks");

            alert.showAndWait();
        }
    }

    public void setConnect(Connect connect) {
        this.connect = connect;
    }

    public void randomClicked(ActionEvent event) {
        ObservableList<Node> childrens = field.getChildren();
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                Button button = (Button) childrens.get(i * 10 + j);
                button.setDisable(false);
                button.setStyle("");
            }
        }
        Map tempMap = new Map();
        tempMap.randomLoc();
        ListOfShip.getInstance().removeShips();
        matrix = tempMap.map;
        for (Map.Ship ship: tempMap.listships) {
            Ship newShip = new Ship();
            newShip.setHealth(ship.size);
            newShip.setDecks(ship.size);
            coordinates = new ArrayList<>();
            disable_points = new ArrayList<>();
            for (int[] p : ship.coords) {
                coordinates.add(new Point(p[0], p[1]));
            }

            newShip.setCoordinates(coordinates);
            newShip.setOrientation(ship.isVertically ? "vertical" : "horizontal");
            disable_points = findDisabledPoints(coordinates);
            newShip.setDisablePoints(disable_points);
            ListOfShip.getInstance().addShip(newShip);
        }
        count1ship.setText("0");
        count2ship.setText("0");
        count3ship.setText("0");
        count4ship.setText("0");
        ship_count = 10;
        placeShips();
    }

    private void placeShips() {
        ObservableList<Node> childrens = field.getChildren();
        for (int i = 0; i < ListOfShip.getInstance().getShips().size(); i++) {
            for (int j = 0; j < ListOfShip.getInstance().getShips().get(i).getDisablePoints().size(); j++) {
                int x, y;
                x = ListOfShip.getInstance().getShips().get(i).getDisablePoints().get(j).getX();
                y = ListOfShip.getInstance().getShips().get(i).getDisablePoints().get(j).getY();
                Button button = (Button) childrens.get(x * 10 + y);
                button.setDisable(true);
            }
            for (int j = 0; j < ListOfShip.getInstance().getShips().get(i).getCoordinates().size(); j++) {
                int x, y;
                x = ListOfShip.getInstance().getShips().get(i).getCoordinates().get(j).getX();
                y = ListOfShip.getInstance().getShips().get(i).getCoordinates().get(j).getY();
                String color = "";
                switch (ListOfShip.getInstance().getShips().get(i).getDecks()) {
                    case 1: {
                        color = "#a0bfe8;";
                        break;
                    }
                    case 2: {
                        color = "#2670cb;";
                        break;
                    }
                    case 3: {
                        color = "#033171;";
                        break;
                    }
                    case 4: {
                        color = "#1a2741;";
                        break;
                    }
                }
                Button button = (Button) childrens.get(x * 10 + y);
                button.setDisable(false);
                button.setStyle(new StringBuilder().append("-fx-background-color: ").append(color).toString());
            }
        }
    }

    private List<Point> findDisabledPoints(List<Point> coordinates) {
        List<Point> disablePoints = new ArrayList<>();

        for (Point point : coordinates) {
            int x = point.getX();
            int y = point.getY();

            for (int nx = x - 1; nx <= x + 1; nx++) {
                for (int ny = y - 1; ny <= y + 1; ny++) {
                    if (nx < 0 || ny < 0 || nx >= 10 || ny >= 10) {
                        continue;
                    }
                    if (!disablePoints.contains(new Point(nx, ny)) && !coordinates.contains(new Point(nx, ny)))
                        disablePoints.add(new Point(nx, ny));
                }
            }
        }

        return disablePoints;
    }
    private void disconnect() {
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
            myStage.close();
        } catch (IOException e) {
            System.out.println("MainController - " + e);
            e.printStackTrace();
        }
    }
}