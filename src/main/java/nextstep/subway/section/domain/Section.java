package nextstep.subway.section.domain;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.Station;

import javax.persistence.*;

@Entity
public class Section extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "distance")
    private int distance;

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
        this.distance = distance;
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


    public Long getId() {
        return id;
    }

    public Station getStation() {
        return station;
    }

    public Station getPreStation() {
        return preStation;
    }
}
