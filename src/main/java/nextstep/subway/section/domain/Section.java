package nextstep.subway.section.domain;

import nextstep.subway.line.domain.Line;
import nextstep.subway.section.dto.SectionResponse;
import nextstep.subway.station.domain.Station;

import javax.persistence.*;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Entity
public class Section {
    private static final String UP_DOWN_STATION_NOT_NULL = "상/하행역은 빈값일 수 없습니다.";
    private static final String UP_DOWN_STATION_NOT_EQUALS = "상/하행역은 동일할 수 없습니다.";
    private static final String DISTANCE_NOT_NULL = "지하철 구간 길이는 빈값일 수 없습니다.";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "up_station_id", nullable = false, foreignKey = @ForeignKey(name = "fk_section_to_up_station"))
    private Station upStation;

    @OneToOne
    @JoinColumn(name = "down_station_id", nullable = false, foreignKey = @ForeignKey(name = "fk_section_to_down_station"))
    private Station downStation;

    @Embedded
    private Distance distance;

    @ManyToOne
    @JoinColumn(name = "line_id", nullable = false, foreignKey = @ForeignKey(name = "fk_section_to_line"))
    private Line line;

    protected Section() {}

    public Section(Station upStation, Station downStation, Distance distance) {
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
        validate();
    }

    public Section(Station upStation, Station downStation, int distance) {
        this(upStation, downStation, Distance.from(distance));
    }

    private void validate() {
        if (Objects.isNull(this.upStation) || Objects.isNull(this.downStation)) {
            throw new IllegalArgumentException(UP_DOWN_STATION_NOT_NULL);
        }

        if (this.upStation.equals(this.downStation)) {
            throw new IllegalArgumentException(UP_DOWN_STATION_NOT_EQUALS);
        }

        if (Objects.isNull(this.distance)) {
            throw new IllegalArgumentException(DISTANCE_NOT_NULL);
        }
    }

    public void assignLine(Line line) {
        this.line = line;
    }

    public SectionResponse toSectionResponse() {
        return SectionResponse.from(this);
    }

    public List<Station> getStations() {
        return Arrays.asList(this.upStation, this.downStation);
    }

    public boolean isEqualsUpStation(Station station) {
        return this.upStation.equals(station);
    }

    public boolean isEqualsDownStation(Station station) {
        return this.downStation.equals(station);
    }

    public void updateUpStation(Station station) {
        this.upStation = station;
    }

    public void updateDownStation(Station station) {
        this.downStation = station;
    }

    public void decreaseDistance(Distance distance) {
        this.distance.decrease(distance);
    }

    public Station getUpStation() {
        return this.upStation;
    }

    public Station getDownStation() {
        return this.downStation;
    }

    public Distance getDistance() {
        return this.distance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Section section = (Section) o;
        return Objects.equals(id, section.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
