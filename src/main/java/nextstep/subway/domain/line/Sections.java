package nextstep.subway.domain.line;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import nextstep.subway.domain.station.Station;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line")
    private List<Section> sections = new ArrayList<>();

    protected Sections() {

    }

    private Sections(List<Section> sections) {
        this.sections = sections;
    }

    public static Sections addInitialSection(Section section) {
        List<Section> sections = new ArrayList<>();
        sections.add(section);
        return new Sections(sections);
    }

    public List<Section> getSections() {
        // TODO 정렬된 구간 목록 반환 기능이 필요 여부 고민
        return sections;
    }

    public void add(Section section) {
        // TODO 상행/하행역 사이에 중간 구간을 삽입 : 중간 구간 추가 및 연결되어야 하는 구간의 상행/하행역 갱신 처리
        sections.add(section);
    }

    public List<Station> sortedByFinalUpStations() {
        List<Station> sortedByFinalUpStations = new ArrayList<>();
        sortedByFinalUpStations.add(getFinalUpStation());
        for (int i = 0; i < sections.size(); i++) {
            Station nextNode = findSectionByUpStation(sortedByFinalUpStations.get(i)).getDownStation();
            sortedByFinalUpStations.add(nextNode);
        }
        return sortedByFinalUpStations;
    }

    private List<Station> getAllUpStations() {
        return sections.stream()
            .map(Section::getUpStation)
            .collect(Collectors.toList());
    }

    private List<Station> getAllDownStations() {
        return sections.stream()
            .map(Section::getDownStation)
            .collect(Collectors.toList());
    }

    public Station getFinalUpStation() {
        List<Station> upStations = getAllUpStations();
        upStations.removeAll(getAllDownStations());
        return getStationByOptional(upStations);
    }

    public Station getFinalDownStation() {
        List<Station> downStations = getAllDownStations();
        downStations.removeAll(getAllUpStations());
        return getStationByOptional(downStations);
    }

    private Station getStationByOptional(List<Station> stations) {
        return stations.stream()
            .findFirst()
            .orElseThrow(IllegalArgumentException::new);
    }

    private Section findSectionByUpStation(Station station) {
        return sections.stream()
            .filter(it -> it.getUpStation().equals(station))
            .findFirst()
            .orElseThrow(IllegalArgumentException::new);
    }

    private Section findSectionByDownStation(Station station) {
        return sections.stream()
            .filter(it -> it.getDownStation().equals(station))
            .findFirst()
            .orElseThrow(IllegalArgumentException::new);
    }

    public Section getFinalUpSection() {
        return findSectionByUpStation(getFinalUpStation());
    }

    public Section getFinalDownSection() {
        return findSectionByDownStation(getFinalDownStation());
    }
}
