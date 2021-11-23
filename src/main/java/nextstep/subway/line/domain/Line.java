package nextstep.subway.line.domain;

import java.util.List;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import nextstep.subway.common.BaseEntity;
import nextstep.subway.common.Messages;
import nextstep.subway.exception.BusinessException;
import nextstep.subway.section.domain.Distance;
import nextstep.subway.section.domain.Section;
import nextstep.subway.section.domain.SectionType;
import nextstep.subway.station.domain.Station;

@Entity
public class Line extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String name;
    private String color;

    @Embedded
    private SectionList sections = new SectionList();

    protected Line() {
    }

    public Line(String name, String color) {
        this.name = name;
        this.color = color;
    }

    /**
     * 연관관계 편의 메서드
     *
     * @param section
     */
    public void addSection(Section section) {
        this.sections.add(section);
        if (!section.equalsLine(this)) {
            section.setLine(this);
        }
    }

    public void removeSection(Section section) {
        this.sections.remove(section);
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

    public List<Section> getSortedSections() {
        return sections.getSortedList();
    }

    public boolean containsSection(Section section) {
        return this.sections.contains(section);
    }

    public Line addSection(Distance distance, Station upStation, Station downStation) {
        Section upSection = sections.findByStation(upStation);
        Section downSection = sections.findByStation(downStation);

        validate(upSection, downSection);

        if (upSection != null) {
            addDownSection(distance, upSection, downStation);
            return this;
        }

        if (downSection != null) {
            addUpSection(distance, downSection, upStation);
            return this;
        }
        return this;
    }

    private void addDownSection(Distance distance, Section upSection, Station downStation) {
        if (upSection.isDownStation()) {
            upSection.update(distance, SectionType.MIDDLE, downStation);
            Section downSection = Section.fromDownSection(downStation, this);
            sections.add(downSection);
            return;
        }

        addMiddleSection(upSection, downStation, distance, upSection.getLinkStation());
    }

    private void addUpSection(Distance distance, Section downSection, Station upStation) {
        if (downSection.isUpStation()) {
            downSection.update(SectionType.MIDDLE);
            Section upSection =
                Section.ofUpSection(distance, upStation, downSection.getStation(), this);
            sections.add(upSection);
            return;
        }

        Section upSection = sections.findByLinkStation(downSection.getStation());
        addMiddleSection(upSection, upStation, distance, downSection.getStation());
    }

    private void addMiddleSection(Section updateSection, Station addStation, Distance distance,
        Station linkStation) {
        updateSection.update(updateSection.calculateDistance(distance), addStation);
        Section middleSection = Section.ofMiddleSection(distance, addStation, linkStation, this);
        sections.add(middleSection);
    }

    private void validate(Section upSection, Section downSection) {
        if (upSection == null && downSection == null) {
            throw new BusinessException(Messages.NOT_INCLUDE_SECTION.getValues());
        }

        if (upSection != null && downSection != null) {
            throw new BusinessException(Messages.ALREADY_EXISTS_SECTION.getValues());
        }
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
