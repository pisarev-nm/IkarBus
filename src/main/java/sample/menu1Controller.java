package sample;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class menu1Controller implements Initializable{
    @FXML
    private Button addBusStop;
    @FXML
    private Button addBus;
    @FXML
    private Button addRoute;
    @FXML
    private Button addTour;
    @FXML
    private AnchorPane menu1pane;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }
    @FXML
    public void goToBusStops(ActionEvent actionEvent) throws IOException{
        AnchorPane pane = FXMLLoader.load(getClass().getResource("/busStops.fxml"));
        menu1pane.getChildren().setAll(pane);
    }

    @FXML
    public void goToBuses(ActionEvent actionEvent) throws IOException{
        AnchorPane pane = FXMLLoader.load(getClass().getResource("/buses.fxml"));
        menu1pane.getChildren().setAll(pane);
    }

    @FXML
    public void goToRoutes(ActionEvent actionEvent) throws IOException{
        AnchorPane pane = FXMLLoader.load(getClass().getResource("/routes.fxml"));
        menu1pane.getChildren().setAll(pane);
    }

    @FXML
    public void goToToures(ActionEvent actionEvent) throws IOException{
        AnchorPane pane = FXMLLoader.load(getClass().getResource("/toures.fxml"));
        menu1pane.getChildren().setAll(pane);
    }
    @FXML
    public void goToBookings(ActionEvent actionEvent) throws IOException{
        AnchorPane pane = FXMLLoader.load(getClass().getResource("/bookings.fxml"));
        menu1pane.getChildren().setAll(pane);
    }
}
