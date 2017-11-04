package model;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "tour", schema = "ikarbus", catalog = "")
public class TourEntity {
    private int id;
    private Timestamp tourDate;
    private RoutesEntity routesByRouteId;
    private BusesEntity busesByBusId;

    @Id
    @Column(name = "id", nullable = false)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Basic
    @Column(name = "tour_date", nullable = true)
    public Timestamp getTourDate() {
        return tourDate;
    }

    public void setTourDate(Timestamp tourDate) {
        this.tourDate = tourDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TourEntity that = (TourEntity) o;

        if (id != that.id) return false;
        if (tourDate != null ? !tourDate.equals(that.tourDate) : that.tourDate != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (tourDate != null ? tourDate.hashCode() : 0);
        return result;
    }

    @ManyToOne
    @JoinColumn(name = "route_id", referencedColumnName = "id")
    public RoutesEntity getRoutesByRouteId() {
        return routesByRouteId;
    }

    public void setRoutesByRouteId(RoutesEntity routesByRouteId) {
        this.routesByRouteId = routesByRouteId;
    }

    @ManyToOne
    @JoinColumn(name = "bus_id", referencedColumnName = "id")
    public BusesEntity getBusesByBusId() {
        return busesByBusId;
    }

    public void setBusesByBusId(BusesEntity busesByBusId) {
        this.busesByBusId = busesByBusId;
    }
}
