package nextstep.subway.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class Section {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private Station startStation;

    @ManyToOne
    private Station endStation;

    private Long distance;

    protected Section(){

    }

    public Section(Station startStation, Station endStation, Long distance){
        this.startStation=startStation;
        this.endStation=endStation;
        this.distance=distance;
    }

    public Long getId() {
        return id;
    }

    public Station getStartStation() {
        return startStation;
    }

    public Station getEndStation() {
        return endStation;
    }

    public Long getDistance() {
        return distance;
    }
}
