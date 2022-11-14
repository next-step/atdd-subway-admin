package nextstep.subway.domain;

import org.springframework.util.StringUtils;

import javax.persistence.*;
import java.util.Objects;

@Entity
public class Line extends BaseEntity {
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

    @Column(nullable = false)
    private Long distance;

    protected Line() {
    }

    public Line(String name, String color, Long distance) {
        this.name = name;
        this.color = color;
        this.distance = distance;
    }

    public void setStations(Station upStation, Station downStation) {
        validateNotNullStation(upStation, downStation);
        validateStationId(upStation);
        validateStationId(downStation);
        validateNotSameStation(upStation,downStation);
        this.upStation = upStation;
        this.downStation = downStation;
    }

    private void validateNotSameStation(Station upStation, Station downStation) {
        if(upStation.equals(downStation)){
            throw new IllegalArgumentException(MESSAGE_STATION_SHOULD_BE_DIFFERENT);
        }
    }

    private void validateStationId(Station station) {
        if(!station.hasId()){
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
        return this.distance;
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
        return Objects.equals(id, line.id) && Objects.equals(name, line.name) && Objects.equals(color, line.color) && Objects.equals(upStation, line.upStation) && Objects.equals(downStation, line.downStation) && Objects.equals(distance, line.distance);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, color, upStation, downStation, distance);
    }
}
