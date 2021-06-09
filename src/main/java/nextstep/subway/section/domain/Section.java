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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "line_id", foreignKey = @ForeignKey(name = "fk_section_to_line"))
    private Line line;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "station_id", foreignKey = @ForeignKey(name = "fk_section_to_station"))
    private Station station = new Station();

    protected Section() {
    }

    public Section(Station station) {
        this.station = station;
    }

    public Long getId() {
        return id;
    }

    public Station getStation() {
        return station;
    }

    public void setLine(Line line) {
        this.line = line;
    }

    public void setStation(Station station) {
        this.station = station;
    }
}
