package model;

import javax.persistence.*;

@Entity
@Table(name = "buses", schema = "ikarbus", catalog = "")
public class BusesEntity {
    private int id;
    private String govNumber;
    private String model;
    private int capacity;

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
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
    @Column(name = "gov_number", nullable = true, length = 10)
    public String getGovNumber() {
        return govNumber;
    }

    public void setGovNumber(String govNumber) {
        this.govNumber = govNumber;
    }

    @Basic
    @Column(name = "model", nullable = true, length = 30)
    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    @Basic
    @Column(name = "capacity", nullable = true)
    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BusesEntity that = (BusesEntity) o;

        if (id != that.id) return false;
        if (govNumber != null ? !govNumber.equals(that.govNumber) : that.govNumber != null) return false;
        if (model != null ? !model.equals(that.model) : that.model != null) return false;
        if (capacity != that.capacity) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (govNumber != null ? govNumber.hashCode() : 0);
        result = 31 * result + (model != null ? model.hashCode() : 0);
        result = capacity;
        return result;
    }
}
