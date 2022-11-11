package nextstep.subway.domain;

import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import nextstep.subway.dto.LineResponse;

@Entity
public class Line extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String name;
    @Column
    private String color;
    @Column(name = "up_station_id")
    private Long upStationId;
    @Column(name = "down_station_id")
    private Long downStationId;
    @Column
    private int distance;

    protected Line() {
    }

    public Line(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public Line(String name, String color, Long upStationId, Long downStationId, int distance) {
        this.name = name;
        this.color = color;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public void updateLine(Line line) {
        this.name = line.name;
        this.color = line.color;
    }

    public LineResponse toLineResponse() {
        return new LineResponse(id, name, color);
    }

    public Long getUpStationId() {
        return upStationId;
    }

    public Long getDownStationId() {
        return downStationId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Line line = (Line) o;
        return distance == line.distance && Objects.equals(name, line.name) && Objects.equals(color,
                line.color) && Objects.equals(upStationId, line.upStationId) && Objects.equals(
                downStationId, line.downStationId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, color, upStationId, downStationId, distance);
    }
}
