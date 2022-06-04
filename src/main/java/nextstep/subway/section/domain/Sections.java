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
    public static final int MINIMUM_SECTION_NUMBER = 1;

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
        validateAtLeastOneStationIsNew(section);
        validateAtLeastOneStationIsRegistered(section);
    }

    private void validateAtLeastOneStationIsNew(Section section) {
        if (hasStation(section.getUpStation()) && hasStation(section.getDownStation())) {
            throw new IllegalArgumentException(
                    String.format(ErrorMessage.ERROR_SECTION_ALREADY_REGISTERED_STATIONS,
                            section.getUpStation().getName(), section.getDownStation().getName())
            );
        }
    }

    private void validateAtLeastOneStationIsRegistered(Section section) {
        if (!hasStation(section.getUpStation()) && !hasStation(section.getDownStation())) {
            throw new IllegalArgumentException(
                    String.format(ErrorMessage.ERROR_SECTION_UNKNOWN_STATIONS,
                            section.getUpStation().getName(), section.getDownStation().getName())
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
        List<Station> stations = sections.stream().map(Section::getUpStation).collect(Collectors.toList());
        if(!stations.isEmpty()) {
            stations.add(getLastDownStation());
        }
        return stations;
    }

    private Station getLastDownStation() {
        return sections.get(sections.size() - 1).getDownStation();
    }

    private boolean hasStation(Station station){
        return getSortedStations().contains(station);
    }

    private void sortSections() {
        sections.sort((Section section1, Section section2) -> {
            if (section1.getDownStation().equals(section2.getUpStation())) {
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
        if (sections.size() <= MINIMUM_SECTION_NUMBER) {
            throw new IllegalArgumentException(
                    String.format("[ERROR] 구간이 %d개 이하일 때는 제거할 수 없습니다.", MINIMUM_SECTION_NUMBER)
            );
        }
        if (!hasStation(station)) {
            throw new IllegalArgumentException(
                    "[ERROR] 노선에 존재하지 않는 Station %s은 제거할 수 없습니다."
            );
        }
    }

    private void removeEndStation(Station station) {
        sections.remove(getEndSection(station));
    }

    private void removeMiddleStation(Station station) {
        Section previousSection = getPreviousSection(station);
        Section postSection = getPostSection(station);
        previousSection.mergeWith(postSection);
        sections.remove(postSection);
    }

    private Section getPreviousSection(Station station) {
        return sections.stream()
                .filter(section -> section.getDownStation().equals(station))
                .findFirst().orElseThrow(() ->
                        new IllegalArgumentException("[ERROR] Station %s의 이전 구간을 찾을 수 없습니다."));
    }

    private Section getPostSection(Station station) {
        return sections.stream()
                .filter(section -> section.getUpStation().equals(station))
                .findFirst().orElseThrow(() ->
                        new IllegalArgumentException("[ERROR] Station %s의 이후 구간을 찾을 수 없습니다."));
    }

    private Section getEndSection(Station station) {
        return sections.stream()
                .filter(section -> section.getDownStation().equals(station) || section.getUpStation().equals(station))
                .findFirst().orElseThrow(() ->
                        new IllegalArgumentException("[ERROR] Station %s의 종점 구간을 찾을 수 없습니다."));
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
