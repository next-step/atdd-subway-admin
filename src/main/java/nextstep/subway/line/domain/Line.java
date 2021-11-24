package nextstep.subway.line.domain;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.line.exception.NotEmptyLineColorException;
import nextstep.subway.line.exception.NotEmptyLineNameException;
import nextstep.subway.station.domain.Station;
import org.springframework.util.StringUtils;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.util.List;

@Entity
@Table(
        uniqueConstraints = @UniqueConstraint(name = "uk_line_name", columnNames = {"name"})
)
public class Line extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
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

    private Line(String name, String color, List<Section> sections) {
        this(name, color);
        this.sections = Sections.of(sections);
    }

    public static Line of(String name, String color, Section section) {
        return new Line(name, color, section);
    }

    public static Line of(String name, String color, List<Section> sections) {
        return new Line(name, color, sections);
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

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public Long getId() {
        return id;
    }

    public List<Station> getStationsBySort(LineStationSortAble sortAble) {
        return this.sections.getStationsBySort(sortAble);
    }
}
