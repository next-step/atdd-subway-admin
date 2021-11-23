package nextstep.subway.line.domain;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.section.domain.Section;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Entity
public class Line extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", unique = true)
    private String name;

    @Column(name = "color")
    private String color;

    @OneToMany(mappedBy = "line", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Section> sections = new ArrayList<>();

    protected Line() {
    }

    public Line(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public void update(Line line) {
        this.name = line.getName();
        this.color = line.getColor();
    }

    public List<Section> getStationInOrder() {
        List<Section> sectionsResult = new ArrayList<>();

        Optional<Section> preStation = this.sections.stream()
                .filter(st -> st.getPreStation() == null)
                .findFirst();

        while (preStation.isPresent()) {
            final Section section = preStation.get();
            sectionsResult.add(section);

            preStation = sections.stream()
                    .filter(st -> st.getPreStation() != null)
                    .filter(st -> st.getPreStation().equals(section.getStation()))
                    .findFirst();
        }

        return sectionsResult;
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

    public void addSection(final Section section) {
        this.sections.add(section);
    }

}
