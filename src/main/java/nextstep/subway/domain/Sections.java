package nextstep.subway.domain;

import com.google.common.collect.Lists;
import nextstep.subway.exception.CannotAddSectionException;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    private List<Section> getOrderedSections() {
        if (sectionList.isEmpty()) {
            return Collections.emptyList();
        }

        Deque<Section> sections = new ArrayDeque<>(sectionList);
        Deque<Section> orderedSections = new ArrayDeque<>();
        Section firstSection = sections.poll();
        orderedSections.add(firstSection);
        return getOrderedSections(sections, orderedSections);
    }

    private List<Section> getOrderedSections(Deque<Section> sections, Deque<Section> orderedSections) {
        if (sections.isEmpty()) {
            return new ArrayList<>(orderedSections);
        }
        Section section = sections.poll();
        Section lastUpSection = orderedSections.getFirst();
        Section lastDownSection = orderedSections.getLast();

        if (lastUpSection.isUpSection(section)) {
            orderedSections.addFirst(section);
            return getOrderedSections(sections, orderedSections);
        }
        if (lastDownSection.isDownSection(section)) {
            orderedSections.addLast(section);
            return getOrderedSections(sections, orderedSections);
        }
        sections.addLast(section);
        return getOrderedSections(sections, orderedSections);
    }

    public Stations getStations() {
        if (sectionList.isEmpty()) {
            return Stations.EMPTY;
        }
        List<Station> orderedStations = Stream.concat(
                        Stream.of(getOrderedSections().get(0).getUpStation()),
                        getOrderedSections()
                                .stream()
                                .map(Section::getDownStation))
                        .collect(Collectors.toList());

        return new Stations(orderedStations);
    }

    public void removeStation(Station station) {
        sortSections();

        if (isLastUpStation(station)) {
            removeLastUpSection();
            return;
        }
        if (isLastDownStation(station)) {
            removeLastDownStation();
            return;
        }

        Section upStationSection = findUpStationSections(station);
        Section downStationSection = findDownStationSections(station);
        collapseSections(upStationSection, downStationSection);
    }

    private void sortSections() {
        List<Section> sections = getOrderedSections();
        this.sectionList.clear();
        this.sectionList.addAll(sections);
    }

    private void removeLastDownStation() {
        sectionList.remove(sectionList.size()-1);
    }

    private void removeLastUpSection() {
        sectionList.remove(sectionList.get(0));
    }

    private boolean isLastDownStation(Station station) {
        return sectionList.get(sectionList.size()-1).isDownStation(station);
    }
    private boolean isLastUpStation(Station station) {
        return sectionList.get(0).isUpStation(station);
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
        return "Sections{" +
                "sectionList=" + sectionList +
                '}';
    }
}
