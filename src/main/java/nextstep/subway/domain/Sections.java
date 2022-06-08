package nextstep.subway.domain;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static nextstep.subway.common.Messages.*;

@Embeddable
public class Sections {
    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    protected Sections() {
    }

    public void addSections(Section section) {
        validate(section);
        Station upStation = section.getUpStation();
        Station downStation = section.getDownStation();

        if (matchUpStation(upStation)) {
            updateUpStation(section);
        }

        if (matchDownStation(downStation)) {
            updateDownStation(section);
        }

        sections.add(section);
    }

    public List<Section> getSections() {
        return sections;
    }

    public List<Station> getStations() {
        List<Station> stations = new ArrayList<>();

        sections.forEach(section -> {
            stations.add(section.getUpStation());
            stations.add(section.getDownStation());
        });

        return stations.stream()
                .distinct()
                .collect(Collectors.toList());
    }

    private void validate(Section section) {
        if (sections.contains(section)) {
            throw new IllegalArgumentException(DUPLICATE_SECTION_ERROR);
        }

        if (getStations().size() > 0 && !isContainsStation(section.getUpStation(), section.getDownStation())) {
            throw new IllegalArgumentException(NOT_MATCH_STATION_ERROR);
        }
    }

    private boolean isContainsStation(Station upStation, Station downStation) {
        return getStations().contains(upStation) || getStations().contains(downStation);
    }

    private void updateUpStation(Section section) {
        Station upStation = section.getUpStation();
        Section findSection = findSectionByUpStation(upStation);
        findSection.updateUpStation(section.getDownStation(), section.getDistance());
    }

    private void updateDownStation(Section section) {
        Station downStation = section.getDownStation();
        Section findSection = findSectionByDownStation(downStation);
        findSection.updateDownStation(section.getUpStation(), section.getDistance());
    }

    private boolean matchUpStation(Station station) {
        return sections.stream().anyMatch(section -> section.isEqualsUpStation(station));
    }

    private Section findSectionByUpStation(Station station) {
        return sections.stream()
                .filter(section -> section.isEqualsUpStation(station))
                .findFirst()
                .orElseGet(Section::new);
    }

    private boolean matchDownStation(Station station) {
        return sections.stream().anyMatch(section -> section.isEqualsDownStation(station));
    }

    private Section findSectionByDownStation(Station station) {
        return sections.stream()
                .filter(section -> section.isEqualsDownStation(station))
                .findFirst()
                .orElseGet(Section::new);
    }

    public Section deleteSectionByStation(Station station) {
        validateDeleteStation(station);

        if (isUpStationAndDownStation(station)) {
            return matchUpStationAndDownStation(station);
        }

        if (matchUpStation(station)) {
            return matchAndRemoveUpStation(station);
        }

        return matchAndRemoveDownStation(station);
    }

    private Section matchUpStationAndDownStation(Station station) {
        Section sectionByUpStation = findSectionByUpStation(station);
        Section sectionByDownStation = findSectionByDownStation(station);

        Section section = Section.of(
                sectionByUpStation.getDistance() + sectionByDownStation.getDistance(),
                sectionByDownStation.getUpStation(),
                sectionByUpStation.getDownStation()
        );

        sections.add(section);
        sections.remove(sectionByUpStation);
        sections.remove(sectionByDownStation);
        return section;
    }

    private Section matchAndRemoveUpStation(Station station) {
        Section upStation = findSectionByUpStation(station);
        sections.remove(upStation);
        return upStation;
    }

    private Section matchAndRemoveDownStation(Station station) {
        Section downStation = findSectionByDownStation(station);
        sections.remove(downStation);
        return downStation;
    }

    private boolean isUpStationAndDownStation(Station station) {
        return matchUpStation(station) && matchDownStation(station);
    }

    private void validateDeleteStation(Station station) {
        if (sections.size() < 1) {
            throw new IllegalArgumentException(STATION_MINIMUM_DELETE_ERROR);
        }

        if ((!matchUpStation(station) || !matchDownStation(station))) {
            throw new IllegalArgumentException(NOT_MATCH_STATION_DELETE_ERROR);
        }
    }
}
