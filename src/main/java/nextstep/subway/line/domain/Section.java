package nextstep.subway.line.domain;

import nextstep.subway.station.domain.Station;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.util.Arrays;
import java.util.List;

@Entity
public class Section {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "lineId")
    @ManyToOne
    Line line;

    @JoinColumn(name = "upStationId")
    @ManyToOne
    Station upStation;

    @JoinColumn(name = "downStationId")
    @ManyToOne
    Station downStation;

    private int distance;

    protected Section() {
    }

    public Section(Station upStation, Station downStation, int distance) {
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public void setLine(Line line) {
        this.line = line;
    }

    public List<Station> upAndDownStations() {
        return Arrays.asList(upStation, downStation);
    }

}
