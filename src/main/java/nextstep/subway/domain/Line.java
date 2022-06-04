package nextstep.subway.domain;

import nextstep.subway.dto.LineRequest;

import javax.persistence.*;
import java.util.Objects;

@Entity
public class Line extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    private String color;

    @ManyToOne
    @JoinColumn(name = "up_station_id")
    private Station upStation;

    @ManyToOne
    @JoinColumn(name = "down_station_id")
    private Station downStation;

    @Column(nullable = false)
    private int distance;

    protected Line() {
    }

    private Line(String name, String color, Station upStation, Station downStation, Integer distance) {
        validateDistance(distance);
        validateUpDownStations(upStation, downStation);

        this.name = name;
        this.color = color;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    private void validateDistance(Integer distance) {
        if (distance <= 0) {
            throw new IllegalArgumentException("지하철 노선의 거리는 양수만 입력해 주세요.");
        }
    }

    private void validateUpDownStations(Station upStation, Station downStation) {
        if (upStation.equals(downStation)) {
            throw new IllegalArgumentException("상행종점역과 하행종점역은 같을 수 없습니다.");
        }
    }

    public static Line of(String name, String color, Station upStation, Station downStation, Integer distance) {
        return new Line(name, color, upStation, downStation, distance);
    }

    public void update(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public Station getUpStation() {
        return upStation;
    }

    public Station getDownStation() {
        return downStation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Line line = (Line) o;
        return Objects.equals(distance, line.distance)
                && Objects.equals(id, line.id)
                && Objects.equals(name, line.name)
                && Objects.equals(color, line.color)
                && Objects.equals(upStation, line.upStation)
                && Objects.equals(downStation, line.downStation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, color, upStation, downStation, distance);
    }
}
