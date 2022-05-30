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
    @OneToMany(mappedBy = "line", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<Section> elements = new ArrayList<>();

    public void add(Section section) {
        validateDuplicate(section);

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

    private void repairSections(Section section) {
        for (Section element : elements) {
            element.repair(section);
        }
    }

    public List<Section> getElements() {
        return Collections.unmodifiableList(elements);
    }
}
