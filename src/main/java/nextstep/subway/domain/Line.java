package nextstep.subway.domain;

import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import nextstep.subway.exception.InvalidSectionException;
import nextstep.subway.exception.InvalidStringException;
import org.apache.commons.lang3.StringUtils;

@Entity
public class Line extends BaseEntity {

    private static final String INVALID_LINE_NAME = "노선 이름정보가 존재하지 않습니다.";
    private static final String INVALID_LINE_COLOR = "노선 색상정보가 존재하지 않습니다.";
    private static final String INVALID_SECTION = "구간 정보가 존재하지 않습니다.";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String name;

    @Column
    private String color;

    @Embedded
    private Sections sections = new Sections();

    protected Line() {
    }

    public Line(String name, String color, Section section) {
        validate(name, color, section);
        this.name = name;
        this.color = color;
        addSection(section);
    }

    public void update(String name, String color) {
        this.name = name;
        this.color = color;
    }

    private void validate(String name, String color, Section section) {
        validateName(name);
        validateColor(color);
        validateSection(section);
    }

    private void validateName(String name) {
        if (StringUtils.isEmpty(name)) {
            throw new InvalidStringException(INVALID_LINE_NAME);
        }
    }

    private void validateColor(String color) {
        if (StringUtils.isEmpty(color)) {
            throw new InvalidStringException(INVALID_LINE_COLOR);
        }
    }

    private void validateSection(Section section){
        if (Objects.isNull(section)) {
            throw new InvalidSectionException(INVALID_SECTION);
        }
    }

    public void addSection(Section section) {
        sections.connect(section);
        section.addLine(this);
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
        if (o == null || getClass() != o.getClass()) {
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
