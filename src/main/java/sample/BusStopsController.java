package sample;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import model.BusStopsEntity;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import javafx.event.ActionEvent;

import java.io.IOException;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;

public class BusStopsController implements Initializable {
    @FXML
    private AnchorPane busStopsPane;
    @FXML
    private TextField tfID;
    @FXML
    private TextField tfCity;
    @FXML
    private Button btnAdd;
    @FXML
    private Button backButton;
    @FXML
    private Button btnDel;
    @FXML
    private TableView<BusStopsEntity> tableviewCity;
    @FXML
    private TableColumn<BusStopsEntity, Integer> colID;
    @FXML
    private TableColumn<BusStopsEntity, String> colCity;

    @Override
    public void initialize(URL location, ResourceBundle resourses)
    {
        colID.setCellValueFactory(new PropertyValueFactory<BusStopsEntity, Integer>("id"));
        colCity.setCellValueFactory(new PropertyValueFactory<BusStopsEntity, String>("city"));

        //Загрузка предыдущих записей
        SessionFactory sf = HibernateSessionFactory.getSessionFactory();
        Session session = sf.openSession();

        int last=0;

        List<BusStopsEntity> list = (List<BusStopsEntity>) session.createQuery("from BusStopsEntity ").list();
        Iterator<BusStopsEntity> itr=list.iterator();
        while(itr.hasNext()) {
            BusStopsEntity q = itr.next();
            tableviewCity.getItems().add(q);
            last = q.getId();
        }

        //автоматический id в текстфилде
        tfID.setText(String.valueOf(last+1));

        session.close();

    }


    @FXML
    public void AddCity(ActionEvent actionEvent) {

        SessionFactory sf = HibernateSessionFactory.getSessionFactory();
        Session session = sf.openSession();

        int ID = Integer.parseInt(tfID.getText());
        String city = tfCity.getText();

        BusStopsEntity busStops = new BusStopsEntity();
        busStops.setId(ID);
        busStops.setCity(city);

        // Добавить в таблицу
        tableviewCity.getItems().add(busStops);

        //очистка полей формы и автоиндекс
        tfID.setText(String.valueOf(busStops.getId()+1));
        tfCity.setText("");

        // Добавить в базу данных
        session.beginTransaction();
        session.saveOrUpdate(busStops);
        session.getTransaction().commit();
        session.close();

        //
    }
    @FXML
    public void goBack(ActionEvent actionEvent) throws IOException{
        AnchorPane pane = FXMLLoader.load(getClass().getResource("/menu1.fxml"));
        busStopsPane.getChildren().setAll(pane);
    }

    @FXML
    public void DelCity(ActionEvent actionEvent)
    {
        //удалить из таблицы
        BusStopsEntity delObject = tableviewCity.getSelectionModel().getSelectedItem();
        tableviewCity.getItems().removeAll(tableviewCity.getSelectionModel().getSelectedItem());
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
        BusStopsEntity selObject = tableviewCity.getSelectionModel().getSelectedItem();
        tfID.setText(String.valueOf(selObject.getId()));
        tfCity.setText(selObject.getCity());
    }
}
