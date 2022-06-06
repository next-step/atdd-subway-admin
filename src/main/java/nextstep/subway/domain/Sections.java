package nextstep.subway.domain;

import nextstep.subway.exception.ErrorMessage;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Embeddable
public class Sections {
    @OneToMany(mappedBy = "line", cascade=CascadeType.ALL, orphanRemoval = true)
    private final List<Section> sections = new ArrayList<>();

    public Sections() {
    }

    public List<Section> getSections() {
        return sections;
    }

    public void addFisrtSection(Section section) {
        if (sections.size() == 0) {
            sections.add(section);
        }
    }

    public void addSection(Section section) {
        checkValidation(section);

        AddSectionType addType = determineAddSection(section);

        if (addType == AddSectionType.NEW_UP_STATION) {
            setNewUptation(section);
        }

        if (addType == AddSectionType.NEW_DOWN_STATION) {
            setDownStation(section);
        }

        if (addType == AddSectionType.NEW_STATION) {
            insertStation(section);
        }
    }

    public void removeSection(Station deleteStation) {
        List<Section> containingDeleteStationSections = findSectionsContainingStation(deleteStation);

        if (containingDeleteStationSections.size() == 0) {
            throw new IllegalArgumentException(ErrorMessage.NOT_FOUND_STATION_FOR_DELETE_SECTION.getMessage());
        }

        checkDeletableSection();

        if (containingDeleteStationSections.size() > 1) {
            relocateStation(deleteStation);
        }

        sections.removeIf(section -> section.getUpStation() == deleteStation || section.getDownStation() == deleteStation);
    }

    private void relocateStation(Station station) {
        // deleteStation - x
        Section containingUpStation = findSectionContainingUpStationForDelete(station);
        // x - deleteStation
        Section containingDownStation = findSectionContainingDownStationForDelete(station);

        int containingUpStationIndex = this.sections.indexOf(containingUpStation);
        containingUpStation.updateUpStationAndDistance(containingDownStation.getUpStation(), containingUpStation.getDistance() + containingDownStation.getDistance());
        this.sections.set(containingUpStationIndex, containingUpStation);
    }

    private List<Section> findSectionsContainingStation(Station station) {
        return sections.stream()
                .filter(section -> section.hasStation(station))
                .collect(Collectors.toList());
    }

    private Section findSectionContainingUpStationForDelete(Station station) {
        return sections.stream()
                .filter(section -> section.getUpStation() == station)
                .findAny().orElseThrow(() -> new IllegalArgumentException(ErrorMessage.NOT_FOUND_STATION_FOR_DELETE_SECTION.getMessage()));
    }

    private Section findSectionContainingDownStationForDelete(Station station) {
        return sections.stream()
                .filter(section -> section.getDownStation() == station)
                .findAny().orElseThrow(() -> new IllegalArgumentException(ErrorMessage.NOT_FOUND_STATION_FOR_DELETE_SECTION.getMessage()));
    }

    private void checkDeletableSection() {
        int deletableSectionSize = 1;
        if (sections.size() == deletableSectionSize) {
            throw new IllegalArgumentException(ErrorMessage.NOT_DELETABLE_SIZE_SECTION.getMessage());
        }
    }

    private boolean isRegisteredStation(Station station) {
        boolean result = false;

        for (Section section : sections) {
            result = result || (section.getUpStation() == station || section.getDownStation() == station);
        }

        return result;
    }

    private void checkValidation(Section section) {
        boolean isRegisteredUpstation = isRegisteredStation(section.getUpStation());
        boolean isRegisteredDownStation = isRegisteredStation(section.getDownStation());

        Station firstStation = sections.get(0).getUpStation();
        Station lastStation = sections.get(Math.max((sections.size() - 1), 0)).getDownStation();

        if (isRegisteredUpstation && isRegisteredDownStation) {
            throw new IllegalArgumentException(ErrorMessage.ALREADY_REGISTERED_LINE.getMessage());
        }

        if (!isRegisteredUpstation &&
                !isRegisteredDownStation &&
                section.getDownStation() != firstStation &&
                section.getUpStation() != lastStation
        ) {
            throw new IllegalArgumentException(ErrorMessage.NOT_FOUND_STATIONS_FOR_SECTION.getMessage());
        }
     }


    private void checkDistanceForNewStation(Section newSection, Section originSection) {
        if (originSection.getDistance() <= newSection.getDistance()) {
            throw new IllegalArgumentException(ErrorMessage.NOT_VALID_DISTANCE_FOR_SECTION.getMessage());
        }
    }

    private AddSectionType determineAddSection(Section section) {
        Station upStreamStation = sections.get(0).getUpStation();
        Station downStreamStation = sections.get(Math.max(sections.size() - 1, 0)).getDownStation();

        if (upStreamStation == section.getDownStation()) {
            return AddSectionType.NEW_UP_STATION;
        }

        if (downStreamStation == section.getUpStation()) {
            return AddSectionType.NEW_DOWN_STATION;
        }

        return AddSectionType.NEW_STATION;
    }

    private void setNewUptation(Section section) {
        int firstIndex = 0;
        this.sections.add(firstIndex, section);
    }

    private void setDownStation(Section section) {
        this.sections.add(section);
    }

    private void insertStation(Section section) {
        Section originSection = sections.stream().filter(s ->
          s.getUpStation() == section.getUpStation() && s.getDownStation() != null
        ).findAny().orElseThrow(() -> new IllegalArgumentException(ErrorMessage.NOT_FOUND_STATION_FOR_ADD_SECTION.getMessage()));

        checkDistanceForNewStation(section, originSection);

        int originalSectionIndex = this.sections.indexOf(originSection);
        originSection.updateUpStationAndDistance(section.getDownStation(), originSection.getDistance() - section.getDistance());
        sections.set(originalSectionIndex,originSection);

        int addSectionIndex = Math.max((originalSectionIndex - 1), 0);
        sections.add(addSectionIndex , section);
    }
}
