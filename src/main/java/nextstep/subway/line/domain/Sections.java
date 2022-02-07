package nextstep.subway.line.domain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import nextstep.subway.line.exception.SectionEmptyException;
import nextstep.subway.line.exception.SectionNotFoundException;
import nextstep.subway.line.exception.StationsNotExistInTheLine;
import nextstep.subway.station.domain.Station;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL)
    private List<Section> sections = new ArrayList<>();

    public Sections() {
    }

    private Sections(List<Section> sections) {
        this.sections = sections;
    }

    public static Sections of(Section... sections) {
        return new Sections(new ArrayList<>(Arrays.asList(sections)));
    }

    public void addSection(Section section) {
        checkSection(section);
        sections.stream()
            .forEach(s -> s.addInnerSection(section));
        sections.add(section);
    }

    private void checkSection(Section section) {
        checkStationsDuplicate(section);
        checkStationExist(section);
    }

    private void checkStationsDuplicate(Section section) {
        sections.stream()
            .forEach(s -> s.checkStationsDuplicate(section));
    }

    private void checkStationExist(Section section) {
        List<Station> stations = stations();
        if (!stations.contains(section.getUpStation()) &&
            !stations.contains(section.getDownStation())) {
            throw new StationsNotExistInTheLine("상행역과 하행역 둘중 하나는 노선에 존재해야 합니다.");
        }
    }

    public void deleteStation(Station station) {
        checkStationRemovable(station);
        if (isInnerStation(stations(), station)) {
            Section upSection = upSection(station);
            Section downSection = downSection(station);
            upSection.mergeSection(downSection);
        }
        deleteSection(station);
    }

    private Section upSection(Station station) {
        return sections.stream()
            .filter(section -> section.isUpSection(station))
            .findFirst()
            .orElseThrow(() -> new SectionNotFoundException("노선에 역이 포함되지 않습니다."));
    }

    private Section downSection(Station station) {
        return sections.stream()
            .filter(section -> section.isDownSection(station))
            .findFirst()
            .orElseThrow(() -> new SectionNotFoundException("노선에 역이 포함되지 않습니다."));
    }

    private void deleteSection(Station station) {
        this.sections = sections.stream()
            .filter(section -> !section.hasStation(station))
            .collect(Collectors.toList());
    }

    private boolean isInnerStation(List<Station> stations, Station station) {
        return !isFirstStation(stations, station) && !isLastStation(stations, station);
    }

    private boolean isFirstStation(List<Station> stations, Station station) {
        return stations.get(0).equals(station);
    }

    private boolean isLastStation(List<Station> stations, Station station) {
        return stations.get(stations.size() - 1).equals(station);
    }

    private void checkStationRemovable(Station station) {
        if (sections.size() == 1 && sections.get(0).hasStation(station)) {
            throw new SectionEmptyException("노선은 두개의 역을 포함한 구간이 하나 이상 존재해야 합니다.");
        }
    }

    public List<Section> orderedSections() {
        List<Section> orderedSections = new ArrayList();
        Section now = findFirstSection(sections.get(0));
        orderedSections.add(now);
        while (orderedSections.size() != sections.size()) {
            Section next = findDownSection(now);
            orderedSections.add(next);
            now = next;
        }
        return Collections.unmodifiableList(orderedSections);
    }

    private Section findFirstSection(Section now) {
        if (isFirstSection(now)) {
            return now;
        }
        return findFirstSection(findUpSection(now));
    }

    private Section findUpSection(Section now) {
        return sections.stream()
            .filter(section -> section.isUp(now))
            .findFirst()
            .orElse(now);
    }

    private Section findDownSection(Section now) {
        return sections.stream()
            .filter(section -> section.isDown(now))
            .findFirst()
            .orElse(now);
    }

    private boolean isFirstSection(Section now) {
        Section upSection = findUpSection(now);
        return upSection.equals(now);
    }

    public List<Station> stations() {
        List<Section> orderedSections = orderedSections();
        List<Station> stations = orderedSections.stream()
            .map(Section::getUpStation)
            .collect(Collectors.toList());
        stations.add(orderedSections.get(orderedSections.size() - 1).getDownStation());
        return stations;
    }

    public List<Section> getSections() {
        return Collections.unmodifiableList(sections);
    }
}
