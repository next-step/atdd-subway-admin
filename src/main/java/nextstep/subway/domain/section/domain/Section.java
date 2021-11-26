package nextstep.subway.domain.section.domain;

import nextstep.subway.domain.line.domain.Line;
import nextstep.subway.domain.common.BaseEntity;
import nextstep.subway.domain.station.domain.Station;

import javax.persistence.*;

@Entity
public class Section extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private Distance distance;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "line_id")
    private Line line;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "pre_station_id")
    private Station preStation;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "station_id")
    private Station station;

    protected Section() {
    }

    public Section(final Station station) {
        this.station = station;
    }

    public Section(final Station preStation, final Station station, final int distance) {
        this.distance = new Distance(distance);
        this.preStation = preStation;
        this.station = station;
    }

    public static void createFirstSection(Station preStation, Station station, int distance, Line line) {
        final Section sectionStart = new Section(preStation);
        final Section sectionEnd = new Section(preStation, station, distance);
        sectionStart.addLine(line);
        sectionEnd.addLine(line);
    }

    private void addLine(Line line) {
        this.line = line;
        line.addSection(this);
    }

    public void changePreStation(Station station) {
        this.preStation = station;
    }

    public void changeStation(Station station) {
        this.station = station;
    }


    public Distance oldSectionDistance(Section section) {
        return this.distance.minus(section.getDistance());
    }

    public void changeDistinct(final Distance distance) {
        this.distance = distance;
    }

    public Long getId() {
        return id;
    }

    public Station getStation() {
        return station;
    }

    public Station getPreStation() {
        return preStation;
    }

    public Distance getDistance() {
        return distance;
    }
}
