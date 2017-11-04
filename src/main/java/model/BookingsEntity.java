package model;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Date;

@Entity
@Table(name = "bookings", schema = "ikarbus", catalog = "")
public class BookingsEntity {
    private int id;
    private String firstName;
    private String secondName;
    private String patron;
    private Date birthDate;
    private String passportNum;
    private TourEntity tourByTourId;

    public void setBirthDate(Timestamp birthDate) {
        this.birthDate = birthDate;
    }

    @Id
    @Column(name = "id", nullable = false)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Basic
    @Column(name = "first_name", nullable = true, length = 45)
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @Basic
    @Column(name = "second_name", nullable = true, length = 45)
    public String getSecondName() {
        return secondName;
    }

    public void setSecondName(String secondName) {
        this.secondName = secondName;
    }

    @Basic
    @Column(name = "patron", nullable = true, length = 45)
    public String getPatron() {
        return patron;
    }

    public void setPatron(String patron) {
        this.patron = patron;
    }

    @Basic
    @Column(name = "birth_date", nullable = true)
    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    @Basic
    @Column(name = "passport_num", nullable = true, length = 45)
    public String getPassportNum() {
        return passportNum;
    }

    public void setPassportNum(String passportNum) {
        this.passportNum = passportNum;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BookingsEntity that = (BookingsEntity) o;

        if (id != that.id) return false;
        if (firstName != null ? !firstName.equals(that.firstName) : that.firstName != null) return false;
        if (secondName != null ? !secondName.equals(that.secondName) : that.secondName != null) return false;
        if (patron != null ? !patron.equals(that.patron) : that.patron != null) return false;
        if (birthDate != null ? !birthDate.equals(that.birthDate) : that.birthDate != null) return false;
        if (passportNum != null ? !passportNum.equals(that.passportNum) : that.passportNum != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (firstName != null ? firstName.hashCode() : 0);
        result = 31 * result + (secondName != null ? secondName.hashCode() : 0);
        result = 31 * result + (patron != null ? patron.hashCode() : 0);
        result = 31 * result + (birthDate != null ? birthDate.hashCode() : 0);
        result = 31 * result + (passportNum != null ? passportNum.hashCode() : 0);
        return result;
    }

    @ManyToOne
    @JoinColumn(name = "tour_id", referencedColumnName = "id", nullable = false)
    public TourEntity getTourByTourId() {
        return tourByTourId;
    }

    public void setTourByTourId(TourEntity tourByTourId) {
        this.tourByTourId = tourByTourId;
    }
}
