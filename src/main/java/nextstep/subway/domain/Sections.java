package nextstep.subway.domain;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Embeddable
public class Sections {
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "line", orphanRemoval = true)
    private List<Section> content = new ArrayList<>();

    protected Sections() {
    }

    public Sections(List<Section> content) {
        this.content = new ArrayList<>(content);
    }

    public void addSection(Section newSection) {
        validateNotContainsAny(newSection);
        validateAlreadyContainsAll(newSection);

        content.forEach(section -> section.modify(newSection));
        content.add(newSection);
    }

    public void removeStation(final Station station) {
        final List<Section> sections = content.stream()
                .filter(section -> section.hasStation(station))
                .collect(toList());
        if(sections.size() == 0) {
            throw new IllegalArgumentException("해당 역은 구간에 포함되어 있지 않습니다.");
        }
        if (content.size() == 1) {
            throw new IllegalArgumentException("구간이 하나인 노선에서는 역을 제거할 수 없습니다.");
        }
        //종점 역일 때
        if(sections.size() == 1) {
            content.remove(sections.get(0));
            return;
        }
        //가운데 역일 때
        if(sections.size() == 2) {
            final Section upSection = sections.get(0);
            final Section downSection = sections.get(1);
            final int distance = upSection.getDistance() + downSection.getDistance();

            final Section newSection = createSection(station, upSection, downSection, distance);
            content.add(newSection);
            content.remove(upSection);
            content.remove(downSection);
        }
    }

    public List<Station> getStations() {
        return content.stream()
                .flatMap(section -> section.getStations().stream())
                .collect(toList());
    }

    private void validateNotContainsAny(Section section) {
        List<Station> stations = this.getStations();
        if (stations.isEmpty()) {
            return;
        }
        if (!section.hasLeastOneStations(stations)) {
            throw new IllegalArgumentException("상행역과 하행역 둘 중 하나도 포함되어있지 않아 추가할 수 없습니다.");
        }
    }

    private void validateAlreadyContainsAll(Section section) {
        boolean isAlreadyContainsAll = new HashSet<>(getStations()).containsAll(section.getStations());
        if (isAlreadyContainsAll) {
            throw new IllegalArgumentException("상행역과 하행역이 이미 노선에 모두 등록되어 있어 추가할 수 없습니다.");
        }
    }

    private Section createSection(Station station, Section upSection, Section downSection, int distance) {
        return new Section(upSection.getStation(station), downSection.getStation(station), distance, upSection.getLine());
    }
}
