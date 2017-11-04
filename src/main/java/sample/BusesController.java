package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import model.BusStopsEntity;
import model.BusesEntity;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.io.IOException;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;


public class BusesController implements Initializable{
    @FXML
    private AnchorPane busesPane;
    @FXML
    private TextField tfID;
    @FXML
    private TextField tfGovNumber;
    @FXML
    private TextField tfModel;
    @FXML
    private TextField tfCapacity;
    @FXML
    private Button btnAdd;
    @FXML
    private Button backButton;
    @FXML
    private TableView<BusesEntity> tableviewBuses;
    @FXML
    private TableColumn<BusesEntity, Integer> colID;
    @FXML
    private TableColumn<BusesEntity, String> colGovNumber;
    @FXML
    private TableColumn<BusesEntity, String> colModel;
    @FXML
    private TableColumn<BusesEntity, Integer> colCapacity;

    @Override
    public void initialize(URL location, ResourceBundle resourses)
    {
        colID.setCellValueFactory(new PropertyValueFactory<BusesEntity, Integer>("id"));
        colGovNumber.setCellValueFactory(new PropertyValueFactory<BusesEntity, String>("govNumber"));
        colModel.setCellValueFactory(new PropertyValueFactory<BusesEntity, String>("model"));
        colCapacity.setCellValueFactory(new PropertyValueFactory<BusesEntity, Integer>("capacity"));

        //Загрузка предыдущих записей
        SessionFactory sf = HibernateSessionFactory.getSessionFactory();
        Session session = sf.openSession();

        List<BusesEntity> list = (List<BusesEntity>) session.createQuery("from BusesEntity ").list();
        Iterator<BusesEntity> itr=list.iterator();
        while(itr.hasNext()) {
            BusesEntity q = itr.next();
            tableviewBuses.getItems().add(q);
        }
        session.close();

    }

    @FXML
    public void AddBus(ActionEvent actionEvent)
    {
        SessionFactory sf = HibernateSessionFactory.getSessionFactory();
        Session session = sf.openSession();

        int ID = Integer.parseInt(tfID.getText());
        String govNumber = tfGovNumber.getText();
        String model = tfModel.getText();
        int capacity = Integer.parseInt(tfCapacity.getText());

        BusesEntity buses = new BusesEntity();
        buses.setId(ID);
        buses.setGovNumber(govNumber);
        buses.setModel(model);
        buses.setCapacity(capacity);
        // Добавить в таблицу
        tableviewBuses.getItems().add(buses);
        //очистка полей формы и автоиндекс
        tfID.setText(String.valueOf(buses.getId()+1));
        tfGovNumber.setText("");
        tfModel.setText("");
        tfCapacity.setText("");
        // Добавить в базу данных
        session.beginTransaction();
        session.saveOrUpdate(buses);
        session.getTransaction().commit();
        session.close();
    }
    @FXML
    public void goBack(ActionEvent actionEvent) throws IOException
    {
        AnchorPane pane = FXMLLoader.load(getClass().getResource("/menu1.fxml"));
        busesPane.getChildren().setAll(pane);
    }

    @FXML
    public void DelBus(ActionEvent actionEvent)
    {
        //удалить из таблицы
        BusesEntity delObject = tableviewBuses.getSelectionModel().getSelectedItem();
        tableviewBuses.getItems().removeAll(tableviewBuses.getSelectionModel().getSelectedItem());
        //удалить из бд
        SessionFactory sf = HibernateSessionFactory.getSessionFactory();
        Session session = sf.openSession();
        session.beginTransaction();
        session.delete(delObject);
        session.getTransaction().commit();
        session.close();
    }

    @FXML
    public void selectRow(MouseEvent mouseEvent) {
        BusesEntity selObject = tableviewBuses.getSelectionModel().getSelectedItem();
        tfID.setText(String.valueOf(selObject.getId()));
        tfModel.setText(selObject.getModel());
        tfGovNumber.setText(selObject.getGovNumber());
        tfCapacity.setText(String.valueOf(selObject.getCapacity()));
    }
}
