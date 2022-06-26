package nextstep.subway.domain;

import java.util.List;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.LineStationResponse;
import nextstep.subway.dto.SectionRequest;
import org.hibernate.Hibernate;

@Entity
public class Line extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true, nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    private String color;

    @Embedded
    LineStations lineStations = new LineStations();

    public Line() {
    }

    public Line(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public Line(Long id, String name, String color, List<LineStationResponse> lineStations) {
        this.id = id;
        this.name = name;
        this.color = color;

        for (LineStationResponse lineStation : lineStations) {
            this.lineStations.add(lineStation.toLineStation());
        }
    }

    public Line(String name, String color, Station upStation, Station downStation, int distance) {
        this(name, color);
        LineStation lineStationUp =  new LineStation(upStation.getId(), -1, 0, this);
        LineStation lineStationDown =  new LineStation(downStation.getId(), upStation.getId(), distance, this);

        lineStations.add(lineStationUp);
        lineStations.add(lineStationDown);
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

    public LineStations getLineStations() {
        return lineStations;
    }

    public void setLineStations(LineStations lineStations) {
        this.lineStations = lineStations;
    }

    public void update(LineRequest lineRequest) {
        this.name = lineRequest.getName();
        this.color = lineRequest.getColor();
    }

    public void addLineStation(SectionRequest sectionRequest) {
        this.lineStations = LineStations.addSection(sectionRequest, this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
            return false;
        }
        Line line = (Line) o;
        return id != null && Objects.equals(id, line.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
