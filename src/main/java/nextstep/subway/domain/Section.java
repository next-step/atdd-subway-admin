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
    private Station upStation;

    @ManyToOne
    private Station downStation;

    private Long distance;

    private int sectionOrder;

    protected Section(){

    }

    public Section(Station upStation, Station downStation, Long distance){
        this.upStation =upStation;
        this.downStation = downStation;
        this.distance=distance;
    }

    public Long getId() {
        return id;
    }

    public Station getUpStation() {
        return upStation;
    }

    public Station getDownStation() {
        return downStation;
    }

    public Long getDistance() {
        return distance;
    }

    void setSectionOrder(int sectionOrder) {
        this.sectionOrder = sectionOrder;
    }
}
