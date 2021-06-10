package nextstep.subway.section.domain;

import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.Station;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import static javax.persistence.FetchType.LAZY;

@Entity
public class Section {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "line_id")
    private Line line;

    @OneToOne(fetch = LAZY)
    @JoinColumn(name = "prev_station_id")
    private Station prevStation;

    @OneToOne(fetch = LAZY)
    @JoinColumn(name = "next_station_id")
    private Station nextStation;

    private int distance;

    public void setId(Long id) {
        this.id = id;
    }

    public void setLine(Line line) {
        this.line = line;
    }

    public void setPrevStation(Station prevStation) {
        this.prevStation = prevStation;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public void setNextStation(Station nextStation) {
        this.nextStation = nextStation;
    }
}
