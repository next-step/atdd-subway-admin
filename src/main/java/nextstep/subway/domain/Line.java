package nextstep.subway.domain;

import nextstep.subway.exception.InvalidSectionException;

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
        initStation(distance, upStation, downStation);
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

    public void initStation(int distance, Station upStation, Station downStation) {
        this.distance = new Distance(distance);
        sections.add(new Section(1, null, upStation, this));
        sections.add(new Section(distance, upStation, downStation, this));
        sections.add(new Section(1, downStation, null, this));
    }

    public void insertSection(Section section) {
        validateSection(section);

        if (isLineUpStation(section.getDownStation())) {
            sections.add(new Section(1, null, section.getUpStation(), this));
            Section lineUpSection = sections.getLineUpSection();
            lineUpSection.updateSection(section.getUpStation(), lineUpSection.getDownStation(), section.getDistance());
            addLineDistance(section.getDistance());
            return;
        }

        if (isLineDownStation(section.getUpStation())) {
            sections.add(new Section(1, section.getDownStation(), null, this));
            Section lineDownSection = sections.getLineDownSection();
            lineDownSection.updateSection(lineDownSection.getUpStation(), section.getDownStation(), section.getDistance());
            addLineDistance(section.getDistance());
            return;
        }

        Optional<Section> findUpStream = sections.findSectionWithUpStation(section.getUpStation());
        if (findUpStream.isPresent()) {
            Section find = findUpStream.get();
            insertSectionFromUpStation(section.getDownStation(), section.getDistance(), find);
            return;
        }

        Optional<Section> findDownStream = sections.findSectionWithDownStation(section.getDownStation());
        if (findDownStream.isPresent()) {
            Section find = findDownStream.get();
            insertSectionFromDownStation(section.getUpStation(), section.getDistance(), find);
        }
    }

    private void validateSection(Section section) {
        if (sections.containBothStation(section)) {
            throw new InvalidSectionException("이미 노선에 포함된 구간은 추가할 수 없습니다.");
        }

        if (sections.containNoneStation(section)) {
            throw new InvalidSectionException("구간 내 지하철 역이 하나는 등록된 상태여야 합니다.");
        }
    }

    private void insertSectionFromUpStation(Station insertDownStation, Integer distance, Section section) {
        int restDistance = section.getDistance() - distance;
        sections.add(new Section(restDistance, insertDownStation, section.getDownStation(), this));
        section.updateSection(section.getUpStation(), insertDownStation, distance);
    }

    private void insertSectionFromDownStation(Station insertUpStation, Integer distance, Section section) {
        int restDistance = section.getDistance() - distance;
        sections.add(new Section(distance, insertUpStation, section.getDownStation(), this));
        section.updateSection(section.getUpStation(), insertUpStation, restDistance);
    }

    public boolean isLineUpStation(Station station) {
        return getUpStation().equals(station);
    }

    public boolean isLineDownStation(Station station) {
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
        return sections.getLineUpStation();
    }

    public Station getDownStation() {
        return sections.getLineDownStation();
    }

    public Sections getSections() {
        return sections;
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

    @Override
    public String toString() {
        return "Line{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", color='" + color + '\'' +
                ", distance=" + distance +
                ", upStation=" + upStation +
                ", downStation=" + downStation +
                ", sections=" + sections +
                '}';
    }
}
