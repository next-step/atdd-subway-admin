package nextstep.subway.section.domain;

import java.util.Objects;
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

    public static final String UP_AND_DOWN_STATIONS_CANNOT_BE_THE_SAME = "구간의 상행역과 하행역은 같을 수 없습니다.";
    public static final String DISTANCE_MUST_BE_AT_LEAST_MIN_DISTANCE = "거리는 %d 이상이어야 합니다.";
    public static final String CANNOT_ADD_SECTION_GREATER_THAN_OR_EQUAL_DISTANCE = "기존 역 사이 길이보다 크거나 같은 구간은 추가할 수 없습니다.";
    public static final String UP_STATION_ID_CANNOT_BE_NULL = "상행역ID는 NULL이 될 수 없습니다.";
    public static final String DOWN_STATION_CANNOT_BE_NULL = "상행역ID는 NULL이 될 수 없습니다.";
    public static final int MIN_DISTANCE = 0;

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
        validationStations(upStationId, downStationId);
        validationDistance(distance);
        this.upStation = new Station(upStationId);
        this.downStation = new Station(downStationId);
        this.distance = distance;
    }

    public Section(Long lineId, Long upStationId, Long downStationId, int distance) {
        validationStations(upStationId, downStationId);
        validationDistance(distance);
        this.line = new Line(lineId);
        this.upStation = new Station(upStationId);
        this.downStation = new Station(downStationId);
        this.distance = distance;
    }

    protected Section() {

    }

    protected boolean isBefore(Section section) {
        return downStation.equals(section.getUpStation());
    }

    protected boolean isAfter(Section section) {
        return upStation.equals(section.getDownStation());
    }

    protected boolean isBetween(Section section) {
        if (isBaseOnUpStation(section)) {
            return true;
        }
        if (isBaseOnDownStation(section)) {
            return true;
        }
        return false;
    }

    private boolean isBaseOnUpStation(Section section) {
        if (!upStation.equals(section.getUpStation())) {
            return false;
        }
        if (downStation.equals(section.getDownStation())) {
            return false;
        }
        return true;
    }

    private boolean isBaseOnDownStation(Section section) {
        if (upStation.equals(section.getUpStation())) {
            return false;
        }
        if (!downStation.equals(section.getDownStation())) {
            return false;
        }
        return true;
    }

    protected boolean isEqualAllStation(Section section) {
        if (!upStation.equals(section.getUpStation())) {
            return false;
        }
        if (!downStation.equals(section.getDownStation())) {
            return false;
        }
        return true;
    }

    protected boolean isPresentAnyStation(Section section) {
        if (contain(section.getUpStation())) {
            return true;
        }
        if (contain(section.getDownStation())) {
            return true;
        }
        return false;
    }

    private boolean contain(Station station) {
        if (upStation.equals(station)) {
            return true;
        }
        if (downStation.equals(station)) {
            return true;
        }
        return false;
    }

    protected void reconnectStations(Section newSection) {
        minusDistance(newSection.distance);
        if (upStation.equals(newSection.getUpStation())) {
            updateDownStationTo(newSection);
            return;
        }
        if (downStation.equals(newSection.getDownStation())) {
            updateUpStationTo(newSection);
            return;
        }
        throw new IllegalArgumentException(Sections.THERE_IS_NO_STATION_INCLUDED_BETWEEN_UP_AND_DOWN_STATIONS);
    }

    private void updateDownStationTo(Section newSection) {
        Station preDownStation = new Station(downStation.getId());
        Station newStation = new Station(newSection.downStation.getId());
        downStation = newStation;
        newSection.upStation = newStation;
        newSection.downStation = preDownStation;
    }

    private void updateUpStationTo(Section newSection) {
        Station preUpStation = new Station(upStation.getId());
        Station newStation = new Station(newSection.upStation.getId());
        upStation = newStation;
        newSection.upStation = preUpStation;
        newSection.downStation = newStation;
    }

    private void minusDistance(int distance) {
        if (isShortEqualThan(distance)) {
            throw new IllegalArgumentException(CANNOT_ADD_SECTION_GREATER_THAN_OR_EQUAL_DISTANCE);
        }
        this.distance -= distance;
    }

    private boolean isShortEqualThan(int distance) {
        return this.distance <= distance;
    }

    private void validationDistance(int distance) {
        if (distance == MIN_DISTANCE) {
            throw new IllegalArgumentException(String.format(DISTANCE_MUST_BE_AT_LEAST_MIN_DISTANCE, MIN_DISTANCE));
        }
    }

    private void validationStations(Long upStationId, Long downStationId) {
        if (upStationId == null) {
            throw new IllegalArgumentException(UP_STATION_ID_CANNOT_BE_NULL);
        }
        if (downStationId == null) {
            throw new IllegalArgumentException(DOWN_STATION_CANNOT_BE_NULL);
        }
        if (upStationId.equals(downStationId)) {
            throw new IllegalArgumentException(UP_AND_DOWN_STATIONS_CANNOT_BE_THE_SAME);
        }
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Section section = (Section) o;
        return Objects.equals(id, section.id)
            && Objects.equals(line, section.line)
            && Objects.equals(upStation, section.upStation)
            && Objects.equals(downStation, section.downStation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, line, upStation, downStation);
    }

    @Override
    public String toString() {
        return "Section{" +
            "id=" + id +
            ", line=" + line +
            ", upStation.id=" + upStation.getId() +
            ", downStation.id=" + downStation.getId() +
            ", distance=" + distance +
            '}';
    }
}
