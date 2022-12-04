package nextstep.subway.domain;

import java.util.List;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import org.apache.commons.lang3.StringUtils;

@Entity
public class Line extends BaseEntity {
    private static final String ERROR_MESSAGE_IS_BLANK_NAME = "노선 이름은 필수입니다.";
    private static final String ERROR_MESSAGE_IS_BLANK_COLOR = "노선 색상은 필수입니다.";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true, nullable = false)
    private String name;
    @Column
    private String color;
    @Embedded
    private Sections sections = new Sections();

    protected Line() {
    }

    private Line(String name, String color) {
        validNameAndColor(name, color);

        this.name = name;
        this.color = color;
    }

    public static Line of(String name, String color) {
        return new Line(name, color);
    }

    public static Line of(String name, String color, Section section) {
        Line line = new Line(name, color);
        line.addSection(section);
        return line;
    }

    private void validNameAndColor(String name, String color) {
        if (StringUtils.isBlank(name)) {
            throw new IllegalArgumentException(ERROR_MESSAGE_IS_BLANK_NAME);
        }

        if (StringUtils.isBlank(color)) {
            throw new IllegalArgumentException(ERROR_MESSAGE_IS_BLANK_COLOR);
        }
    }

    public void updateLine(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public void addSection(Section section) {
        sections.addSection(section);
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

    public List<Station> getStations() {
        return sections.getSortStation();
    }

    public int totalDistance() {
        return sections.totalDistance();
    }
}