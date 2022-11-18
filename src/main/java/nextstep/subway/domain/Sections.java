package nextstep.subway.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;

@Embeddable
public class Sections {
    private static final String ALREADY_CONTAIONS_ERROR_MESSAGE = "상행역과 하행역이 이미 노선에 모두 등록되어 있어 추가할 수 없습니다.";
    private static final String NOT_CONTAIONS_ANY_ERROR_MESSAGE = "상행역과 하행역 둘 중 하나도 포함되어있지 않아 추가할 수 없습니다.";

    @OneToMany(mappedBy = "line", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private final List<Section> sections = new ArrayList<>();

    protected Sections() {
    }

    public Stations allStations() {
        return sections.stream()
            .map(Section::getStations)
            .reduce(Stations::concatDistinct)
            .orElseGet(Stations::empty);
    }

    public void addSection(Section newSection) {
        Stations stations = this.allStations();
        validateAlreadyContainsAll(stations, newSection);
        validateNotContainsAny(stations, newSection);
        this.sections.forEach(section -> section.modify(newSection));
        this.sections.add(newSection);
    }

    private void validateNotContainsAny(Stations stations, Section newSection) {
        if (stations.isEmpty()) {
            return;
        }
        if (newSection.nonMatch(stations)) {
            throw new IllegalArgumentException(NOT_CONTAIONS_ANY_ERROR_MESSAGE);
        }
    }

    private void validateAlreadyContainsAll(Stations stations, Section newSection) {
        if (stations.containsAll(newSection.getStations())) {
            throw new IllegalArgumentException(ALREADY_CONTAIONS_ERROR_MESSAGE);
        }
    }
}
