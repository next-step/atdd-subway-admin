package nextstep.subway.line.domain;

import nextstep.subway.station.domain.Station;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
                .filter(existSection -> existSection.isSameUpStation(section))
                .findFirst()
                .ifPresent(existSection -> existSection.replaceUpStation(section));
    }

    private void replaceDownStation(Section section) {
        sections.stream()
                .filter(existSection -> existSection.isSameDownStation(section))
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
}
