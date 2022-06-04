package nextstep.subway.domain;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Embeddable
public class Sections {
    public static final int END_POINT = 1;

    @OneToMany(mappedBy = "line", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<Section> elements = new ArrayList<>();

    public void add(Section section) {
        validateDuplicate(section);
        validateNotingMatch(section);

        repairSections(section);
        elements.add(section);
    }

    private void validateDuplicate(Section section) {
        Optional<Section> duplicate = elements.stream()
                .filter(element -> element.isSame(section))
                .findFirst();

        if (duplicate.isPresent()) {
            throw new IllegalArgumentException("상행역과 하행역이 이미 노선에 등록되어 있으면 구간을 추가할 수 없습니다.");
        }
    }

    private void validateNotingMatch(Section section) {
        if (elements.isEmpty()) {
            return;
        }
        boolean anyMatch = elements.stream()
                        .anyMatch(element -> element.isAnyMatch(section));

        if (!anyMatch) {
            throw new IllegalArgumentException("노선에 상행선과 하행선이 둘 다 포함되어 있지 않으면 구간을 추가할 수 없습니다.");
        }
    }

    private void repairSections(Section section) {
        for (Section element : elements) {
            element.repair(section);
        }
    }

    public List<Section> getElements() {
        return Collections.unmodifiableList(elements);
    }

    public void remove(Station station) {
        if (isEndPoint(station)) {
            elements.remove(findSection(station));
            return;
        }
        Section section = findSectionByDownStation(station);
        removeIntermediateSection(section);
    }

    private void removeIntermediateSection(Section section) {
        for (Section element : elements) {
            element.remove(section);
        }
        elements.remove(section);
    }

    private Section findSection(Station station) {
        return elements.stream()
                .filter(section -> section.hasStation(station))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("해당역을 포함하는 구간이 존재하지 않습니다."));
    }

    private Section findSectionByDownStation(Station station) {
        return elements.stream()
                .filter(section -> section.isSameDownStation(station))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("해당역을 포함하는 구간이 존재하지 않습니다."));
    }

    private boolean isEndPoint(Station station) {
        long stationCount = elements.stream()
                .filter(section -> section.hasStation(station))
                .count();
        return stationCount == END_POINT;
    }
}
