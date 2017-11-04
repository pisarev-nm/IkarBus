package sample;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import model.BookingsEntity;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    @FXML
    ChoiceBox choiceTest;


@Override
public void initialize(URL location, ResourceBundle resourses)
{
    SessionFactory sf = HibernateSessionFactory.getSessionFactory();
    Session session = sf.openSession();

    List<BookingsEntity> list = (List<BookingsEntity>) session.createQuery("from BookingsEntity").list();
    Iterator<BookingsEntity> itr=list.iterator();
    while(itr.hasNext()) {
        BookingsEntity q = itr.next();
        choiceTest.getItems().add(q.getPassportNum());
    }
    session.close();
}

}
