package nextstep.subway.domain;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import org.apache.commons.lang3.StringUtils;

@Entity
public class Line extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    private String color;

    @OneToMany(mappedBy = "line", cascade = CascadeType.PERSIST)
    private List<Section> sections = new ArrayList<>();

    public static Line of(String name, String color, Station upStation, Station downStation, Integer distance) {
        Line line = new Line(name, color);
        line.addInitialSections(Section.makeInitialSections(upStation, downStation, distance));
        return line;
    }

    protected Line() {

    }

    public Line(String name, String color) {
        validName(name);
        validColor(color);

        this.name = name;
        this.color = color;
    }

    public List<Station> getStations() {
        Set<Station> stations = new HashSet<>();
        sections.forEach(section -> {
                    stations.add(section.getUpStation());
                    stations.add(section.getDownStation());
                });
        return stations.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
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

    public void changeNameAndColor(String newName, String newColor) {
        this.name = newName;
        this.color = newColor;
    }

    public List<Section> getSections() {
        return sections;
    }

    public void addSection(Section section) {
        if (!this.sections.contains(section)) {
            this.sections.add(section);
        }
        if (section.getLine() != this) {
            section.setLine(this);
        }
    }

    private void addInitialSections(List<Section> sections) {
        sections.forEach(this::addSection);
    }

    private static void validColor(String color) {
        if (StringUtils.isBlank(color)) {
            throw new IllegalArgumentException("노선의 색을 입력하세요.");
        }
    }

    private static void validName(String name) {
        if (StringUtils.isBlank(name)) {
            throw new IllegalArgumentException("노선의 이름을 입력하세요.");
        }
    }
}
