package nextstep.subway.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import nextstep.subway.dto.StationResponse;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Entity
@Builder
public class Line extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String name;

    @Column(nullable = false)
    private String color;

    @Column(nullable = false)
    private int distance;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "up_last_station_id", foreignKey = @ForeignKey(name = "fk_line_up_last_station_to_station"))
    private Station upLastStation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "down_last_station_id", foreignKey = @ForeignKey(name = "fk_line_down_last_station_to_station"))
    private Station downLastStation;

    public Line() {
    }

    public Line(Long id, String name, String color, int distance, Station upLastStation, Station downLastStation) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.distance = distance;
        this.upLastStation = upLastStation;
        this.downLastStation = downLastStation;
    }

    public Line(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getDistance() { return distance; }

    public List<Station> getStations() {
        return new ArrayList(Arrays.asList(upLastStation, downLastStation));
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Line line = (Line) o;
        return distance == line.distance && Objects.equals(id, line.id) && Objects.equals(name, line.name) && Objects.equals(color, line.color) && Objects.equals(upLastStation, line.upLastStation) && Objects.equals(downLastStation, line.downLastStation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, color, distance, upLastStation, downLastStation);
    }


    @Override
    public String toString() {
        return "Line{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", color='" + color + '\'' +
                ", distance=" + distance +
                ", upLastStation=" + upLastStation +
                ", downLastStation=" + downLastStation +
                '}';
    }



}
