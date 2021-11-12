package nextstep.subway.section.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;

import nextstep.subway.station.domain.Station;

@Embeddable
public class Sections {
    private static final String NOT_EXIST_FIRST_SECTION = "첫 번째 구간이 존재하지 않습니다.";
    private static final String NOT_EXIST_UP_STATION = "구간에 상행 역이 존재하지 않습니다.";

    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    protected Sections() {}

    private Sections(List<Section> sections) {
        this.sections = sections;
    }

    public static Sections createEmpty() {
        return new Sections(new ArrayList<>());
    }

    public static Sections from(List<Section> sections) {
        return new Sections(sections);
    }

    public void add(Section section) {
        this.sections.add(section);
    }

    public List<Station> getSortedStations() {
        List<Station> stations = new ArrayList<>();
        stations.add(findFirstSection().getUpStation());

        for (int i = 0; i < sections.size(); i++) {
            Section section = findSectionByUpStation(stations.get(i));
            stations.add(section.getDownStation());
        }

        return stations;
    }

    public boolean contains(Section section) {
        return this.sections.contains(section);
    }

    private Section findSectionByUpStation(Station upStation) {
        return sections.stream()
                       .filter(section -> section.isUpStation(upStation))
                       .findFirst()
                       .orElseThrow(() -> new IllegalStateException(NOT_EXIST_UP_STATION));
    }

    protected Section findFirstSection() {
        return sections.stream()
                       .filter(section -> !findDownStations().contains(section.getUpStation()))
                       .findFirst()
                       .orElseThrow(() -> new IllegalStateException(NOT_EXIST_FIRST_SECTION));
    }

    private List<Station> findDownStations() {
        return sections.stream()
                       .map(Section::getDownStation)
                       .collect(Collectors.toList());
    }
}
