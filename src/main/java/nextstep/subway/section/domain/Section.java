package nextstep.subway.section.domain;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.Station;

@Entity
public class Section extends BaseEntity {
    private static final String CAN_NOT_STATION_CREATE = "상행선과 하행선 동일한 역을 등록할 수 없습니다.";
    private static final String EXISTS_SAME_SECTION = "추가할 구간과 동일한 구간이 존재합니다.";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "up_station_id", foreignKey = @ForeignKey(name = "fk_section_up_station"))
    private Station upStation;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "down_station_id", foreignKey = @ForeignKey(name = "fk_section_down_station"))
    private Station downStation;

    @Column(nullable = false)
    @Embedded
    private Distance distance;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "line_id", foreignKey = @ForeignKey(name = "fk_section_line"))
    private Line line;

    public Section() {
    }

    public Section(Station upStation, Station downStation, int distance) {
        this(null, upStation, downStation, distance);
    }

    public Section(Long id, Station upStation, Station downStation, int distance) {
        validate(upStation, downStation);

        this.id = id;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = Distance.of(distance);
    }

    private void validate(Station upStation, Station downStation)  {
        if (upStation.equals(downStation)) {
            throw new IllegalArgumentException(CAN_NOT_STATION_CREATE);
        }
    }

    public void updateSection(Section newSection) {
        distance.updateDiffDistance(newSection.getDistance());
        if (upStation.isSame(newSection.upStation)) {
            upStation = newSection.downStation;
            return;
        }
        downStation = newSection.upStation;
    }

    public boolean isContainSection(Section section) {
        if (upStation.isSame(section.upStation) && downStation.isSame(section.downStation)) {
            throw new IllegalArgumentException(EXISTS_SAME_SECTION);
        }
        return upStation.isSame(section.upStation) || downStation.isSame(section.downStation);
    }

    public void changeLine(Line line) {
        this.line = line;
    }

    public List<Station> toStations() {
        return new LinkedList<>(Arrays.asList(upStation, downStation));
    }

    public Station getUpStation() {
        return upStation;
    }

    public Station getDownStation() {
        return downStation;
    }

    public int getDistance() {
        return distance.toNumber();
    }

    public Line getLine() {
        return line;
    }
}
