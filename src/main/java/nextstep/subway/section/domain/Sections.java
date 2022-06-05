package nextstep.subway.section.domain;

import nextstep.subway.consts.ErrorMessage;
import nextstep.subway.station.domain.Station;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Embeddable
public class Sections{
    public static final int MINIMUM_SECTION_LENGTH = 1;

    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL, orphanRemoval = true)
    List<Section> sections = new ArrayList<>();

    protected Sections() {
    }

    private Sections(List<Section> sections) {
        this.sections = sections;
    }

    public static Sections from(List<Section> sections) {
        return new Sections(sections);
    }

    public static Sections from(Section section) {
        return new Sections(new ArrayList<>(Arrays.asList(section)));
    }

    public void addSection(Section section) {
        validateSectionToAdd(section);
        updateAdjacentSection(section);
        sections.add(section);
    }

    private void validateSectionToAdd(Section section) {
        validateDuplicated(section);
        validateAddable(section);
    }

    private void validateDuplicated(Section section) {
        if (hasStation(section.upStation()) && hasStation(section.downStation())) {
            throw new IllegalArgumentException(
                    String.format(ErrorMessage.ERROR_SECTION_ALREADY_REGISTERED_STATIONS,
                            section.upStation().name(), section.downStation().name())
            );
        }
    }

    private void validateAddable(Section section) {
        if (!hasStation(section.upStation()) && !hasStation(section.downStation())) {
            throw new IllegalArgumentException(
                    String.format(ErrorMessage.ERROR_SECTION_UNKNOWN_STATIONS,
                            section.upStation().name(), section.downStation().name())
            );
        }
    }

    private void updateAdjacentSection(Section sectionToAdd) {
        for (Section section : sections) {
            section.divideWith(sectionToAdd);
        }
    }

    public List<Station> getSortedStations(){
        sortSections();
        List<Station> stations = sections.stream()
                .map(Section::upStation)
                .collect(Collectors.toList());
        if(!stations.isEmpty()) {
            stations.add(getLastDownStation());
        }
        return stations;
    }

    private Station getLastDownStation() {
        return sections.get(sections.size() - 1).downStation();
    }

    private boolean hasStation(Station station){
        return getSortedStations().contains(station);
    }

    private void sortSections() {
        sections.sort((Section section1, Section section2) -> {
            if (section1.isSameDownStation(section2.upStation())) {
                return -1;
            }
            return 1;
        });
    }

    public void removeStation(Station station) {
        validateStationToRemove(station);
        if (isEndStation(station)) {
            removeEndStation(station);
            return;
        }
        removeMiddleStation(station);
    }

    private void validateStationToRemove(Station station) {
        validateSectionMinimumLength();
        validateStationExists(station);
    }

    private void validateSectionMinimumLength() {
        if (sections.size() <= MINIMUM_SECTION_LENGTH) {
            throw new IllegalArgumentException(
                    String.format(ErrorMessage.ERROR_CANNOT_DELETE_SECTION_MINIMUM_LENGTH, MINIMUM_SECTION_LENGTH)
            );
        }
    }

    private void validateStationExists(Station station) {
        if (!hasStation(station)) {
            throw new IllegalArgumentException(
                    String.format(ErrorMessage.ERROR_CANNOT_DELETE_SECTION_NOT_EXIST, station.name())
            );
        }
    }

    private void removeEndStation(Station station) {
        sections.remove(getEndSection(station));
    }

    private void removeMiddleStation(Station station) {
        Section previousSection = findPreviousSection(station);
        Section postSection = findPostSection(station);
        previousSection.mergeWith(postSection);
        sections.remove(postSection);
    }

    private Section findPreviousSection(Station station) {
        return sections.stream()
                .filter(section -> section.isSameDownStation(station))
                .findFirst().orElseThrow(() -> new IllegalArgumentException(
                        String.format(ErrorMessage.ERROR_PREVIOUS_SECTION_NOT_FOUND, station.name()))
                );
    }

    private Section findPostSection(Station station) {
        return sections.stream()
                .filter(section -> section.isSameUpStation(station))
                .findFirst().orElseThrow(() -> new IllegalArgumentException(
                        String.format(ErrorMessage.ERROR_POST_SECTION_NOT_FOUND, station.name()))
                );
    }

    private Section getEndSection(Station station) {
        return sections.stream()
                .filter(section -> section.isSameDownStation(station) || section.isSameUpStation(station))
                .findFirst().orElseThrow(() -> new IllegalArgumentException(
                        String.format(ErrorMessage.ERROR_END_SECTION_NOT_FOUND, station.name()))
                );
    }

    private boolean isEndStation(Station station) {
        return isFirstStation(station) || isLastStation(station);
    }

    private boolean isFirstStation(Station station) {
        List<Station> sortedStations = getSortedStations();
        return !sortedStations.isEmpty() && sortedStations.get(0).equals(station);
    }

    private boolean isLastStation(Station station) {
        List<Station> sortedStations = getSortedStations();
        return !sortedStations.isEmpty() && sortedStations.get(sortedStations.size() - 1).equals(station);
    }
}
