package nextstep.subway.section.domain;

import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.Station;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.util.Objects;

@Entity
public class Section {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "line_id")
    private Line line;

    @ManyToOne
    @JoinColumn(name = "station_id")
    private Station station;

    private int distance;

    public Section() {
    }

    public Section(Long id, Line line, Station station, int distance) {
        this.id = id;
        changeLine(line);
        changeStation(station);
        this.distance = distance;
    }

    public void changeLine(Line line) {
        if (!Objects.isNull(this.line)) {
            this.line.getSections().remove(this);
        }

        this.line = line;
        line.getSections().add(this);
    }

    public void changeStation(Station station) {
        if (!Objects.isNull(this.station)) {
            this.station.getSections().remove(this);
        }

        this.station = station;
        station.getSections().add(this);
    }

    public Long getId() {
        return id;
    }

    public Line getLine() {
        return line;
    }

    public Station getStation() {
        return station;
    }
}
