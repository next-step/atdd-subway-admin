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

        this.lineStations.add(LineStation.ascEndSection(this, upStation));
        this.lineStations.add(LineStation.section(this, downStation, upStation, distance));
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

    public List<LineStation> getAllSections() {
        return lineStations.stream().sorted().collect(Collectors.toList());
    }

    public LineStation getAscEndSection() {
        return getAllSections().stream().filter(it -> it.getPreStation() == null).findFirst()
                .orElseThrow(RuntimeException::new);
    }

    public LineStation getDescEndSection() {
        List<LineStation> sections = getAllSections();
        return sections.get(sections.size() - 1);
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
        checkSame(preStation, station);
        checkAlreadyAdded(preStation, station);

        if (isAscEnd(preStation)) {
            changeAscEnd(station, distance);
            return;
        }

        if (isDescEnd(preStation)) {
            changeDescEnd(station, distance);
            return;
        }

        addBetweenEndToEnd(preStation, station, distance);
    }

    private void checkSame(Station preStation, Station station) {
        if (Objects.equals(preStation, station)) {
            throw new IllegalArgumentException("구간은 서로 같은 역을 포함할 수 없습니다.");
        }
    }

    private void checkAlreadyAdded(Station preStation, Station station) {
        if (getAllSections().stream().anyMatch(section -> Objects.equals(section.getStation(), station) && Objects.equals(section.getPreStation(), preStation))) {
            throw new IllegalArgumentException("이미 존재하는 구간입니다.");
        }
    }

    private boolean isAscEnd(Station station) {
        return Objects.equals(station, null);
    }

    private boolean isDescEnd(Station station) {
        return Objects.equals(getDescEndSection().getStation(), station);
    }

    private void addBetweenEndToEnd(Station preStation, Station station, Integer distance) {
        LineStation startSection = getAllSections().stream().filter(it -> Objects.equals(preStation, it.getStation())).findFirst()
                .orElseThrow(IllegalAccessError::new);
        LineStation endSection = getAllSections().get(getAllSections().indexOf(startSection) + 1);

        if (endSection.getDistance() - startSection.getDistance() <= distance) {
            throw new IllegalArgumentException("distance 는 기존 역 구간 내에 속할 수 있는 값이어야 합니다.");
        }

        endSection.updatePreStation(station);
        this.lineStations.add(LineStation.section(this, station, preStation, startSection.getDistance() + distance));
    }

    private void changeAscEnd(Station station, Integer distance) {
        for (LineStation section : getAllSections()) {
            section.updateDuration(section.getDistance() + distance);
        }

        LineStation ascEndSection = getAscEndSection();
        ascEndSection.updatePreStation(station);

        this.lineStations.add(LineStation.ascEndSection(this, station));
    }

    private void changeDescEnd(Station station, Integer distance) {
        LineStation descEndSection = getDescEndSection();
        this.lineStations.add(LineStation.section(this, station, descEndSection.getStation(),
                descEndSection.getDistance() + distance));
    }
}
