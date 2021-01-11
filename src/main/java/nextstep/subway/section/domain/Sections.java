package nextstep.subway.section.domain;


import com.fasterxml.jackson.annotation.JsonManagedReference;
import nextstep.subway.advice.exception.SectionBadRequestException;
import nextstep.subway.advice.exception.SectionNotFoundException;
import nextstep.subway.advice.exception.StationNotFoundException;
import nextstep.subway.station.domain.Station;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Section> sections = new ArrayList<>();

    public List<Section> getSections() {
        return sections;
    }

    public void createSection(Section newSection) {
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

    public void updateSections(List<Section> newSections) {
        this.sections.removeAll(this.sections);
        this.sections.addAll(newSections);
    }

    public void deleteSection(Station station) {
        if (!containStation(station)) throw new StationNotFoundException(station.getId());

        Section upStationSection = getUpStationSection(station);
        Section downStationSection = getDownStationSection(station);

        sections.stream()
                .filter(section -> section == upStationSection)
                .forEachOrdered(section -> {
                    section.updateDistance(upStationSection.getDistance() + downStationSection.getDistance());
                    section.updateDownStation(downStationSection.getDownStation());
                });

        Section deleteSection = sections.stream()
                .filter(section -> section == downStationSection)
                .findFirst().get();

        sections.remove(deleteSection);
    }

    private Section getDownStationSection(Station station) {
        Section downStationSection = sections.stream()
                .filter(section -> section.getUpStation() == station)
                .findFirst().orElseThrow(() -> new SectionNotFoundException(station));
        return downStationSection;
    }

    private Section getUpStationSection(Station station) {
        Section upStationSection = sections.stream()
                .filter(section -> section.getDownStation() == station)
                .findFirst().orElseThrow(() -> new SectionNotFoundException(station));


        return upStationSection;
    }

    private boolean containStation(Station station) {
        return sections.stream()
                .anyMatch(section -> section.getDownStation() == station
                        || section.getUpStation() == station);
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
