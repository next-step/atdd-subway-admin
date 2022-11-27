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
        sliceDownSection(section, distance, sections.indexOf(downSection));
        pushSequenceFromSlice(section);
        sections.add(section);
        return true;
    }

    private void sliceDownSection(Section newSection, Distance distance, int matchIndex) {
        Section slice = null;
        while (slice == null) {
            Section targetSection = sections.get(matchIndex);
            distance.subtract(targetSection.getDistance());
            slice = sliceDownSection(newSection, targetSection, distance);
            matchIndex++;
        }
        validateExceedSection(slice);
    }

    private Section sliceDownSection(Section newSection, Section targetSection, Distance distance) {
        validateLocatedStation(distance);
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

    private void sliceUpSection(Section newSection, Distance distance, int matchIndex) {
        Section slice = null;
        while (slice == null) {
            Section targetSection = sections.get(matchIndex);
            distance.subtract(targetSection.getDistance());
            slice = sliceUpSection(newSection, targetSection, distance);
            matchIndex--;
        }
        validateExceedSection(slice);
    }

    private Section sliceUpSection(Section newSection, Section targetSection, Distance distance) {
        validateLocatedStation(distance);
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
            throw new IllegalArgumentException("입력한 구간의 두 역 중 하나 이상은 이미 등록되어 있어야 합니다.");
        }
    }

    private void validateLocatedStation(Distance distance) {
        if (distance.isZero()) {
            throw new IllegalArgumentException("해당 거리에 이미 역이 등록되었습니다.");
        }
    }

    private void validateExceedSection(Section slice) {
        if (slice == null) {
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
