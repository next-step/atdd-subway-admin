package nextstep.subway.domain;

import java.util.Collections;
import java.util.Objects;
import java.util.Optional;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "LINE")
public class Line extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private String color;

    @Embedded
    private Sections sections;

    protected Line() {}

    private Line(Long id, String name, String color, Station upStation, Station downStation, Integer distance) {
        validateName(name);
        validateColor(color);
        this.id = id;
        this.name = name;
        this.color = color;

        Section section = Section.of(upStation, downStation, distance, this);
        this.sections = Sections.from(Collections.singletonList(section));
    }

    public static Line of(String name, String color, Station upStation, Station downStation, Integer distance) {
        return new Line(null, name, color, upStation, downStation, distance);
    }

    private void validateName(String name) {
        if (isBlankString(name)) {
            throw new IllegalArgumentException("노선 생성 시 이름은 필수 값 입니다.");
        }
    }

    private void validateColor(String color) {
        if (isBlankString(color)) {
            throw new IllegalArgumentException("노선 생성 시 색깔은 필수 값 입니다.");
        }
    }

    private boolean isBlankString(String text) {
        return Objects.isNull(text) || text.trim().isEmpty();
    }

    public void update(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public void addSection(Section section) {
        sections.add(section);
    }

    public void removeSection(Optional<Section> upSection, Optional<Section> downSection) {
        sections.remove(upSection, downSection);
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

    public Sections getSections() {
        return sections;
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
        return Objects.equals(id, line.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
