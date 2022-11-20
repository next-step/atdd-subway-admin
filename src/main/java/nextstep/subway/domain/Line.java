package nextstep.subway.domain;

import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
public class Line extends BaseEntity {
    private static final String MESSAGE_NEW_SECTION_IS_SAME_WITH_LINE = "노선 끝과 동일한 상/하행역을 가진 구간은 등록할 수 없습니다";
    public static final String MESSAGE_STATION_SHOULD_NOT_EMPTY = "지하철역은 비어있을 수 없습니다";
    public static final String MESSAGE_STATION_SHOULD_HAS_ID = "지하철역은 식별자가 있어야 합니다.";
    public static final String MESSAGE_STATION_SHOULD_BE_DIFFERENT = "상/하행역은 같을 수 없습니다";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false, unique = true)
    private String color;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "up_station_id", nullable = false)
    private Station upStation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "down_station_id", nullable = false)
    private Station downStation;

    @Embedded
    private Distance distance;

    @Embedded
    private Sections sections = new Sections();

    protected Line() {
    }

    public Line(String name, String color, Long distance) {
        this.name = name;
        this.color = color;
        this.distance = Distance.of(distance);
    }

    public void setStations(Station upStation, Station downStation) {
        validateNotNullStation(upStation, downStation);
        validateStationId(upStation);
        validateStationId(downStation);
        validateNotSameStation(upStation, downStation);
        this.upStation = upStation;
        this.downStation = downStation;
    }

    private void validateNotSameStation(Station upStation, Station downStation) {
        if (upStation.equals(downStation)) {
            throw new IllegalArgumentException(MESSAGE_STATION_SHOULD_BE_DIFFERENT);
        }
    }

    private void validateStationId(Station station) {
        if (!station.hasId()) {
            throw new IllegalArgumentException(MESSAGE_STATION_SHOULD_HAS_ID);
        }
    }

    private static void validateNotNullStation(Station upStation, Station downStation) {
        if (Objects.isNull(upStation) || Objects.isNull(downStation)) {
            throw new IllegalArgumentException(MESSAGE_STATION_SHOULD_NOT_EMPTY);
        }
    }

    public Station getUpStation() {
        return this.upStation;
    }

    public Station getDownStation() {
        return this.downStation;
    }

    public Long getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public String getColor() {
        return this.color;
    }

    public Long getDistance() {
        return this.distance.get();
    }

    public void modifyLine(String name, String color) {
        if (StringUtils.hasText(name) && !this.name.equals(name)) {
            this.name = name;
        }
        if (StringUtils.hasText(color) && !this.color.equals(color)) {
            this.color = color;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Line line = (Line) o;
        return Objects.equals(id, line.id) && Objects.equals(name, line.name) && Objects.equals(color, line.color) && Objects.equals(upStation, line.upStation) && Objects.equals(downStation, line.downStation) && Objects.equals(distance, line.distance) && Objects.equals(sections, line.sections);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, color, upStation, downStation, distance, sections);
    }

    public void addSection(Section newSection) {
        this.sections.add(newSection);
    }

    public boolean extendFromUpStation(Station upStation, Station downStation, Long distance) {
        if (!this.upStation.equals(downStation)) {
            return false;
        }
        this.upStation = upStation;
        this.distance = this.distance.plus(distance) ;
        this.sections.add(Section.of(this, upStation, downStation, distance));
        return true;
    }

    public boolean extendFromDownStation(Station upStation, Station downStation, Long distance) {
        if (!this.downStation.equals(upStation)) {
            return false;
        }
        this.downStation = downStation;
        this.distance = this.distance.plus(distance);
        this.sections.add(Section.of(this, upStation, downStation, distance));
        return true;
    }

    public boolean hasSameEndStations(Station upStation, Station downStation) {
        if(this.upStation.equals(upStation) && this.downStation.equals(downStation)){
            return true;
        }
        if(this.upStation.equals(downStation) && this.downStation.equals(upStation)){
            return true;
        }
        return false;
    }

    public boolean extend(Station upStation, Station downStation, Long distance) {
        checkRequestSectionIsLineEndStations(upStation, downStation);
        return extendFromUpStation(upStation, downStation, distance) ||
                extendFromDownStation(upStation, downStation, distance);
    }

    private void checkRequestSectionIsLineEndStations(Station upStation, Station downStation) {
        boolean hasSame = hasSameEndStations(upStation, downStation);
        if (hasSame) {
            throw new IllegalArgumentException(MESSAGE_NEW_SECTION_IS_SAME_WITH_LINE);
        }
    }

    public boolean insert(Station upStation, Station downStation, Long distance) {
        return this.sections.insert(upStation,downStation,distance);
    }
}
