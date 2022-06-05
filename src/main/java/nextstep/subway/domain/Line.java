package nextstep.subway.domain;

import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@DynamicUpdate
@Entity
@Table(name = "line")
public class Line extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String name;
    @Column(unique = true)
    private String color;
    @OneToMany(mappedBy = "line", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LineStation> lineStations = new ArrayList<>();

    protected Line() {
    }

    public Line(String name, String color, Integer distance, Station upStation, Station downStation) {
        this.name = name;
        this.color = color;

        this.lineStations.add(new LineStation(this, upStation, null, 0));
        this.lineStations.add(new LineStation(this, downStation, upStation, distance));
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

    public List<LineStation> getLineStations() {
        return lineStations.stream().sorted().collect(Collectors.toList());
    }

    public LineStation getAscEnd() {
        return getLineStations().stream().filter(it -> it.getPreStation() == null).findFirst().orElseThrow(RuntimeException::new);
    }

    public LineStation getDescEnd() {
        return getLineStations().get(getLineStations().size() - 1);
    }

    public void update(String name, String color) {
        this.name = name;
        this.color = color;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Line)) {
            return false;
        }
        Line line = (Line) o;
        return getId().equals(line.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    public void addSection(Station preStation, Station station, Integer distance) {
        if (isAscEnd(preStation)) {
            addAscEnd(station, distance);
            return;
        }

        if (isDescEnd(preStation)) {
            addDescEnd(preStation, station, distance);
            return;
        }

        addBetweenEndToEnd(preStation, station, distance);
    }

    private void addDescEnd(Station preStation, Station station, Integer distance) {
        LineStation descEnd = getDescEnd();
        this.lineStations.add(new LineStation(this, station, preStation, descEnd.getDistance() + distance));
    }

    private boolean isAscEnd(Station preStation) {
        return preStation == null;
    }

    private boolean isDescEnd(Station preStation) {
        return getDescEnd().getStation() == preStation;
    }

    private void addBetweenEndToEnd(Station preStation, Station station, Integer distance) {
        LineStation upLineStation =
                getLineStations().stream().filter(it -> it.getStation() == preStation).findFirst().orElseThrow(IllegalAccessError::new);
        LineStation downLineStation = getLineStations().get(getLineStations().indexOf(upLineStation) + 1);

        if (downLineStation.getDistance() - upLineStation.getDistance() <= distance)
            throw new IllegalArgumentException("distance 는 기존 역 구간 내에 속할 수 있는 값이어야 합니다.");

        downLineStation.updatePreStation(station);
        this.lineStations.add(new LineStation(this, station, preStation, upLineStation.getDistance() + distance));
    }

    private void addAscEnd(Station station, Integer distance) {
        getLineStations().forEach(lineStation -> lineStation.updateDuration(lineStation.getDistance() + distance));

        LineStation preAscEnd = getAscEnd();
        preAscEnd.updatePreStation(station);

        this.lineStations.add(new LineStation(this, station, null, 0));
    }
}
