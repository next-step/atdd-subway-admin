package nextstep.subway.line.domain;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.common.ErrorCode;
import nextstep.subway.exception.NotAcceptableApiException;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.section.domain.Section;
import nextstep.subway.section.domain.Sections;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.Stations;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Entity
public class Line extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String name;

    private String color;

    @Embedded
    private final Sections sections = new Sections();

    public Line() {
    }

    public Line(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public Line(String name, String color, Station upStation, Station downStation, int distance) {
        this(name, color);
        sections.addSection(Section.of(this, upStation, downStation, distance));
    }

    public static Line of(String name, String color, Station upStation, Station downStation, int distance) {
        return new Line(name, color, upStation, downStation, distance);
    }

    public void update(Line line) {
        this.name = line.getName();
        this.color = line.getColor();
    }

    public LineResponse addSection(Station upStation, Station downStation, int distance) {
        Section section = new Section(upStation, downStation, this, distance);
        sections.addSection(section);

        return LineResponse.of(this);
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

    public Stations getOrderedStations() {
        Stations orderedStations = new Stations();
        Section section = sections.getFirstSection();
        orderedStations.add(section.getUpStation());
        orderedStations.add(section.getDownStation());

        while (section.hasNexSection()) {
            Section nextSection = section.getNextSection();
            orderedStations.add(nextSection.getDownStation());
            section = nextSection;
        }
        return orderedStations;
    }

    public List<Section> getSections() {
        return sections.getSections();
    }

    public void deleteStation(Station station) {
        validateDeleteStation(station);

        if (deleteFirstStation(station)) {
            return;
        }
        if (deleteLastStation(station)) {
            return;
        }
        deleteBetweenStation(station);
    }

    public void validateDeleteStation(Station station) {
        if (!hasDeletableSection()) {
            throw new NotAcceptableApiException(ErrorCode.CAN_NOT_DELETE_SECTION);
        }
        if (getOrderedStations().notContains(station)) {
            throw new NotAcceptableApiException(ErrorCode.NOT_REGISTERED_STATION_TO_LINE);
        }
    }

    private boolean hasDeletableSection() {
        return getSections().size() > 1;
    }

    private boolean deleteFirstStation(Station station) {
        Section firstSection = sections.getFirstSection();
        if (station.isUpStation(firstSection)) {
            return sections.removeSection(firstSection);
        }
        return false;
    }

    private boolean deleteLastStation(Station station) {
        Section lastSection = sections.getLastSection();
        if (station.isDownStation(lastSection)) {
            return sections.removeSection(lastSection);
        }
        return false;
    }

    private void deleteBetweenStation(Station station) {
        Section oldSection = sections.getOldSectionByDownStation(station);
        Section nextOldSection = sections.getOldSectionByUpStation(station);

        Section newSection = Section.of(
                this
                , oldSection.getUpStation()
                , nextOldSection.getDownStation()
                , oldSection.getDistance() + nextOldSection.getDistance());

        sections.getSections().removeAll(Arrays.asList(oldSection, nextOldSection));
        sections.addSection(newSection);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Line line = (Line) o;
        return Objects.equals(id, line.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
