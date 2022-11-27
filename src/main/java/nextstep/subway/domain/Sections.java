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

    public boolean isFirst(Station station) {
        return sections.stream()
                .filter(section -> section.getUpStation() == null)
                .anyMatch(section -> section.getDownStation().equals(station));
    }

    public boolean isLast(Station station) {
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
        if (isFirst(downStation)) {
            sections.remove(existingSection);
            return new Section(null, newStation, new Distance(0, true));
        }
        sections.remove(existingSection);
        return new Section(existingSection.getUpStation(), newStation,
                existingSection.getDistance().compareTo(distance));
    }

    private Section getNewLowerSection(Station upStation, Station newStation, Distance distance) {
        Section existingSection = sections.stream()
                .filter(section -> upStation.equals(section.getUpStation()))
                .findFirst().get();
        if (isLast(upStation)) {
            sections.remove(existingSection);
            return new Section(newStation, null, new Distance(0, true));
        }
        sections.remove(existingSection);
        return new Section(newStation, existingSection.getDownStation(),
                existingSection.getDistance().compareTo(distance));
    }

    public List<Section> init(Section section) {
        sections.add(section);
        sections.add(new Section(null, section.getUpStation(), new Distance(0, true)));
        sections.add(new Section(section.getDownStation(), null, new Distance(0, true)));
        return sections;
    }

}
