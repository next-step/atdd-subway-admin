package nextstep.subway.section;

import nextstep.subway.station.Station;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.*;
import java.util.stream.Collectors;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", cascade = { CascadeType.PERSIST, CascadeType.REMOVE })
    private final List<Section> sections = new ArrayList<>();

    public Sections() { }

    public List<Map<String, Object>> getStationsResponse() {
        return getStations().stream()
                .map(Station::toMapForOpen)
                .collect(Collectors.toList());
    }

    private Set<Station> getStations() {
        sort();
        Set<Station> stations = new LinkedHashSet<>();
        for (Section section : sections) {
            stations.add(section.getUpStation());
            stations.add(section.getDownStation());
        }
        return stations;
    }

    private void sort() {
        sections.sort(Comparator.comparingInt(Section::getSequence));
    }

    public void add(Section section) {
        if (addFirstSection(section)) { return; }
        validateStationName(section);
        sort();
        if (extendDownSection(section)) { return; }
        if (extendUpSection(section)) { return; }
        if (sliceDownSection(section)) { return; }
        if (sliceUpSection(section)) { return; }
        throw new IllegalArgumentException("구간 등록이 불가능한 알 수 없는 오류입니다.");
    }

    private boolean addFirstSection(Section section) {
        if (sections.size() == 0) {
            sections.add(section);
            return true;
        }
        return false;
    }

    private boolean extendDownSection(Section section) {
        int sectionLength = sections.size();
        if (sections.get(sectionLength - 1).isExtendDownStation(section)) {
            section.increaseSequence(sectionLength);
            sections.add(section);
            return true;
        }
        return false;
    }

    private boolean extendUpSection(Section section) {
        if (sections.get(0).isExtendUpStation(section)) {
            sections.forEach(Section::increaseSequence);
            sections.add(section);
            return true;
        }
        return false;
    }

    private boolean sliceDownSection(Section section) {
        Section downSection = findSectionEqualUpStation(section);
        if (downSection == null) {
            return false;
        }
        Distance distance = new Distance(section.getDistance());
        int startIndex = sections.indexOf(downSection);
        sliceDownSection(section, distance, startIndex);
        pushSequenceFromSlice(section);
        sections.add(section);
        return true;
    }

    private void sliceDownSection(Section newSection, Distance distance, int startIndex) {
        validateDownSectionDistance(distance, startIndex);
        Section slice = null;
        while (slice == null) {
            Section targetSection = sections.get(startIndex);
            distance.subtract(targetSection.getDistance());
            slice = sliceDownSection(newSection, targetSection, distance);
            startIndex++;
        }
    }

    private Section sliceDownSection(Section newSection, Section targetSection, Distance distance) {
        if (distance.isNegative()) {
            newSection.syncUpStation(targetSection, distance);
            return newSection;
        }
        return null;
    }

    private Section findSectionEqualUpStation(Section section) {
        return sections.stream()
                .filter(child -> child.isEqualUpStation(section))
                .findFirst()
                .orElse(null);
    }

    private boolean sliceUpSection(Section section) {
        Section upSection = findSectionEqualDownStation(section);
        if (upSection == null) {
            return false;
        }
        Distance distance = new Distance(section.getDistance());
        sliceUpSection(section, distance, sections.indexOf(upSection));
        pushSequenceFromSlice(section);
        sections.add(section);
        return true;
    }

    private void sliceUpSection(Section newSection, Distance distance, int endIndex) {
        validateUpSectionDistance(distance, endIndex);
        Section slice = null;
        while (slice == null) {
            Section targetSection = sections.get(endIndex);
            distance.subtract(targetSection.getDistance());
            slice = sliceUpSection(newSection, targetSection, distance);
            endIndex--;
        }
    }

    private Section sliceUpSection(Section newSection, Section targetSection, Distance distance) {
        if (distance.isNegative()) {
            newSection.syncDownStation(targetSection, distance);
            return newSection;
        }
        return null;
    }

    private Section findSectionEqualDownStation(Section section) {
        return sections.stream()
                .filter(child -> child.isEqualDownStation(section))
                .findFirst()
                .orElse(null);
    }

    private void pushSequenceFromSlice(Section from) {
        for (int index = from.getSequence() - 1; index < sections.size(); index++) {
            sections.get(index)
                    .increaseSequence();
        }
    }

    private void validateStationName(Section section) {
        Set<Station> stations = getStations();
        Station upStation = section.getUpStation();
        Station downStation = section.getDownStation();
        if (stations.contains(upStation) && stations.contains(downStation)) {
            throw new IllegalArgumentException("입력한 구간의 역은 모두 이미 등록되었습니다.");
        }
        if (!stations.contains(upStation) && !stations.contains(downStation)) {
            throw new IllegalArgumentException("입력한 구간의 두 역 중 하나 이상은 등록되어 있어야 합니다.");
        }
    }

    private void validateDownSectionDistance(Distance distance, int startIndex) {
        Distance totalDistance = new Distance(0L);
        for (int index = startIndex; index < sections.size(); index++) {
            totalDistance.add(sections.get(index).getDistance());
        }
        validateDistance(distance, totalDistance);
    }

    private void validateUpSectionDistance(Distance distance, int endIndex) {
        Distance totalDistance = new Distance(0L);
        for (int index = 0; index <= endIndex; index++) {
            totalDistance.add(sections.get(index).getDistance());
        }
        validateDistance(distance, totalDistance);
    }

    private void validateDistance(Distance distance, Distance totalDistance) {
        if (distance.compare(totalDistance) == 0) {
            throw new IllegalArgumentException("해당 거리에 이미 역이 등록되었습니다.");
        }
        if (distance.compare(totalDistance) == 1) {
            throw new IllegalArgumentException("입력한 역의 길이가 종점까지의 거리를 초과했습니다.");
        }
    }

    @Override
    public String toString() {
        sort();
        return sections.stream()
                .map(Section::toString)
                .collect(Collectors.joining("\n"));
    }
}
