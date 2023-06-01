package com.example.coursework;

import javafx.application.Platform;
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
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {
    @FXML
    public TextField nameField;

    @FXML
    public TextField ipField;

    @FXML
    public TextField portField;

    @FXML
    private Label waitingLabel;

    @FXML
    public Button connectBtn;

    private Connect connect;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        waitingLabel.setVisible(false);
        ipField.setText("127.0.0.1");
        portField.setText("5050");
    }

    private void loadWindow(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("CreateWindow.fxml"));
            loader.setControllerFactory(controllerClass -> {
                CreateController controller = new CreateController();
                controller.setConnect(connect);
                controller.setName(nameField.getText());
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
            stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.close();
        } catch (IOException e) {
            System.out.println("MainController - " + e);
            e.printStackTrace();
        }
    }

    public void onCancel(MouseEvent event) {
        if (connect != null) {
            try {
                connect.send(new Message(Message.MessageType.DISCONNECT, null));
                connect.close();
            } catch (IOException e) {

            }
        }
        waitingLabel.setVisible(false);
        connectBtn.setVisible(true);
    }

    public void onConnect(ActionEvent event) {
        if (!nameField.getText().isEmpty()) {
            try {
                Socket socket = new Socket();
                socket.connect(new InetSocketAddress(ipField.getText(), Integer.parseInt(portField.getText())), 1000);
                waitingLabel.setVisible(true);
                connectBtn.setVisible(false);
                connect = new Connect(socket);
                new Thread(() -> {
                    try {
                        Message message;
                        do {
                            message = connect.receive();
                            if (message.getType() == Message.MessageType.CONNECT) {
                                Platform.runLater(() -> {
                                    loadWindow(event);
                                });
                            }
                        } while (message.getType() != Message.MessageType.CONNECT);
                    } catch (NumberFormatException e) {
                        Platform.runLater(() -> {
                            new Alert(Alert.AlertType.ERROR, "Check server settings!").showAndWait();
                        });
                    } catch (IOException e) {

                    } catch (ClassNotFoundException e) {

                    }

                }).start();
            } catch (Exception e) {

            }
        }
    }
}