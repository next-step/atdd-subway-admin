package nextstep.subway.domain;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
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
        return isFirstStationById(station.getId());
    }

    public boolean isFirstStationById(Long stationId) {
        return sections.stream()
                .filter(section -> section.getUpStation() == null)
                .map(section -> section.getDownStation().getId())
                .anyMatch(stationId::equals);
    }

    public boolean isLastStation(Station station) {
        return isLastStationById(station.getId());
    }

    public boolean isLastStationById(Long stationId) {
        return sections.stream()
                .filter(section -> section.getDownStation() == null)
                .map(section -> section.getUpStation().getId())
                .anyMatch(stationId::equals);
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
            return getNewUpperSections(requestUpStation, requestDownStation, distance);
        }
        return getNewLowerSections(requestUpStation, requestDownStation, distance);
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

    private List<Section> getNewUpperSections(Station requestUpStation, Station requestDownStation, Distance distance) {
        List<Section> sections = new ArrayList<>();
        Section existingSection = this.sections.stream()
                .filter(section -> requestDownStation.equals(section.getDownStation()))
                .findFirst().get();
        int addIndex = this.sections.indexOf(existingSection);
        if (isFirstStation(requestDownStation)) {
            this.sections.remove(existingSection);
            sections.add(new Section(null, requestUpStation, Distance.getTerminalSectionDistance()));
            sections.add(new Section(requestUpStation, requestDownStation, distance));
            this.sections.addAll(addIndex, sections);
            return sections;
        }
        this.sections.remove(existingSection);
        sections.add(new Section(existingSection.getUpStation(), requestUpStation,
                existingSection.getDistance().subtract(distance)));
        sections.add(new Section(requestUpStation, requestDownStation, distance));
        this.sections.addAll(addIndex, sections);
        return sections;
    }

    private List<Section> getNewLowerSections(Station requestUpStation, Station requestDownStation, Distance distance) {
        List<Section> sections = new ArrayList<>();
        Section existingSection = this.sections.stream()
                .filter(section -> requestUpStation.equals(section.getUpStation()))
                .findFirst().get();
        int addIndex = this.sections.indexOf(existingSection);
        if (isLastStation(requestUpStation)) {
            this.sections.remove(existingSection);
            sections.add(new Section(requestUpStation, requestDownStation, distance));
            sections.add(new Section(requestDownStation, null, Distance.getTerminalSectionDistance()));
            this.sections.addAll(addIndex, sections);
            return sections;
        }
        this.sections.remove(existingSection);
        sections.add(new Section(requestUpStation, requestDownStation, distance));
        sections.add(new Section(requestDownStation, existingSection.getDownStation(),
                existingSection.getDistance().subtract(distance)));
        this.sections.addAll(addIndex, sections);
        return sections;
    }

    public List<Section> init(Section section) {
        sections.add(new Section(null, section.getUpStation(), Distance.getTerminalSectionDistance()));
        sections.add(section);
        sections.add(new Section(section.getDownStation(), null, Distance.getTerminalSectionDistance()));
        return sections;
    }

    public Section removeSectionByStationAndGetNewSection(Long stationId) {
        checkStationExist(stationId);
        checkOnlyOneSection();

        if (isFirstStationById(stationId)) {
            return removeFirstSection(stationId);
        }

        if (isLastStationById(stationId)) {
            return removeLastSection(stationId);
        }

        return removeNonTerminalSection(stationId);
    }

    public void checkStationExist(Long stationId) {
        if (!getStations().stream().map(Station::getId).anyMatch(stationId::equals)) {
            throw new IllegalArgumentException(ErrorMessage.LINE_NOT_CONTAIN_STATION.getMessage());
        }
    }

    public void checkOnlyOneSection() {
        if (sections.size() == 3) {
            throw new IllegalArgumentException(ErrorMessage.ONLY_ONE_SECTION.getMessage());
        }
    }

    private Section removeFirstSection(Long stationId) {
        sections.remove(sections.stream()
                .filter(section -> section.getUpStation() == null)
                .findFirst().get());

        return sections.stream()
                .filter(section -> section.getUpStation() != null &&
                        section.getUpStation().getId().equals(stationId))
                .findFirst().get()
                .deleteUpStation();
    }

    private Section removeLastSection(Long stationId) {
        sections.remove(sections.stream()
                .filter(section -> section.getDownStation() == null)
                .findFirst().get());

        return sections.stream()
                .filter(section -> section.getDownStation() != null &&
                        section.getDownStation().getId().equals(stationId))
                .findFirst().get()
                .deleteDownStation();
    }

    private Section removeNonTerminalSection(Long stationId) {
        Section upperSection = sections.stream()
                .filter(section -> section.getDownStation() != null &&
                        section.getDownStation().getId().equals(stationId))
                .findFirst().get();
        Section lowerSection = sections.stream()
                .filter(section -> section.getUpStation() != null &&
                        section.getUpStation().getId().equals(stationId))
                .findFirst().get();
        Section newSection = new Section(upperSection.getUpStation(), lowerSection.getDownStation(),
                upperSection.getDistance().add(lowerSection.getDistance()));

        int addIndex = sections.indexOf(upperSection);
        sections.remove(upperSection);
        sections.remove(lowerSection);
        sections.add(addIndex, newSection);
        return newSection;
    }
}
