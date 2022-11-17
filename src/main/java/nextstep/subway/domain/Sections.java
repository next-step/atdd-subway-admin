package nextstep.subway.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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

    public List<Station> getStations() {
        return Collections.unmodifiableList(
            sections.stream()
                .flatMap(section -> section.getStations().stream())
                .distinct()
                .collect(Collectors.toList())
        );
    }

    public void addSection(Section newSection) {
        validateAlreadyContainsAll(newSection);
        validateNotContainsAny(newSection);
        this.sections.forEach(section -> section.modify(newSection));
        this.sections.add(newSection);
    }

    private void validateNotContainsAny(Section section) {
        List<Station> stations = this.getStations();
        if (stations.isEmpty()) {
            return;
        }
        if (section.getStations().stream().noneMatch(stations::contains)) {
            throw new IllegalArgumentException(NOT_CONTAIONS_ANY_ERROR_MESSAGE);
        }
    }

    private void validateAlreadyContainsAll(Section section) {
        if (getStations().containsAll(section.getStations())) {
            throw new IllegalArgumentException(ALREADY_CONTAIONS_ERROR_MESSAGE);
        }
    }
}
