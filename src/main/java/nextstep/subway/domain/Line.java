package nextstep.subway.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import nextstep.subway.dto.LineRequest;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Entity
@Builder
@AllArgsConstructor
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

    @Embedded
    private Sections sections = new Sections();

    public Line() {
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

    public int getDistance() { return distance; }

    public List<Station> getStations() {
        return new ArrayList(Arrays.asList(upLastStation, downLastStation));
    }

    public void update(LineRequest updateRequest) {
        if(!updateRequest.getName().isEmpty() && updateRequest.getName() != "") {
            this.name = updateRequest.getName();
        }
        if(!updateRequest.getColor().isEmpty() && updateRequest.getColor() != ""){
            this.color = updateRequest.getColor();
        }
        if(updateRequest.getDistance() > 0) {
            this.distance = updateRequest.getDistance();
        }
    }


    public void changeUpStation(Station station) {
        this.upLastStation = station;
    }

    public void changeDownStation(Station station) {
        this.downLastStation = station;
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
