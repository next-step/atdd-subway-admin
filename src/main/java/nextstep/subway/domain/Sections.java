package nextstep.subway.domain;

import com.google.common.collect.Lists;
import nextstep.subway.exception.CannotAddSectionException;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.*;
import java.util.stream.Collectors;

import static nextstep.subway.exception.CannotAddSectionException.NO_MATCHED_STATION;
import static nextstep.subway.exception.CannotAddSectionException.UP_AND_DOWN_STATION_ALL_EXISTS;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Section> sectionList = new LinkedList<>();

    protected Sections() {
    }

    public Sections(Section... sections) {
        sectionList.addAll(Lists.newArrayList(sections));
    }

    public void addSection(Line line, Station upStation, Station downStation, Distance distance) {
        if (sectionList.isEmpty()) {
            sectionList.add(new Section(line, upStation, downStation, distance));
            return;
        }
        validate(upStation, downStation);

        List<Section> splitSections = splitSection(upStation, downStation, distance);
        sectionList.clear();
        sectionList.addAll(splitSections);
    }

    public void validate(Station upStation, Station downStation) {
        boolean isDownStationExistsOnUpStation = isUpStationExists(downStation);
        boolean isUpStationExistsOnDownStation = isDownStationExists(upStation);

        boolean isUpStationExists = isUpStationExists(upStation);
        boolean isDownStationExists = isDownStationExists(downStation);

        if (canAddOnLastUpStation(isDownStationExistsOnUpStation, isUpStationExists) ||
                canAddOnLastDownStation(isUpStationExistsOnDownStation, isDownStationExists)) {
            return;
        }
        if (!isUpStationExists && !isDownStationExists) {
            throw new CannotAddSectionException(NO_MATCHED_STATION);
        }
        if (isUpStationExists && isDownStationExists) {
            throw new CannotAddSectionException(UP_AND_DOWN_STATION_ALL_EXISTS);
        }
    }

    private static boolean canAddOnLastUpStation(boolean isDownStationExistsOnUpStation, boolean isUpStationExsists) {
        return isDownStationExistsOnUpStation && !isUpStationExsists;
    }

    private static boolean canAddOnLastDownStation(boolean isUpStationExistsOnDownStation, boolean isDownStationExists) {
        return isUpStationExistsOnDownStation && !isDownStationExists;
    }

    private boolean isUpStationExists(Station upStation) {
        return sectionList.stream().anyMatch(section -> section.isUpStation(upStation));
    }
    private boolean isDownStationExists(Station downStation) {
        return sectionList.stream().anyMatch(section -> section.isDownStation(downStation));
    }

    private List<Section> splitSection(Station upStation, Station downStation, Distance distance) {
        return sectionList.stream()
                .map(section -> section.splitSection(upStation, downStation, distance))
                .flatMap(Collection::stream)
                .distinct()
                .collect(Collectors.toList());
    }

    public Stations getStations() {
        if (sectionList.isEmpty()) {
            return Stations.EMPTY;
        }
        Deque<Section> sections = new ArrayDeque<>(sectionList);
        Deque<Station> orderedStations = new ArrayDeque<>();
        Section firstSection = sections.poll();
        orderedStations.add(firstSection.getUpStation());
        orderedStations.add(firstSection.getDownStation());

        return new Stations(new ArrayList<>(getOrderedStations(sections, orderedStations)));
    }

    private Deque<Station> getOrderedStations(Deque<Section> sections, Deque<Station> orderedStations) {
        if (sections.isEmpty()) {
            return orderedStations;
        }
        Section section = sections.pollFirst();
        Station upStation = section.getUpStation();
        Station downStation = section.getDownStation();

        if (orderedStations.getFirst().equals(downStation)) {
            orderedStations.addFirst(upStation);
            return getOrderedStations(sections, orderedStations);
        }
        if (orderedStations.getLast().equals(upStation)) {
            orderedStations.addLast(downStation);
            return getOrderedStations(sections, orderedStations);
        }

        sections.addLast(section);
        return getOrderedStations(sections, orderedStations);
    }

    public void removeStation(Station station) {
        Section upStationSection = findUpStationSections(station);
        Section downStationSection = findDownStationSections(station);
        collapseSections(upStationSection, downStationSection);
    }

    private void collapseSections(Section upStationSection, Section downStationSection) {

        Station upStation = downStationSection.getUpStation();
        Station downStation = upStationSection.getDownStation();

        Section section = new Section(upStationSection.getLine(),
                upStation,
                downStation,
                upStationSection.addDistance(downStationSection));

        this.sectionList.add(section);
        removeSections(upStationSection, downStationSection);
    }

    private void removeSections(Section ...sections) {
        Arrays.stream(sections)
                .forEach(section -> sectionList.remove(section));
    }

    private Section findUpStationSections(Station upStation) {
        return sectionList.stream()
                .filter(section -> section.isUpStation(upStation))
                .findFirst().orElseThrow(NullPointerException::new);
    }

    private Section findDownStationSections(Station downStation) {
        return sectionList.stream()
                .filter(section -> section.isDownStation(downStation))
                .findFirst().orElseThrow(NullPointerException::new);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Sections that = (Sections) o;
        return sectionList.equals(that.sectionList);
    }
    @Override
    public int hashCode() {
        return Objects.hash(sectionList);
    }

    @Override
    public String toString() {
        return "LineStations{" +
                "lineStationList=" + sectionList +
                '}';
    }
}
