package nextstep.subway.section.domain;


import com.fasterxml.jackson.annotation.JsonManagedReference;
import nextstep.subway.advice.exception.SectionBadRequestException;
import nextstep.subway.station.domain.Station;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<Section> sections;

    public Sections() {
        sections = new ArrayList<>();
    }

    public Sections(Section section) {
        sections = new ArrayList<>();
        sections.add(section);
    }

    public List<Section> getSections() {
        return sections;
    }

    public void addInitSection(Section newSection) {
        sections.add(newSection);
    }

    public void addSection(Section newSection) {
        if (isTerminalStation(newSection)) {
            sections.add(newSection);
            return;
        }
        if (isUpStation(newSection)) {
            addMiddleDownStation(newSection);
            sections.add(newSection);
            return;
        }
        if (isDownStation(newSection)) {
            addMiddleUpStation(newSection);
            sections.add(newSection);
            return;
        }
    }

    public List<Station> getDownStations() {
        List<Station> downStations = sections.stream()
                .map(section -> section.getDownStation())
                .collect(Collectors.toList());
        return downStations;
    }

    public List<Station> getUpStations() {
        List<Station> upStations = sections.stream()
                .map(section -> section.getUpStation())
                .collect(Collectors.toList());
        return upStations;
    }

    public void updateSections(List<Section> newSections) {
        this.sections = newSections;
    }

    private boolean isUpStation(Section newSection) {
        return sections.stream()
                .anyMatch(section -> section.getUpStation().equals(newSection.getUpStation()));
    }

    private boolean isDownStation(Section newSection) {
        return sections.stream()
                .anyMatch(section -> section.getDownStation().equals(newSection.getDownStation()));
    }

    private void addMiddleDownStation(Section newSection) {
        sections.stream()
                .filter(section -> isNotDuplicateStations(section, newSection))
                .filter(section -> section.getUpStation().equals(newSection.getUpStation()))
                .filter(section -> validateDistance(section, newSection.getDistance()))
                .findFirst().ifPresent(section -> {
            section.updateUpStation(newSection.getDownStation());
            section.updateDistance(section.getDistance() - newSection.getDistance());
        });
    }

    private void addMiddleUpStation(Section newSection) {
        sections.stream()
                .filter(section -> isNotDuplicateStations(section, newSection))
                .filter(section -> section.getDownStation().equals(newSection.getDownStation()))
                .filter(section -> validateDistance(section, newSection.getDistance()))
                .findFirst().ifPresent(section -> {
            section.updateDownStation(newSection.getUpStation());
            section.updateDistance(section.getDistance() - newSection.getDistance());
        });
    }

    private boolean isTerminalStation(Section newSection) {
        return sections.stream()
                .filter(section -> isNotDuplicateStations(section, newSection))
                .anyMatch(section -> section.getUpStation().equals(newSection.getDownStation()) ||
                        section.getDownStation().equals(newSection.getUpStation()));
    }

    private boolean validateDistance(Section section, int distance) {
        if (section.getDistance() > distance) {
            return true;
        }
        throw new SectionBadRequestException(section.getDistance(), distance);
    }

    private boolean isNotDuplicateStations(Section section, Section newSection) {
        if (!(section.getUpStation().equals(newSection.getUpStation())
                && section.getDownStation().equals(newSection.getDownStation()))) {
            return true;
        }
        throw new SectionBadRequestException(section);
    }

}
