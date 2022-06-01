package nextstep.subway.domain;

import javax.persistence.*;
import java.util.Objects;
import java.util.Optional;

@Entity
public class Line extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String name;
    @Column
    private String color;
    @Embedded
    private Distance distance;

    @OneToOne
    @JoinColumn(name = "up_station_id")
    private Station upStation;

    @OneToOne
    @JoinColumn(name = "down_station_id")
    private Station downStation;

    @Embedded
    private Sections sections = new Sections();

    @Embedded
    private LineStations lineStations = new LineStations();

    protected Line() {
    }

    public Line(String name, String color, int distance) {
        this.name = name;
        this.color = color;
        this.distance = new Distance(distance);
    }

    public Line(String name, String color, int distance, Station upStation, Station downStation) {
        this.name = name;
        this.color = color;
        this.distance = new Distance(distance);
        this.upStation = upStation;
        this.downStation = downStation;
    }

    public Line setUpStation(Station upStation) {
        this.upStation = upStation;
        return this;
    }

    public Line setDownStation(Station downStation) {
        this.downStation = downStation;
        return this;
    }

    public void updateLine(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public void initStation(Station upStation, Station downStation) {
        setUpStation(upStation);
        setDownStation(downStation);
        sections.add(new Section(getDistance(), upStation, downStation, this));
        lineStations.add(new LineStation(upStation, this));
        lineStations.add(new LineStation(downStation, this));
    }

    public void insertSection(Station insertUpStation, Station insertDownStation, Integer distance) {
        if (isSameUpStation(insertDownStation)) {
            insertSectionToHead(new Section(distance, insertUpStation, getUpStation(), this));
            return;
        }

        if (isSameDownStation(insertUpStation)) {
            insertSectionToTail(new Section(distance, getDownStation(), insertDownStation, this));
            return;
        }

        Optional<Section> findUpStream = sections.findSectionWithUpStation(insertUpStation);
        if (findUpStream.isPresent()) {
            Section section = findUpStream.get();
            insertSectionFromUpStation(insertDownStation, distance, section);
            return;
        }

        Optional<Section> findDownStream = sections.findSectionWithDownStation(insertDownStation);
        if (findDownStream.isPresent()) {
            Section section = findDownStream.get();
            insertSectionFromDownStation(insertUpStation, distance, section);
            return;
        }
    }

    private void insertSectionFromUpStation(Station insertDownStation, Integer distance, Section section) {
        int restDistance = section.getDistance() - distance;
        sections.add(new Section(restDistance, insertDownStation, section.getDownStation(), this));
        section.updateSection(section.getUpStation(), insertDownStation, distance);
        lineStations.add(new LineStation(insertDownStation, this));
    }

    private void insertSectionFromDownStation(Station insertUpStation, Integer distance, Section section) {
        int restDistance = section.getDistance() - distance;
        sections.add(new Section(distance, insertUpStation, section.getDownStation(), this));
        section.updateSection(section.getUpStation(), insertUpStation, restDistance);
        lineStations.add(new LineStation(insertUpStation, this));
    }

    public void insertSectionToHead(Section section) {
        sections.add(section);
        setUpStation(section.getUpStation());
        addLineDistance(section.getDistance());
        lineStations.add(new LineStation(section.getUpStation(), section.getLine()));
    }

    public void insertSectionToTail(Section section) {
        sections.add(section);
        setDownStation(section.getDownStation());
        addLineDistance(section.getDistance());
        lineStations.add(new LineStation(section.getDownStation(), section.getLine()));
    }

    public boolean isSameUpStation(Station station) {
        return getUpStation().equals(station);
    }

    public boolean isSameDownStation(Station station) {
        return getDownStation().equals(station);
    }

    private void addLineDistance(Integer distance) {
        this.distance = new Distance(getDistance() + distance);
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

    public Integer getDistance() {
        return distance.getDistance();
    }

    public Station getUpStation() {
        return upStation;
    }

    public Station getDownStation() {
        return downStation;
    }

    public Sections getSections() {
        return sections;
    }

    public LineStations getLineStations() {
        return lineStations;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Line line = (Line) o;
        return Objects.equals(name, line.name) &&
                Objects.equals(color, line.color);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, color);
    }
}
