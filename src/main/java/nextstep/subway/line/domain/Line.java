package nextstep.subway.line.domain;

import nextstep.subway.common.BaseEntity;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Line extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String name;
    private String color;

    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL)
    private List<Section> sections = new ArrayList<>();

    public Line() {
    }

    public Line(String name, String color) {
        this.name = name;
        this.color = color;
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

    public void addSection(Section section) {
        section.setLine(this);
        this.sections.add(section);
    }

    public List<Section> getSections() {
        return sections;
    }

    public void updateSection(Section section) {
        if (isChangedStations(section)) {
            this.sections.stream()
                    .findFirst()
                    .ifPresent(s -> s.update(section));
        }
    }

    private boolean isChangedStations(Section section) {
        return !this.sections.stream()
                .allMatch(s -> s.equals(section));
    }

    public void registrySection(Section targetSection) {
        this.sections.stream()
                .filter(base -> base.isSameUpStation(targetSection.getUpStation()))
                .findFirst()
                .ifPresent(base -> targetSection.changeUpStation(base.getDownStation()));
        addSection(targetSection);
    }

}
