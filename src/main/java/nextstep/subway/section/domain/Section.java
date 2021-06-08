package nextstep.subway.section.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import nextstep.subway.common.BaseEntity;
import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.Station;
import javax.persistence.*;

@Entity
@Getter @NoArgsConstructor
@Table(uniqueConstraints = @UniqueConstraint(name = "unique_section_station_info", columnNames={"line_id", "up_station_id", "down_station_id"}))
public class Section extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "line_id", foreignKey = @ForeignKey(name = "fk_section_to_line"))
    private Line line;

    @OneToOne
    @JoinColumn(name = "up_station_id", foreignKey = @ForeignKey(name = "fk_section_to_up_station"))
    private Station upStation;

    @OneToOne
    @JoinColumn(name = "down_station_id", foreignKey = @ForeignKey(name = "fk_section_to_down_station"))
    private Station downStation;

    private int distance;

    public void registerStation(final Station upStation, final Station downStation, final int distance) {
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public void registerLine(Line line) {
        this.line = line;

        line.appendSection(this);
    }
}