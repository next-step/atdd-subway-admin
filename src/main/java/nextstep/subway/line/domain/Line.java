package nextstep.subway.line.domain;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.common.Distance;
import nextstep.subway.section.domain.Section;
import nextstep.subway.section.domain.Sections;
import nextstep.subway.section.dto.SectionAddModel;
import nextstep.subway.station.domain.Station;

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

    @Embedded
    private Sections sections;

    protected Line() {
    }

    public Line(String name, String color) {
        this.name = name;
        this.color = color;
        this.sections = new Sections();
    }

    public void update(Line line) {
        this.name = line.getName();
        this.color = line.getColor();
    }

    public void addSection(Section section) {
        this.sections.add(section);
        section.setLine(this);
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

    public List<Station> stations() {
        return sections.stations();
    }

    public Section upStationBeforeAdd(SectionAddModel sectionAddModel) {
        Section addSection = new Section(sectionAddModel.upStation(), sectionAddModel.downStation(),
                sectionAddModel.distance(), sectionAddModel.downSection().sectionOrder() - 1);
        this.addSection(addSection);

        return addSection;
    }

    public Section upStationAfterAdd(SectionAddModel sectionAddModel) {
        Section section = sectionAddModel.upSection();
        Station station = sectionAddModel.downStation();
        Distance distance = sectionAddModel.distance();
        section.compareDistance(distance);

        Section addSection = new Section(station, section.downStation(), section.minusDistance(distance), section.sectionOrder() + 1);
        this.addSection(addSection);

        section.setDownStation(station);
        section.setDistance(distance);

        return addSection;
    }

    public Section downStationBeforeAdd(SectionAddModel sectionAddModel) {
        Section section = sectionAddModel.downSection();
        Distance distance = sectionAddModel.distance();
        Station station = sectionAddModel.upStation();
        section.compareDistance(distance);

        Section addSection = new Section(section.upStation(), station, section.minusDistance(distance), section.sectionOrder() - 1);
        this.addSection(addSection);

        section.setUpStation(station);
        section.setDistance(distance);

        return addSection;
    }

    public Section downStationAfterAdd(SectionAddModel sectionAddModel) {
        Section addSection = new Section(sectionAddModel.upStation(), sectionAddModel.downStation(), sectionAddModel.distance(),
                sectionAddModel.upSection().sectionOrder() + 1);
        this.addSection(addSection);

        return addSection;
    }
}
