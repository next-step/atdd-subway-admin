package nextstep.subway.dto;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Sections;

import java.util.LinkedHashSet;
import java.util.Set;

public class LineResponse {

    private Long id;
    private String name;
    private String color;
    private Set<StationResponse> stations = new LinkedHashSet<>();

    private LineResponse() {}

    public LineResponse(Long id, String name, String color, Sections sections) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.stations = sortSections(sections);
    }

    private Set<StationResponse> sortSections(Sections sections) {
        Set<StationResponse> stationResponses = new LinkedHashSet<>();

        Section firstSection = findFirstSection(sections);
        stationResponses.add(StationResponse.of(firstSection.getUpStation()));
        for (int i = 0; i < sections.getList().size(); i++) {
            addedNextSectionStations(firstSection, sections, stationResponses);
        }

        return stationResponses;
    }

    private void addedNextSectionStations(Section firstSection,
                                          Sections sections,
                                          Set<StationResponse> stationResponses) {
        for (Section section : sections.getList()) {
            firstSection = resetNextSection(section, firstSection);
            stationResponses.add(StationResponse.of(firstSection.getUpStation()));
            stationResponses.add(StationResponse.of(firstSection.getDownStation()));
        }
    }

    private Section resetNextSection(Section section, Section firstSection) {
        if (section.isPostStation(firstSection)) {
            return section;
        }
        return firstSection;
    }

    private Section findFirstSection(Sections sections) {
        Section firstSection = null;
        for (Section section : sections.getList()) {
            firstSection = resetFirstSection(section, firstSection);
        }
        return firstSection;
    }

    private Section resetFirstSection(Section section, Section firstSection) {
        if (section.isPreStation(firstSection)) {
            return section;
        }
        return firstSection;
    }

    public static LineResponse of(Line line) {
        return new LineResponse(
                line.getId(),
                line.getName(),
                line.getColor(),
                line.getSections());
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

    public Set<StationResponse> getStations() {
        return stations;
    }

}
