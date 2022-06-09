package nextstep.subway.domain;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Embeddable
public class Sections {

    @OneToMany(fetch = FetchType.LAZY,
            mappedBy = "line",
            cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    List<Section> sections = new ArrayList<>();

    public void add(Section section) {
        if (!sections.isEmpty()) {
            validateDuplicateSection(section);
            Section connectionSection = findConnectableSection(section);
            connectionSection.merge(section);
        }
        sections.add(section);
    }

    private void validateDuplicateSection(Section section) {
        if (getSectionStream(section).count() > 1) {
            throw new IllegalArgumentException("상행역과 하행역이 이미 노선에 모두 등록되어있습니다.");
        }
    }

    private Section findConnectableSection(Section section) {
        return getSectionStream(section)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("연결할 수 있는 구간이 없습니다."));
    }

    private Stream<Section> getSectionStream(Section section) {
        return sections.stream().filter(s -> s.isContainAnyStation(section));
    }

    public List<Station> getStationsInOrder() {
        Map<Station, Station> map = sections.stream()
                .collect(Collectors.toMap(Section::getUpStation, Section::getDownStation));
        Station station = findFirstUpStation();

        List<Station> stations = new ArrayList<>();
        while (map.get(station) != null) {
            stations.add(station);
            station = map.get(station);
        }
        stations.add(station);
        return stations;
    }

    private Station findFirstUpStation() {
        Set<Station> upStations = sections.stream()
                .map(Section::getUpStation)
                .collect(Collectors.toSet());
        Set<Station> downStations = sections.stream()
                .map(Section::getDownStation)
                .collect(Collectors.toSet());
        return upStations.stream()
                .filter(upStation -> !downStations.contains(upStation))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }

}
