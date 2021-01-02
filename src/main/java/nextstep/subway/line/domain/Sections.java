package nextstep.subway.line.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nextstep.subway.station.domain.Station;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class Sections {
    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL)
    private List<Section> sections = new ArrayList<>();

    public void add(Section section) {
        validateSection(section);
        if (isUpToUpSectionCase(section)) {
            addUpToUpSection(section);
        }
        if (isDownToDownSectionCase(section)) {
            addDownToDownSection(section);
        }
        sections.add(section);
    }

    public List<Station> getStations() {
        Section startSection = findStartSection();
        List<Station> stations = startSection.getStations();
        Optional<Section> nextSection = findNextSection(startSection.getDownStation());
        while (nextSection.isPresent()) {
            stations.add(nextSection.get().getDownStation());
            nextSection = findNextSection(nextSection.get().getDownStation());
        }
        return stations;
    }

    public void remove(Section section) {
        sections.remove(section);
    }

    private boolean isDownToDownSectionCase(Section section) {
        return sections.stream()
                .map(Section::getDownStation)
                .collect(Collectors.toList())
                .contains(section.getUpStation());
    }

    private boolean isUpToUpSectionCase(Section section) {
        return sections.stream()
                .map(Section::getUpStation)
                .collect(Collectors.toList())
                .contains(section.getUpStation());
    }

    private void addUpToUpSection(Section section) {
        Section oldSection = findUpToUpSection(section);
        sections.add(new Section(oldSection.getLine(), section.getDownStation(), oldSection.getDownStation(), getUpStationDistance(section, oldSection)));
        remove(oldSection);
    }

    private void addDownToDownSection(Section section) {
        Section oldSection = findDownToDownSection(section);
        sections.add(new Section(oldSection.getLine(), oldSection.getUpStation(), section.getUpStation(), section.getDistance().get()));
        remove(oldSection);
    }

    private Section findUpToUpSection(Section section) {
        return sections.stream()
                .filter(oldSection -> isSameStation(oldSection.getUpStation(), section.getUpStation()))
                .findFirst().orElseThrow(() -> new IllegalArgumentException("등록하려는 구간의 상행역과 같은 상행역을 가진 구간이 없습니다."));
    }

    private Section findDownToDownSection(Section section) {
        return sections.stream()
                .filter(oldSection -> isSameStation(oldSection.getDownStation(), section.getUpStation()))
                .findFirst().orElseThrow(() -> new IllegalArgumentException("등록하려는 구간의 상행역과 같은 하행역을 가진 구간이 없습니다."));
    }

    private long getUpStationDistance(Section section, Section oldSection) {
        long nextDistance = oldSection.getDistance().get() - section.getDistance().get();
        if (nextDistance < 1) {
            throw new IllegalArgumentException("기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없음");
        }
        return nextDistance;
    }

    private Section findStartSection() {
        return sections.stream()
                .filter(s -> !isContainsDownStation(s.getUpStation()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("구간의 첫번째 역을 찾을 수 없습니다."));
    }

    private boolean isContainsDownStation(Station station) {
        return this.sections.stream()
                .anyMatch((section) -> section.getDownStation() == station);
    }

    private boolean isSameStation(Station station, Station downStation) {
        return downStation == station;
    }

    private Optional<Section> findNextSection(Station downStation) {
        return sections.stream()
                .filter(section -> isSameStation(downStation, section.getUpStation()))
                .findFirst();
    }

    private void validateSection(Section section) {
        if (sections.size() == 0) return;
        int containsCount = getContainsStationCount(section);
        validateStationExist(containsCount);
    }

    private int getContainsStationCount(Section section) {
        List<Station> stations = this.getStations();
        int containsCount = 0;
        for (Station station : section.getStations()) {
            if (stations.contains(station)) {
                containsCount++;
            }
        }
        return containsCount;
    }

    private void validateStationExist(int containsCount) {
        if (containsCount == 2) {
            throw new IllegalArgumentException("상행역과 하행역이 이미 노선에 모두 등록되어 있습니다.");
        }
        if (containsCount == 0) {
            throw new IllegalArgumentException("상행역과 하행역 중 노선에 등록된 역이 없습니다.");
        }
    }
}
