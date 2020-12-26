package nextstep.subway.line.domain;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.line.domain.sections.*;

import javax.persistence.*;
import java.util.List;

@Entity
public class Line extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String name;
    private String color;
    private Sections sections = Sections.of();

    public Line() {
    }

    public Line(String name, String color) {
        this(null, name, color);
    }

    Line(Long id, String name, String color) {
        this.id = id;
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

    public int getSectionsSize() {
        return sections.size();
    }

    public void initFirstSection(final Long upStationId, final Long downStationId, final Long distance) {
        this.sections.initFirstSection(new Section(upStationId, downStationId, distance));
    }

    public boolean addSection(final Section newSection) {
        if (sections.isInEndSection(newSection)) {
            return this.sections.addEndSection(newSection);
        }

        return this.sections.addNotEndSection(newSection);
    }

    public List<Long> getStationIds() {
        return this.sections.getStationIdsOrderBySection();
    }
}
