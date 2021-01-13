package nextstep.subway.line.domain;

import nextstep.subway.station.domain.Station;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Embeddable
public class Sections {
    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private final List<Section> sections = new ArrayList<>();

    public void add(Section section) {
        if (sections.isEmpty()) {
            sections.add(section);
            return;
        }

        validateSections(section);
        replaceDownStation(section);
        replaceUpStation(section);
        sections.add(section);
    }

    private void replaceUpStation(Section section) {
        sections.stream()
                .filter(existSection -> existSection.isSameUpStation(section.getUpStation()))
                .findFirst()
                .ifPresent(existSection -> existSection.replaceUpStation(section));
    }

    private void replaceDownStation(Section section) {
        sections.stream()
                .filter(existSection -> existSection.isSameDownStation(section.getDownStation()))
                .findFirst()
                .ifPresent(existSection -> existSection.replaceDownStation(section));
    }

    private void validateSections(Section section) {
        if (isNotContainUpStationAndDownStation(section, getStations())) {
            throw new IllegalArgumentException("상행역과 하행역 둘 중 하나도 포함되어 있지 않습니다.");
        }
    }

    private boolean isNotContainUpStationAndDownStation(Section section, List<Station> staions) {
        return !staions.contains(section.getUpStation()) && !staions.contains(section.getDownStation());
    }

    public List<Section> getSections() {
        return Collections.unmodifiableList(sections);
    }

    public List<Station> getStations() {
        Section section = sections.stream()
                .findFirst().orElseThrow(EntityNotFoundException::new);

        List<Station> result = findDownToUpStation(section);
        result.addAll(findUpToDownStation(section));
        return result;
    }

    private List<Station> findUpToDownStation(Section section) {
        List<Station> result = new ArrayList<>();
        while (section != null) {
            Station downStation = section.getDownStation();
            result.add(downStation);
            section = sections.stream()
                    .filter(existSection -> existSection.getUpStation() == downStation)
                    .findFirst().orElse(null);
        }

        return result;
    }

    private List<Station> findDownToUpStation(Section section) {
        List<Station> result = new ArrayList<>();
        while (section != null) {
            Station upStation = section.getUpStation();
            result.add(upStation);
            section = sections.stream()
                    .filter(existSection -> existSection.getDownStation() == upStation)
                    .findFirst().orElse(null);
        }
        Collections.reverse(result);
        return result;
    }

    public Section remove(Station station) {
        validateContainStation(station);
        validateMinSection();
        List<Section> targetSections = findSectionsByStation(station);
        if (isNotOneSection(targetSections)) {
            return removeStationBetweenSections(station);
        }

        sections.remove(targetSections.get(0));
        return targetSections.get(0);
    }

    private void validateMinSection() {
        if (sections.size() == 1) {
            throw new IllegalArgumentException("구간이 하나인 노선에서는 제거할 수 없다.");
        }
    }

    private boolean isNotOneSection(List<Section> sections) {
        return sections.size() > 1;
    }

    private List<Section> findSectionsByStation(Station station) {
        return sections.stream()
                .filter(existSection -> existSection.isSameDownStation(station) || existSection.isSameUpStation(station))
                .collect(Collectors.toList());
    }

    private Section removeStationBetweenSections(Station station) {
        Section removeSection = sections.stream()
                .filter(existSection -> existSection.isSameDownStation(station))
                .findFirst().orElseThrow(() -> new IllegalArgumentException("삭제할 역이 해당 노선에 없습니다."));

        replaceSectionByStation(station, removeSection);
        sections.remove(removeSection);
        return removeSection;
    }

    private void replaceSectionByStation(Station station, Section targetSection) {
        sections.stream()
                .filter(existSection -> existSection.isSameUpStation(station))
                .findFirst()
                .ifPresent(existSection -> existSection.replaceSection(targetSection));
    }

    private void validateContainStation(Station station) {
        if(!getStations().contains(station)) {
            throw new IllegalArgumentException("삭제할 역이 해당 노선에 없습니다.");
        }
    }
}
