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
    private final Sections sections = new Sections();

    protected Line() {
    }

    public Line(String name, String color, int distance, Station upStation, Station downStation) {
        this.name = name;
        this.color = color;
        initStation(distance, upStation, downStation);
    }

    public void updateLine(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public void initStation(int distance, Station upStation, Station downStation) {
        sections.add(new Section(1, null, upStation, this));
        sections.add(new Section(distance, upStation, downStation, this));
        sections.add(new Section(1, downStation, null, this));
    }

    public void insertSection(Section section) {
        validateSection(section);

        if (sections.isLineUpStation(section.getDownStation())) {
            sections.insertToLineHead(this, section);
            return;
        }

        if (sections.isLineDownStation(section.getUpStation())) {
            sections.insertToLineTail(this, section);
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
                ", sections=" + sections +
                '}';
    }
}
