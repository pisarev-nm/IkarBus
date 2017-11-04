package sample;

import javafx.beans.property.SimpleObjectProperty;
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
import model.BusesEntity;
import model.RoutesEntity;
import model.TourEntity;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.io.IOException;
import java.net.URL;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class TouresController implements Initializable{

    @FXML private AnchorPane touresPane;
    @FXML private TextField tfID;
    @FXML private ChoiceBox cbRoute;
    @FXML private ChoiceBox cbBus;
    @FXML private Button btnAdd;
    @FXML private Button backButton;
    @FXML private DatePicker dpData;
    @FXML private ChoiceBox cbHour;
    @FXML private ChoiceBox cbMinute;
    @FXML private TableView<TourEntity> tableviewToures;
    @FXML private TableColumn<TourEntity, Integer> colID;
    @FXML private TableColumn<TourEntity, String> colRoute;
    @FXML private TableColumn<TourEntity, String> colData;
    @FXML private TableColumn<TourEntity, String> colBus;

    @Override
    public void initialize(URL location, ResourceBundle resourses)
    {
        colID.setCellValueFactory(new PropertyValueFactory<TourEntity, Integer>("id"));
        colRoute.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<TourEntity, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<TourEntity, String> param) {
                return new SimpleObjectProperty<>((param.getValue().getRoutesByRouteId().getBusStopsByDestination().getCity() + " - " + param.getValue().getRoutesByRouteId().getBusStopsByArrival().getCity()));
            };
        });
        colData.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<TourEntity, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<TourEntity, String> param) {
                return new SimpleObjectProperty<>(String.valueOf(param.getValue().getTourDate()));
            }
        });
        colBus.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<TourEntity, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<TourEntity, String> param) {
                return new SimpleObjectProperty<>(param.getValue().getBusesByBusId().getModel() + "  " + param.getValue().getBusesByBusId().getGovNumber());
            }
        });

        //Загрузка предыдущих записей в тейблвью
        SessionFactory sf = HibernateSessionFactory.getSessionFactory();
        Session session = sf.openSession();

        List<TourEntity> list = (List<TourEntity>) session.createQuery("from TourEntity ").list();
        Iterator<TourEntity> itr=list.iterator();
        while(itr.hasNext()) {
            TourEntity q = itr.next();
            tableviewToures.getItems().add(q);
        }
        //заполнение чойсбоксов
        List<RoutesEntity> list1 = (List<RoutesEntity>) session.createQuery("from RoutesEntity").list();
        Iterator<RoutesEntity> itr1=list1.iterator();
        while(itr1.hasNext()) {
            RoutesEntity q1 = itr1.next();
            cbRoute.getItems().add(q1.getBusStopsByDestination().getCity() + " - " + q1.getBusStopsByArrival().getCity());
        }

        List<BusesEntity> list2 = (List<BusesEntity>) session.createQuery("from BusesEntity").list();
        Iterator<BusesEntity> itr2=list2.iterator();
        while(itr2.hasNext()) {
            BusesEntity q2 = itr2.next();
            cbBus.getItems().add(q2.getModel() + "  " + q2.getGovNumber());
        }

        cbHour.getItems().addAll(0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23);
        cbMinute.getItems().addAll(0, 15, 30, 45);

        session.close();
    }

    @FXML
    public void AddTour(ActionEvent actionEvent) throws ParseException {
        SessionFactory sf = HibernateSessionFactory.getSessionFactory();
        Session session = sf.openSession();

        int ID = Integer.parseInt(tfID.getText());
        String route = cbRoute.getValue().toString();
        String bus = cbBus.getValue().toString();

        //работа с датой
        String parsedDateS;
        parsedDateS = dpData.getValue().toString() + " " + cbHour.getValue().toString() + ":" + cbMinute.getValue().toString() + ":00";
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date parsedDate = dateFormat.parse(parsedDateS);
        Timestamp dateTour = new Timestamp(parsedDate.getTime());

        TourEntity tourD = new TourEntity();
        RoutesEntity routeD = new RoutesEntity();
        BusesEntity busD = new BusesEntity();

        //присваемаем тур айди
        tourD.setId(ID);
        //присваиваем бас айдт
        setBusId1(busD, bus, session);
        tourD.setBusesByBusId(busD);
        //присваиваем рут айди
        setRouteId1(routeD, route, session);
        tourD.setRoutesByRouteId(routeD);
        //присваеваем дату-время поездки
        tourD.setTourDate(dateTour);

        //System.out.print(tourD.getId() + " " + tourD.getRoutesByRouteId().getId() + "  " + tourD.getBusesByBusId().getId() + tourD.getTourDate());

        //добавить в таблицу
        tableviewToures.getItems().add(tourD);
        //очистка полей формы и автоиндекс
        tfID.setText(String.valueOf(tourD.getId()+1));
        dpData.setValue(LocalDate.now());
        cbHour.setValue(0);
        cbMinute.setValue(0);
        //добавить в базу данных
        session.beginTransaction();
        session.saveOrUpdate(tourD);
        session.getTransaction().commit();
        session.close();
    }

    @FXML
    public void goBack(ActionEvent actionEvent) throws IOException
    {
        AnchorPane pane = FXMLLoader.load(getClass().getResource("/menu1.fxml"));
        touresPane.getChildren().setAll(pane);
    }

    public void setBusId1 (BusesEntity be, String stp, Session session)
    {
        List<BusesEntity> list = (List<BusesEntity>) session.createQuery("from BusesEntity ").list();
        Iterator<BusesEntity> itr=list.iterator();
        while(itr.hasNext()) {
            BusesEntity q = itr.next();
            if ((q.getModel() + "  " + q.getGovNumber()).equals(stp))
            {
                be.setId(q.getId());
                be.setModel(q.getModel());
                be.setGovNumber(q.getGovNumber());
                be.setCapacity(q.getCapacity());
            }
        }
    }

    public void setRouteId1 (RoutesEntity re, String stp, Session session)
    {
        List<RoutesEntity> list = (List<RoutesEntity>) session.createQuery("from RoutesEntity ").list();
        Iterator<RoutesEntity> itr=list.iterator();
        while(itr.hasNext()) {
            RoutesEntity q = itr.next();
            if ((q.getBusStopsByDestination().getCity() + " - " + q.getBusStopsByArrival().getCity()).equals(stp))
            {
                re.setId(q.getId());
                re.setCost(q.getCost());
                re.setBusStopsByDestination(q.getBusStopsByDestination());
                re.setBusStopsByArrival(q.getBusStopsByArrival());
            }
        }
    }

    @FXML
    public void DelTour(ActionEvent actionEvent)
    {
        //удалить из таблицы
        TourEntity delObject = tableviewToures.getSelectionModel().getSelectedItem();
        tableviewToures.getItems().removeAll(tableviewToures.getSelectionModel().getSelectedItem());
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
        TourEntity selObject = tableviewToures.getSelectionModel().getSelectedItem();
        tfID.setText(String.valueOf(selObject.getId()));
        cbBus.setValue(selObject.getBusesByBusId().getModel() + "  " + selObject.getBusesByBusId().getGovNumber());
        cbRoute.setValue(selObject.getRoutesByRouteId().getBusStopsByDestination().getCity() + " - " + selObject.getRoutesByRouteId().getBusStopsByArrival().getCity());
        dpData.setValue(selObject.getTourDate().toLocalDateTime().toLocalDate());
        cbHour.setValue(selObject.getTourDate().toLocalDateTime().getHour());
        cbMinute.setValue(selObject.getTourDate().toLocalDateTime().getMinute());
    }
}
