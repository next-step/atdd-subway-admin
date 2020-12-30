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

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class Sections {
    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL)
    private List<Section> sections = new ArrayList<>();

    public void add(Section section) {
        validation(section);
        addUptoUpStation(section);
        addDownToDownStation(section);
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

    private void addUptoUpStation(Section section) {
        sections.stream()
                .filter(oldSection -> section.getUpStation() == oldSection.getUpStation())
                .findFirst()
                .ifPresent(oldSection -> {
                    sections.add(new Section(oldSection.getLine(), section.getDownStation(), oldSection.getDownStation(), getUpStationDistance(section, oldSection)));
                    sections.remove(oldSection);
                });
    }

    private void addDownToDownStation(Section section) {
        sections.stream()
                .filter(oldSection -> section.getUpStation() == oldSection.getDownStation())
                .findFirst()
                .ifPresent(oldSection -> {
                    sections.add(new Section(oldSection.getLine(), oldSection.getUpStation(), section.getUpStation(), section.getDistance().get()));
                    sections.remove(oldSection);
                });
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
                .orElseThrow(RuntimeException::new);
    }

    private boolean isContainsDownStation(Station station) {
        boolean flag = false;
        for (Section section : sections) {
            flag = section.getDownStation() == station;
        }
        return flag;
    }

    private Optional<Section> findNextSection(Station downStation) {
        return sections.stream()
                .filter(section -> section.getUpStation() == downStation)
                .findFirst();
    }

    private void validation(Section section) {
        if (sections.size() == 0) return;
        int containsCount = getContainsCount(section);
        stationExistValid(containsCount);
    }

    private int getContainsCount(Section section) {
        List<Station> stations = this.getStations();
        int containsCount = 0;
        for (Station station : section.getStations()) {
            if (stations.contains(station)) {
                containsCount++;
            }
        }
        return containsCount;
    }

    private void stationExistValid(int containsCount) {
        if (containsCount == 2) {
            throw new IllegalArgumentException("상행역과 하행역이 이미 노선에 모두 등록되어 있습니다.");
        }
        if (containsCount == 0) {
            throw new IllegalArgumentException("상행역과 하행역 중 노선에 등록된 역이 없습니다.");
        }
    }
}
