package model;

import javax.persistence.*;

@Entity
@Table(name = "routes", schema = "ikarbus", catalog = "")
public class RoutesEntity {
    private int id;
    private Integer cost;
    private BusStopsEntity busStopsByDestination;
    private BusStopsEntity busStopsByArrival;

    public void setCost(int cost) {
        this.cost = cost;
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
    @Column(name = "cost", nullable = false)
    public Integer getCost() {
        return cost;
    }

    public void setCost(Integer cost) {
        this.cost = cost;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RoutesEntity that = (RoutesEntity) o;

        if (id != that.id) return false;
        if (cost != null ? !cost.equals(that.cost) : that.cost != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (cost != null ? cost.hashCode() : 0);
        return result;
    }

    @ManyToOne
    @JoinColumns({@JoinColumn(name = "destination", referencedColumnName = "id", nullable = false)})
    public BusStopsEntity getBusStopsByDestination() {
        return busStopsByDestination;
    }

    public void setBusStopsByDestination(BusStopsEntity busStopsByDestination) {
        this.busStopsByDestination = busStopsByDestination;
    }

    @ManyToOne
    @JoinColumns({@JoinColumn(name = "arrival", referencedColumnName = "id", nullable = false)})
    public BusStopsEntity getBusStopsByArrival() {
        return busStopsByArrival;
    }

    public void setBusStopsByArrival(BusStopsEntity busStopsByArrival) {
        this.busStopsByArrival = busStopsByArrival;
    }
}
