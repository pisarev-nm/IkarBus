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
import model.BookingsEntity;
import model.TourEntity;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.io.IOException;
import java.net.URL;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;

public class BookingsController implements Initializable {
    @FXML private AnchorPane bookingsPane;
    @FXML private TextField tfID;
    @FXML private TextField tfFirstName;
    @FXML private TextField tfSecondName;
    @FXML private TextField tfPatron;
    @FXML private TextField tfPassportNumber;
    @FXML private ChoiceBox cbTour;
    @FXML private DatePicker dpBirthDate;
    @FXML private TableView<BookingsEntity> tableviewBookings;
    @FXML private TableColumn<BookingsEntity, Integer> colID;
    @FXML private TableColumn<BookingsEntity, String> colTour;
    @FXML private TableColumn<BookingsEntity, String> colSecondName;
    @FXML private TableColumn<BookingsEntity, String> colFirstName;
    @FXML private TableColumn<BookingsEntity, String> colPatron;
    @FXML private TableColumn<BookingsEntity, String> colBirthDate;
    @FXML private TableColumn<BookingsEntity, String> colPassportNumber;

    @Override
    public void initialize(URL location, ResourceBundle resourses)
    {
        colID.setCellValueFactory(new PropertyValueFactory<BookingsEntity, Integer>("id"));
        colTour.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<BookingsEntity, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<BookingsEntity, String> param) {
                return new SimpleObjectProperty<>(param.getValue().getTourByTourId().getId() + ". " + param.getValue().getTourByTourId().getTourDate() + "  " + param.getValue().getTourByTourId().getRoutesByRouteId().getBusStopsByDestination().getCity() + " - " + param.getValue().getTourByTourId().getRoutesByRouteId().getBusStopsByArrival().getCity());
            };
        });
        colSecondName.setCellValueFactory(new PropertyValueFactory<BookingsEntity, String>("secondName"));
        colFirstName.setCellValueFactory(new PropertyValueFactory<BookingsEntity, String>("firstName"));
        colPatron.setCellValueFactory(new PropertyValueFactory<BookingsEntity, String>("patron"));
        colBirthDate.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<BookingsEntity, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<BookingsEntity, String> param) {
                return new SimpleObjectProperty<>(String.valueOf(param.getValue().getBirthDate()));
            };
        });
        colPassportNumber.setCellValueFactory(new PropertyValueFactory<BookingsEntity, String>("passportNum"));

        //Загрузка предыдущих записей в тейблвью
        SessionFactory sf = HibernateSessionFactory.getSessionFactory();
        Session session = sf.openSession();

        List<BookingsEntity> list = (List<BookingsEntity>) session.createQuery("from BookingsEntity ").list();
        Iterator<BookingsEntity> itr=list.iterator();
        while(itr.hasNext()) {
            BookingsEntity q = itr.next();
            tableviewBookings.getItems().add(q);
        }

        //заполнение чойсбокса
        List<TourEntity> list1 = (List<TourEntity>) session.createQuery("from TourEntity ").list();
        Iterator<TourEntity> itr1=list1.iterator();
        while(itr1.hasNext()) {
            TourEntity q1 = itr1.next();
            cbTour.getItems().add(q1.getId() + ". " + q1.getTourDate() + "  " + q1.getRoutesByRouteId().getBusStopsByDestination().getCity() + " - " + q1.getRoutesByRouteId().getBusStopsByArrival().getCity());
        }
        session.close();
    }

    @FXML
    public void AddBooking(ActionEvent actionEvent) throws ParseException {
        SessionFactory sf = HibernateSessionFactory.getSessionFactory();
        Session session = sf.openSession();

        int ID = Integer.parseInt(tfID.getText());
        String firstName = tfFirstName.getText();
        String secondName = tfSecondName.getText();
        String patron = tfPatron.getText();
        String passportNumber = tfPassportNumber.getText();
        String tour = cbTour.getValue().toString();

        //работа с датой
        String parsedDateS;
        parsedDateS = dpBirthDate.getValue().toString() + " 00:00:00";
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date parsedDate = dateFormat.parse(parsedDateS);
        Timestamp dateBook = new Timestamp(parsedDate.getTime());

        TourEntity tourD = new TourEntity();
        BookingsEntity bookingD = new BookingsEntity();

        //присваимаем бук айди
        bookingD.setId(ID);
        //присваиваем тур айдт
        setTourId1(tourD, tour, session);
        bookingD.setTourByTourId(tourD);
        //присваеваем дату рождения поездки
        bookingD.setBirthDate(dateBook);
        //присваиваем имя, фамилию, отчество и номер паспорта
        bookingD.setFirstName(firstName);
        bookingD.setSecondName(secondName);
        bookingD.setPatron(patron);
        bookingD.setPassportNum(passportNumber);
        //добавляем в таблицу
        tableviewBookings.getItems().add(bookingD);
        //очистка полей формы и автоиндекс
        tfID.setText(String.valueOf(bookingD.getId()+1));
        dpBirthDate.setValue(LocalDate.now());
        tfFirstName.setText("");
        tfSecondName.setText("");
        tfPatron.setText("");
        tfPassportNumber.setText("");
        //добавляем в базу данных
        session.beginTransaction();
        session.saveOrUpdate(bookingD);
        session.getTransaction().commit();
        session.close();

    }

    @FXML
    public void goBack(ActionEvent actionEvent) throws IOException
    {
        AnchorPane pane = FXMLLoader.load(getClass().getResource("/menu1.fxml"));
        bookingsPane.getChildren().setAll(pane);
    }

    public void setTourId1 (TourEntity te, String stp, Session session)
    {
        List<TourEntity> list = (List<TourEntity>) session.createQuery("from TourEntity ").list();
        Iterator<TourEntity> itr=list.iterator();
        while(itr.hasNext()) {
            TourEntity q = itr.next();
            if ((q.getId() + ". " + q.getTourDate() + "  " + q.getRoutesByRouteId().getBusStopsByDestination().getCity() + " - " + q.getRoutesByRouteId().getBusStopsByArrival().getCity()).equals(stp));
            {
                te.setId(q.getId());
                te.setTourDate(q.getTourDate());
                te.setRoutesByRouteId(q.getRoutesByRouteId());
                te.setBusesByBusId(q.getBusesByBusId());
            }
        }
    }

    @FXML
    public void DelBooking(ActionEvent actionEvent)
    {
        //удалить из таблицы
        BookingsEntity delObject = tableviewBookings.getSelectionModel().getSelectedItem();
        tableviewBookings.getItems().removeAll(tableviewBookings.getSelectionModel().getSelectedItem());
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
        BookingsEntity selObject = tableviewBookings.getSelectionModel().getSelectedItem();
        tfID.setText(String.valueOf(selObject.getId()));
        tfFirstName.setText(selObject.getFirstName());
        tfSecondName.setText(selObject.getSecondName());
        tfPatron.setText(selObject.getPatron());
        tfPassportNumber.setText(selObject.getPassportNum());
        dpBirthDate.setValue(selObject.getBirthDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        cbTour.setValue(selObject.getTourByTourId().getId() + ". " + selObject.getTourByTourId().getTourDate() + "  " + selObject.getTourByTourId().getRoutesByRouteId().getBusStopsByDestination().getCity() + " - " + selObject.getTourByTourId().getRoutesByRouteId().getBusStopsByArrival().getCity());
    }

}
