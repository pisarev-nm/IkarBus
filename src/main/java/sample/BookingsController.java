package sample;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.qrcode.encoder.ByteMatrix;
import com.itextpdf.text.*;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.qrcode.BitMatrix;
import com.itextpdf.text.pdf.qrcode.ErrorCorrectionLevel;
import com.itextpdf.text.pdf.qrcode.QRCodeWriter;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.util.Callback;
import model.BookingsEntity;
import model.TourEntity;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import javax.imageio.ImageIO;
import javax.print.Doc;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;

public class BookingsController implements Initializable {
    @FXML private AnchorPane bookingsPane;
    @FXML private TextField tfID;
    @FXML private TextField tfFirstName;
    @FXML private TextField tfSecondName;
    @FXML private TextField tfPatron;
    @FXML private TextField tfPassportNumber;
    @FXML private ChoiceBox cbTour;
    @FXML private DatePicker dpBirthDate;
    @FXML private Button printButton;
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
        //невидимая кнопка печати
        printButton.setVisible(false);
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
        //видимая кнопка печати
        printButton.setVisible(true);
        BookingsEntity selObject = tableviewBookings.getSelectionModel().getSelectedItem();
        tfID.setText(String.valueOf(selObject.getId()));
        tfFirstName.setText(selObject.getFirstName());
        tfSecondName.setText(selObject.getSecondName());
        tfPatron.setText(selObject.getPatron());
        tfPassportNumber.setText(selObject.getPassportNum());
        dpBirthDate.setValue(selObject.getBirthDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        cbTour.setValue(selObject.getTourByTourId().getId() + ". " + selObject.getTourByTourId().getTourDate() + "  " + selObject.getTourByTourId().getRoutesByRouteId().getBusStopsByDestination().getCity() + " - " + selObject.getTourByTourId().getRoutesByRouteId().getBusStopsByArrival().getCity());
    }



//создание pdf файла
    @FXML
    public void printTicket(ActionEvent actionEvent)
    {
        try {
            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream("C:/Users/Nikolay/IdeaProjects/IkarBus/ticket.pdf"));
            document.open();
            addPage(document);
            document.close();
            //Открыть созданный билет
            Process p = Runtime
                    .getRuntime()
                    .exec("rundll32 url.dll,FileProtocolHandler C:\\Users\\Nikolay\\IdeaProjects\\IkarBus\\ticket.pdf");
            p.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
//создание страницы билета
    public void addPage(Document document) throws DocumentException, IOException, WriterException {
        Paragraph preface = new Paragraph();
        //Подключение кирилицы
        BaseFont tmr = BaseFont.createFont("/times.ttf", BaseFont.IDENTITY_H, true);
        BaseFont tmrB = BaseFont.createFont("/timesbd.ttf", BaseFont.IDENTITY_H, true);
        BaseFont tmrI = BaseFont.createFont("/timesi.ttf", BaseFont.IDENTITY_H, true);

        // Заголовок
        //preface.add(new Paragraph("ИкарБус - IkarBus", new Font(tmrB, 30)));
        //Заголовок-логотип
        Image logo = Image.getInstance("C:/Users/Nikolay/IdeaProjects/IkarBus/logo.jpg");
        preface.add(logo);
        // Добавление пустой строки
        addEmptyLine(preface, 1);
        //выбранный в таблице элемент
        BookingsEntity printObject = tableviewBookings.getSelectionModel().getSelectedItem();
        //добавление строки (параграфа)
        preface.add(new Paragraph("Билет №: " + printObject.getId() + "", new Font(tmr, 14)));
        addEmptyLine(preface, 1);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        preface.add(new Paragraph("Дата поездки: " + printObject.getTourByTourId().getTourDate().toLocalDateTime().toLocalDate().format(formatter) + " " + printObject.getTourByTourId().getTourDate().toLocalDateTime().toLocalTime(), new Font(tmr, 18)));
        addEmptyLine(preface, 1);
        preface.add(new Paragraph("Из: " + printObject.getTourByTourId().getRoutesByRouteId().getBusStopsByDestination().getCity() + "", new Font(tmr, 18)));
        preface.add(new Paragraph("В: " + printObject.getTourByTourId().getRoutesByRouteId().getBusStopsByArrival().getCity() + "", new Font(tmr, 18)));
        addEmptyLine(preface, 1);
        preface.add(new Paragraph("Пассажир: " + printObject.getSecondName() + " " + printObject.getFirstName() + " " + printObject.getPatron() + ", рожд. " + new SimpleDateFormat("dd.MM.yyyy").format(printObject.getBirthDate()), new Font(tmr, 18)));
        preface.add(new Paragraph("Номер паспорта: " + printObject.getPassportNum(), new Font(tmr, 18)));
        addEmptyLine(preface, 4);
        Image qr = Image.getInstance(generateQR(printObject));
        qr.setAlignment(Element.ALIGN_CENTER);
        preface.add(qr);
        addEmptyLine(preface, 6);
        preface.add(new Paragraph("" + new Date(), new Font(tmrI, 12)));

        document.add(preface);
        // Start a new page
        document.newPage();
    }
    //пустая строка в пдф файле
    public void addEmptyLine(Paragraph paragraph, int number) {
        for (int i = 0; i < number; i++) {
            paragraph.add(new Paragraph(" "));
        }
    }
    //создание куар кода для билета
    public String generateQR(BookingsEntity obj) throws WriterException, IOException {
        String filename = "C:/Users/Nikolay/IdeaProjects/IkarBus/qr.jpg";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        String content = "Билет №" + obj.getId() + ".   " + obj.getTourByTourId().getTourDate().toLocalDateTime().toLocalDate().format(formatter) + ".   <<" + obj.getTourByTourId().getRoutesByRouteId().getBusStopsByDestination().getCity() + " - " + obj.getTourByTourId().getRoutesByRouteId().getBusStopsByArrival().getCity() + ">>   Пассажир: " + obj.getSecondName() + " " + obj.getFirstName() + " " + obj.getPatron() + ", рожд." + new SimpleDateFormat("dd.MM.yyyy").format(obj.getBirthDate()) + ",   паспорт №" + obj.getPassportNum();
        Map<EncodeHintType, Object> hints = new HashMap<EncodeHintType, Object>();
        hints.put(EncodeHintType.CHARACTER_SET, "windows-1251");
        com.google.zxing.common.BitMatrix matrix = new MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, 250, 250, hints);
        MatrixToImageWriter.writeToFile(matrix, filename.substring(filename.lastIndexOf('.')+1), new File(filename));
        return filename;
    }

}
