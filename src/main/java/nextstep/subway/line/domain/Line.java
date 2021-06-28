package nextstep.subway.line.domain;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.section.domain.Section;
import nextstep.subway.section.domain.Sections;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.dto.StationResponse;
import nextstep.subway.station.dto.StationsResponse;

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
    private Sections sections = new Sections();

    public Line() {
    }

    public Line(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public Line(String name, String color, Section section) {
        this.name = name;
        this.color = color;
        addSection(section);
    }

    public void validateAndRemoveSection(Station removeStation) {
        sections.validateAndRemoveSectionByPosition(removeStation);
    }

    public void validateAndAddSections(long newDistance, Station newUpStation, Station newDownStation) {
        sections.validateAndAddSections(newDistance, newUpStation, newDownStation);
    }

    public void update(Line line) {
        this.name = line.getName();
        this.color = line.getColor();
    }

    public Line addSection(Section section) {
        sections.add(section);
        section.addLine(this);

        return this;
    }

    public StationsResponse extractStationsResponse() {
        return StationsResponse.of(extractStations());
    }

    private List<Station> extractStations() {
        return sections.extractStations();
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

    public List<Section> getSections() {
        return sections.getSections();
    }
}
