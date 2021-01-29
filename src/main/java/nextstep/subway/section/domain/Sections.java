package nextstep.subway.section.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import nextstep.subway.station.domain.Station;
import nextstep.subway.util.CommonException;
import nextstep.subway.util.Message;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.EntityNotFoundException;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Embeddable
@NoArgsConstructor
@Getter
public class Sections {

    public static final int REMOVE_SECTION_SIZE = 3;
    @OneToMany(mappedBy = "line" , cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    public Sections(Section upSection, Section downSection) {
        this.sections = Arrays.asList(upSection, downSection);
    }

    public List<Station> getStations() {
        List<Station> stations = new ArrayList<>();
        Section upEndSection = findUpEndSection();
        stations.add(upEndSection.getUpStation());
        Section nextSection = upEndSection;
        while (nextSection != null) {
            stations.add(nextSection.getDownStation());
            nextSection = findSectionByNextUpStation(nextSection.getDownStation());
        }
        return stations;
    }

    private Section findUpEndSection() {
        List<Station> downStations = this.sections.stream()
                .map(Section::getDownStation)
                .collect(toList());
        return this.sections.stream()
                .filter(section -> !downStations.contains(section.getUpStation()))
                .findFirst()
                .orElseThrow(EntityNotFoundException::new);
    }

    private Section findSectionByNextUpStation(Station station) {
        return this.sections.stream()
                .filter(section -> section.getUpStation() == station)
                .findFirst()
                .orElse(null);
    }


    public void add(Section section) {
        validateNotExistStation(section);
        alreadyExistStation(section);

        addUpStation(section);
        addDownStation(section);

        this.sections.add(section);
    }

    private void addUpStation(Section section) {
        sections.stream()
                .filter(oldSection -> oldSection.isUpStationInSection(section.getUpStation()))
                .findFirst()
                .ifPresent(oldSection -> {
                    oldSection.updateUpStationToDownStation(section.getDownStation(), section.getDistance());
                });
    }

    private void addDownStation(Section section) {
        sections.stream()
                .filter(oldSection -> oldSection.isDownStationInSection(section.getDownStation()))
                .findFirst()
                .ifPresent(oldSection -> {
                    oldSection.updateDownStationToUpStation(section.getUpStation(), section.getDistance());
                });
    }

    private void validateNotExistStation(Section section) {
        List<Station> stations = getStations();
        if(!stations.contains(section.getUpStation()) && !stations.contains(section.getDownStation())) {
            CommonException.throwIllegalArgumentException(Message.NOT_EXIST_STATION_MESSAGE);
         }
    }

    private void alreadyExistStation(Section section) {
        if(getStations().containsAll(Arrays.asList(section.getUpStation(), section.getDownStation()))) {
            CommonException.throwIllegalArgumentException(Message.ALREADY_EXIST_STATION_MESSAGE);
        }
    }

    public void removeSection(Station station) {
        validateRemovableSection();
        Section removeSection = findSection(station.getId());
        updateSectionByRemove(removeSection);
        this.sections.remove(removeSection);
    }

    private void validateRemovableSection() {
        if(this.sections.size() < REMOVE_SECTION_SIZE) {
            throw new IllegalArgumentException(Message.ESSENTIAL_ONE_SECTION_MESSAGE);
        }
    }

    private Section findSection(Long stationId) {
        return this.sections.stream()
                .filter(section -> section.isDownStationInSection(stationId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(Message.NOT_FOUND_STATION_MESSAGE));
    }

    private void updateSectionByRemove(Section removeSection) {
        this.sections.stream()
                .filter(section -> section.isUpStationInSection(removeSection.getDownStation()))
                .findFirst()
                .ifPresent(section -> section.updateUpStationToRemove(removeSection.getUpStation(), removeSection.getDistance()));
    }
}
