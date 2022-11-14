package nextstep.subway.domain;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
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

    @OneToMany(mappedBy = "line")
    private List<Section> sections = new ArrayList<>();

    protected Line() {

    }

    public Line(String name, String color) {

        validName(name);
        validColor(color);

        this.name = name;
        this.color = color;
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

    public void addInitialSections(List<Section> sections) {
        this.sections = sections;
    }

    public List<Section> getSections() {
        return sections;
    }
}
