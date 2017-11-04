package sample;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.util.Callback;
import model.BusStopsEntity;
import model.BusesEntity;
import model.RoutesEntity;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.io.IOException;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;

public class RoutesController implements Initializable {

    @FXML
    private AnchorPane routesPane;
    @FXML
    private Button backButton;
    @FXML
    private Button btnAdd;
    @FXML
    private TextField tfID;
    @FXML
    private TextField tfCost;
    @FXML
    private ChoiceBox cbDestination;
    @FXML
    private ChoiceBox cbArrival;
    @FXML
    private TableView<RoutesEntity> tableviewRoutes;
    @FXML
    private TableColumn<RoutesEntity, Integer> colID;
    @FXML
    private TableColumn<RoutesEntity, String> colDestination;
    @FXML
    private TableColumn<RoutesEntity, String> colArrival;
    @FXML
    private TableColumn<RoutesEntity, Integer> colCost;

    @Override
    public void initialize(URL location, ResourceBundle resourses)
    {
        colID.setCellValueFactory(new PropertyValueFactory<RoutesEntity, Integer>("id"));
        colDestination.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<RoutesEntity, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<RoutesEntity, String> param) {
                return new SimpleObjectProperty<>(param.getValue().getBusStopsByDestination().getCity());
            };
        });
        colArrival.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<RoutesEntity, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<RoutesEntity, String> param) {
                return new SimpleObjectProperty<>(param.getValue().getBusStopsByArrival().getCity());
            }
        });
        colCost.setCellValueFactory(new PropertyValueFactory<RoutesEntity, Integer>("cost"));

        //Загрузка предыдущих записей в тейблвью
        SessionFactory sf = HibernateSessionFactory.getSessionFactory();
        Session session = sf.openSession();

        List<RoutesEntity> list = (List<RoutesEntity>) session.createQuery("from RoutesEntity").list();
        Iterator<RoutesEntity> itr=list.iterator();
        while(itr.hasNext()) {
            RoutesEntity q = itr.next();
            tableviewRoutes.getItems().add(q);
        }
        //заполнение чойсбоксов
        List<BusStopsEntity> list1 = (List<BusStopsEntity>) session.createQuery("from BusStopsEntity").list();
        Iterator<BusStopsEntity> itr1 = list1.iterator();
        while (itr1.hasNext())
        {
            BusStopsEntity q = itr1.next();
            cbDestination.getItems().add(q.getCity());
            cbArrival.getItems().add(q.getCity());
        }
        session.close();
    }

    @FXML
    public void AddRoute(ActionEvent actionEvent)
    {
        SessionFactory sf = HibernateSessionFactory.getSessionFactory();
        Session session = sf.openSession();

        int ID = Integer.parseInt(tfID.getText());
        String destination = cbDestination.getValue().toString();
        String arrival = cbArrival.getValue().toString();
        int cost = Integer.parseInt(tfCost.getText());

        RoutesEntity route = new RoutesEntity();
        BusStopsEntity busStops1 = new BusStopsEntity();
        BusStopsEntity busStops2 = new BusStopsEntity();
        route.setId(ID);
        setStopsId(busStops1, destination, session);
        setStopsId(busStops2, arrival, session);
        route.setBusStopsByDestination(busStops1);
        route.setBusStopsByArrival(busStops2);
        route.setCost(cost);
        //добавить в таблицу
        tableviewRoutes.getItems().add(route);
        //очистка полей формы и автоиндекс
        tfID.setText(String.valueOf(route.getId()+1));
        tfCost.setText("");
        //добавить в базу данных
        session.beginTransaction();
        session.saveOrUpdate(route);
        session.getTransaction().commit();
        session.close();


    }
    @FXML
    public void goBack(ActionEvent actionEvent) throws IOException
    {
        AnchorPane pane = FXMLLoader.load(getClass().getResource("/menu1.fxml"));
        routesPane.getChildren().setAll(pane);
    }

    public void setStopsId (BusStopsEntity bse, String stp, Session session)
    {
        List<BusStopsEntity> list = (List<BusStopsEntity>) session.createQuery("from BusStopsEntity ").list();
        Iterator<BusStopsEntity> itr=list.iterator();
        while(itr.hasNext()) {
            BusStopsEntity q = itr.next();
            if (q.getCity().equals(stp))
            {
                bse.setId(q.getId());
                bse.setCity(q.getCity());
            }
        }
    }

    @FXML
    public void DelRoute(ActionEvent actionEvent)
    {
        //удалить из таблицы
        RoutesEntity delObject = tableviewRoutes.getSelectionModel().getSelectedItem();
        tableviewRoutes.getItems().removeAll(tableviewRoutes.getSelectionModel().getSelectedItem());
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
        RoutesEntity selObject = tableviewRoutes.getSelectionModel().getSelectedItem();
        tfID.setText(String.valueOf(selObject.getId()));
        tfCost.setText(String.valueOf(selObject.getCost()));
        cbDestination.setValue(selObject.getBusStopsByDestination().getCity());
        cbArrival.setValue(selObject.getBusStopsByArrival().getCity());
    }
}
