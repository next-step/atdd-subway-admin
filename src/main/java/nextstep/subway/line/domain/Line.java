package nextstep.subway.line.domain;

import nextstep.subway.common.BaseEntity;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;
import java.util.LinkedList;
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
    @OrderColumn
    private List<Section> sections = new LinkedList<>();

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
        checkValidation(section);
        section.setLine(this);
        this.sections.add(section);
    }

    private void addSection(int index, Section section) {
        checkValidation(section);
        section.setLine(this);
        this.sections.add(index, section);
    }

    private void checkValidation(Section targetSection) {
        if (this.sections.contains(targetSection) || targetSection.isZeroDistance()) {
            throw new IllegalArgumentException();
        }
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
        changeUpStation(targetSection);
        addSection(endPointIndex(targetSection), targetSection);
    }

    private void changeUpStation(Section targetSection) {
        this.sections.stream()
                .filter(base -> base.isSameUpStation(targetSection.getUpStation()))
                .findFirst()
                .ifPresent(base -> {
                    base.changeUpStation(targetSection.getDownStation());
                    base.changeDistance(targetSection.getDistance());
                });
    }

    private Integer endPointIndex(Section targetSection) {
        return this.sections.stream()
                .filter(base -> base.isSameUpStation(targetSection.getDownStation()))
                .findFirst()
                .map(base -> this.sections.indexOf(base))
                .orElseGet(() -> descendEndPointIndex(targetSection));
    }

    private Integer descendEndPointIndex(Section targetSection) {
        return this.sections.stream()
                .filter(base -> base.isSameDownStation(targetSection.getUpStation()))
                .findFirst()
                .map(base -> this.sections.indexOf(base) + 1)
                .orElseThrow(IllegalArgumentException::new);
    }

}
