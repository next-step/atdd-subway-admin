package nextstep.subway.section.domain;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import nextstep.subway.common.BaseEntity;
import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.Station;

@Entity
@Table(
    name = "section",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_section",
            columnNames = {"line_id", "up_station_id", "down_station_id"}
        )
    }
)
public class Section extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "line_id", foreignKey = @ForeignKey(name = "fk_section_to_line"))
    private Line line;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "up_station_id", foreignKey = @ForeignKey(name = "fk_section_upstation"))
    private Station upStation;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "down_station_id", foreignKey = @ForeignKey(name = "fk_section_downstation"))
    private Station downStation;

    private int distance;

    public Section(Long upStationId, Long downStationId, int distance) {
        this.upStation = new Station(upStationId);
        this.downStation = new Station(downStationId);;
        this.distance = distance;
    }

    public Section(Station upStation, Station downStation, int distance) {
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    protected Section() {

    }

    public Long getId() {
        return id;
    }

    public Line getLine() {
        return line;
    }

    public void toLine(Line line) {
        this.line = line;
    }

    public Station getUpStation() {
        return upStation;
    }

    public Station getDownStation() {
        return downStation;
    }

    public int getDistance() {
        return distance;
    }

    public SectionResponse toSectionResponse() {
        return SectionResponse.of(this);
    }

}
