package nextstep.subway.domain;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private List<Section> sections;

    public Sections(List<Section> sections) {
        this.sections = sections;
    }

    protected Sections() {

    }

    public List<Section> getSections() {
        return sections;
    }

    public boolean isFirstStation(Station station) {
        return sections.stream()
                .filter(section -> section.getUpStation() == null)
                .anyMatch(section -> section.getDownStation().equals(station));
    }

    public boolean isLastStation(Station station) {
        return sections.stream()
                .filter(section -> section.getDownStation() == null)
                .anyMatch(section -> section.getUpStation().equals(station));
    }

    public boolean isContainStation(Station station) {
        return getStations().contains(station);
    }

    public List<Station> getStations() {
        List<Station> stations = new ArrayList<>();

        Station firstStation = sections.stream().filter(section -> section.getUpStation() == null)
                .findFirst().get().getDownStation();
        Station nextStation = firstStation;

        while(nextStation != null) {
            stations.add(nextStation);
            Station finalNextStation = nextStation;
            nextStation = sections.stream().filter(section ->
                            section.getUpStation() != null && section.getUpStation().equals(finalNextStation))
                    .findFirst().get().getDownStation();
        }
        return stations;
    }

    public List<Section> addAndGetSections(Station requestUpStation, Station requestDownStation, Distance distance) {
        checkRequestSectionContainsValidStations(requestUpStation, requestDownStation);
        if (isNewStation(requestUpStation)) {
            List<Section> sections = Arrays.asList(getNewUpperSection(requestUpStation, requestDownStation, distance),
                    new Section(requestUpStation, requestDownStation, distance));
            this.sections.addAll(sections);
            return sections;
        }
        List<Section> sections = Arrays.asList(new Section(requestUpStation, requestDownStation, distance),
                getNewLowerSection(requestUpStation, requestDownStation, distance));
        this.sections.addAll(sections);
        return sections;
    }

    private void checkRequestSectionContainsValidStations(Station requestUpStation, Station requestDownStation) {
        boolean isContainUpStation = this.isContainStation(requestUpStation);
        boolean isContainDownStation = this.isContainStation(requestDownStation);
        if (isContainUpStation && isContainDownStation) {
            throw new IllegalArgumentException(ErrorMessage.ALREADY_EXIST_SECTION.getMessage());
        }
        if (!isContainUpStation && !isContainDownStation) {
            throw new IllegalArgumentException(ErrorMessage.NO_EXIST_STATIONS.getMessage());
        }
    }

    private boolean isNewStation(Station station) {
        return !this.isContainStation(station);
    }

    private Section getNewUpperSection(Station newStation, Station downStation, Distance distance) {
        Section existingSection = sections.stream()
                .filter(section -> downStation.equals(section.getDownStation()))
                .findFirst().get();
        if (isFirstStation(downStation)) {
            sections.remove(existingSection);
            return new Section(null, newStation, Distance.getTerminalSectionDistance());
        }
        sections.remove(existingSection);
        return new Section(existingSection.getUpStation(), newStation,
                existingSection.getDistance().subtract(distance));
    }

    private Section getNewLowerSection(Station upStation, Station newStation, Distance distance) {
        Section existingSection = sections.stream()
                .filter(section -> upStation.equals(section.getUpStation()))
                .findFirst().get();
        if (isLastStation(upStation)) {
            sections.remove(existingSection);
            return new Section(newStation, null, Distance.getTerminalSectionDistance());
        }
        sections.remove(existingSection);
        return new Section(newStation, existingSection.getDownStation(),
                existingSection.getDistance().subtract(distance));
    }

    public List<Section> init(Section section) {
        sections.add(section);
        sections.add(new Section(null, section.getUpStation(), Distance.getTerminalSectionDistance()));
        sections.add(new Section(section.getDownStation(), null, Distance.getTerminalSectionDistance()));
        return sections;
    }

    public Section removeSectionByStationAndGetNewSection(Station station) {
        if (sections.size() == 3) {
            throw new IllegalArgumentException(ErrorMessage.ONLY_ONE_SECTION.getMessage());
        }

        if (isFirstStation(station)) {
            sections.remove(0);
            return sections.get(0).deleteUpStation();
        }

        if (isLastStation(station)) {
            sections.remove(sections.size()-1);
            return sections.get(sections.size()-1).deleteDownStation();
        }

        Section upperSection = sections.stream()
                .filter(section -> section.getDownStation() != null &&
                        section.getDownStation().equals(station))
                .findFirst().get();
        Section lowerSection = sections.stream()
                .filter(section -> section.getUpStation() != null &&
                        section.getUpStation().equals(station))
                .findFirst().get();
        Section newSection = new Section(upperSection.getUpStation(), lowerSection.getDownStation(),
                upperSection.getDistance().add(lowerSection.getDistance()));

        int index = sections.indexOf(upperSection);
        sections.remove(upperSection);
        sections.remove(lowerSection);
        sections.add(index, newSection);
        return newSection;
    }
}
