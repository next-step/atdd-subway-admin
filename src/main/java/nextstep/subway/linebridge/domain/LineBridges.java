package nextstep.subway.linebridge.domain;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Embeddable
public class LineBridges {

    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL)
    private List<LineBridge> lineBridges = new ArrayList<>();

    public void add(LineBridge lineBridge) {
        validateDuplicate(lineBridge);
        validateNotingMatch(lineBridge);

        repairSections(lineBridge);
        lineBridges.add(lineBridge);
    }

    private void validateDuplicate(LineBridge lineBridge) {
        Optional<LineBridge> duplicate = lineBridges.stream()
                .filter(element -> element.isSame(lineBridge))
                .findFirst();

        if (duplicate.isPresent()) {
            throw new IllegalArgumentException("상행역과 하행역이 이미 노선에 등록되어 있으면 구간을 추가할 수 없습니다.");
        }
    }

    private void validateNotingMatch(LineBridge lineBridge) {
        if (lineBridges.isEmpty()) {
            return;
        }
        boolean anyMatch = lineBridges.stream()
                .anyMatch(element -> element.isAnyMatch(lineBridge));

        if (!anyMatch) {
            throw new IllegalArgumentException("노선에 상행선과 하행선이 둘 다 포함되어 있지 않으면 구간을 추가할 수 없습니다.");
        }
    }

    private void repairSections(LineBridge lineBridge) {
        for (LineBridge element : lineBridges) {
            element.repair(lineBridge);
        }
    }

    public List<LineBridge> getLineBridges() {
        return Collections.unmodifiableList(lineBridges);
    }
}
