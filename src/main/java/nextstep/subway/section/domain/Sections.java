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
            section.updateWith(sectionToAdd);
        }
    }

    public List<Station> getSortedStations() {
        sortSections();
        return getStations();
    }

    private List<Station> getStations(){
        List<Station> stations = sections.stream().map(Section::getUpStation).collect(Collectors.toList());
        if(!stations.isEmpty()) {
            addLastDownStation(stations);
        }
        return stations;
    }

    private boolean addLastDownStation(List<Station> stations) {
        return stations.add(sections.get(sections.size() - 1).getDownStation());
    }

    private boolean hasStation(Station station){
        return getStations().contains(station);
    }

    private void sortSections() {
        sections.sort((Section section1, Section section2) -> {
            if (section1.getDownStation().equals(section2.getUpStation())) {
                return -1;
            }
            return 1;
        });
    }
}
