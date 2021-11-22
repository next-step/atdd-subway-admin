package nextstep.subway.line.domain;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.line.exception.NotEmptyLineColorException;
import nextstep.subway.line.exception.NotEmptyLineNameException;
import org.springframework.util.StringUtils;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Collections;
import java.util.List;

@Entity
public class Line extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String name;
    private String color;
    @Embedded
    private Sections sections;

    protected Line() {
    }

    private Line(String name, String color) {
        validate(name, color);
        this.name = name;
        this.color = color;
    }

    private Line(String name, String color, Section section) {
        this(name, color);
        this.sections = Sections.of(section);
    }

    public static Line of(String name, String color, Section section) {
        return new Line(name, color, section);
    }

    private void validate(String name, String color) {

        if (!StringUtils.hasText(name)) {
            throw new NotEmptyLineNameException();
        }
        if (!StringUtils.hasText(color)) {
            throw new NotEmptyLineColorException();
        }
    }

    public void update(Line line) {
        this.name = line.getName();
        this.color = line.getColor();
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

    public List<Section> getSections() {
        return Collections.unmodifiableList(sections.getSections());
    }
}
