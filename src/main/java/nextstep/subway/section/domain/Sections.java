package nextstep.subway.section.domain;

import nextstep.subway.consts.ErrorMessage;
import nextstep.subway.station.domain.Station;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.List;
import java.util.stream.Collectors;

@Embeddable
public class Sections{
    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL, orphanRemoval = true)
    List<Section> sections;

    protected Sections() {
    }

    public Sections(List<Section> sections) {
        this.sections = sections;
    }

    public static Sections from(List<Section> sections) {
        return new Sections(sections);
    }

    public void addSection(Section section) {
        validateSectionToAdd(section);
        updateAdjacentSection(section);
        sections.add(section);
    }

    public List<Station> getSortedStations() {
        sortSections();
        return getStations();
    }

    private void validateSectionToAdd(Section section) {
        checkIfBothStationsAreAlreadyRegistered(section);
        checkIfBothStationsAreUnknown(section);
    }

    private void checkIfBothStationsAreAlreadyRegistered(Section section) {
        if (hasStation(section.getUpStation()) && hasStation(section.getDownStation())) {
            throw new IllegalArgumentException(
                    String.format(ErrorMessage.ERROR_SECTION_ALREADY_REGISTERED_STATIONS,
                            section.getUpStation().getName(), section.getDownStation().getName())
            );
        }
    }

    private void checkIfBothStationsAreUnknown(Section section) {
        if (!hasStation(section.getUpStation()) && !hasStation(section.getDownStation())) {
            throw new IllegalArgumentException(
                    String.format(ErrorMessage.ERROR_SECTION_UNKNOWN_STATIONS,
                            section.getUpStation().getName(), section.getDownStation().getName())
            );
        }
    }

    private void updateAdjacentSection(Section newSection) {
        for (Section section : sections) {
            section.updateWith(newSection);
        }
    }

    private List<Station> getStations(){
        List<Station> stations = sections.stream().map(Section::getUpStation).collect(Collectors.toList());
        stations.add(sections.get(sections.size() - 1).getDownStation());
        return stations;
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
