package nextstep.subway.domain;

import com.google.common.collect.Lists;
import nextstep.subway.exception.CannotAddSectionException;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import static nextstep.subway.exception.CannotAddSectionException.NO_MATCHED_STATION;

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
        Section previousSection = getPreviousSection(upStation, downStation);
        List<Section> appendedSections = previousSection.addSection(upStation, downStation, distance);

        replaceSection(previousSection, appendedSections);
    }

    private Section getPreviousSection(Station upStation, Station downStation) {
        return sectionList.stream()
                .filter(section -> section.canAddSection(upStation, downStation))
                .findFirst()
                .orElseThrow(() -> new CannotAddSectionException(NO_MATCHED_STATION));
    }

    private void replaceSection(Section previousSection, List<Section> appendedSections) {
        int previousLineStationIndex = sectionList.indexOf(previousSection);
        sectionList.remove(previousLineStationIndex);
        sectionList.addAll(previousLineStationIndex, appendedSections);
    }

    public List<Station> getStations() {
        if (sectionList.isEmpty()) {
            return Collections.emptyList();
        }
        Deque<Section> sections = new ArrayDeque<>(sectionList);
        Deque<Station> orderedStations = new ArrayDeque<>();
        Section firstSection = sections.poll();
        orderedStations.add(firstSection.getUpStation());
        orderedStations.add(firstSection.getDownStation());

        return new ArrayList<>(getOrderedStations(sections, orderedStations));
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
