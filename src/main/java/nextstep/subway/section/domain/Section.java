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

    @Enumerated(value = EnumType.STRING)
    private SectionStatus status;

    @JoinColumn(name = "station_id")
    @OneToOne(fetch = FetchType.LAZY)
    private Station station;

    @JoinColumn(name = "line_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Line line;

    public Section() {
    }

    public Section(SectionStatus status, Station station, Line line) {
        this.status = status;
        this.station = station;
        this.line = line;
    }

    public Long getId() {
        return id;
    }

    public SectionStatus getStatus() {
        return status;
    }

    public Station getStation() {
        return station;
    }

    public Line getLine() {
        return line;
    }
}
