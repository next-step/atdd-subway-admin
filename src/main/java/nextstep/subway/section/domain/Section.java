package nextstep.subway.section.domain;

import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.Station;

import javax.persistence.*;

@Entity
public class Section {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "distance")
    private int distance;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "line_id")
    private Line line;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "station_id")
    private Station station;

    @OneToOne
    @JoinColumn(name = "parent_id")
    private Section parentStation;

    @OneToOne(mappedBy = "parentStation")
    private Section childStation;

    protected Section() {
    }

    private Section(final Station station, final Line line) {
        this.station = station;
        addLine(line);
    }

    private Section(final Station station, final int distance, final Section parentStation, final Line line) {
        this.station = station;
        this.distance = distance;
        this.parentStation = parentStation;
        addLine(line);
    }

    private void addLine(Line line) {
        this.line = line;
        line.addSection(this);
    }

    public static Section createUpLastStopStation(final Station station, final Line line) {
        return new Section(station, line);
    }

    public static Section createDownLastStopStation(final Station station, final int distance, final Section parent, final Line line) {
        return new Section(station, distance, parent, line);
    }
}
