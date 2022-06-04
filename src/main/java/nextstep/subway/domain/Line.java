package nextstep.subway.domain;

import nextstep.subway.exception.InvalidSectionException;

import javax.persistence.*;
import java.util.Objects;

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
        initStation(new Distance(distance), upStation, downStation);
    }

    public void updateLine(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public void initStation(Distance distance, Station upStation, Station downStation) {
        sections.addSection(new Section(new Distance(1), null, upStation, this));
        sections.addSection(new Section(distance, upStation, downStation, this));
        sections.addSection(new Section(new Distance(1), downStation, null, this));
    }

    public void insertSection(Section section) {
        validateSection(section);
        sections.insertSection(this, section);
    }

    private void validateSection(Section section) {
        if (sections.containBothStation(section)) {
            throw new InvalidSectionException("이미 노선에 포함된 구간은 추가할 수 없습니다.");
        }

        if (sections.containNoneStation(section)) {
            throw new InvalidSectionException("구간 내 지하철 역이 하나는 등록된 상태여야 합니다.");
        }
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
