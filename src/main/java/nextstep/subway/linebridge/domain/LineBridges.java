package nextstep.subway.linebridge.domain;

import nextstep.subway.station.domain.Station;

import java.util.LinkedList;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.List;
import java.util.Optional;

@Embeddable
public class LineBridges {

    public static final int ONLY_HAS_ONE_ENTRY = 1;

    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL)
    private List<LineBridge> lineBridges = new LinkedList<>();

    public void add(LineBridge lineBridge) {
        validateDuplicate(lineBridge);
        validateNotingMatch(lineBridge);

        repairSections(lineBridge);
        lineBridges.add(lineBridge);
    }

    private void validateDuplicate(LineBridge lineBridge) {
        if(isFirstLineBridge()){
            return;
        }
        if(countSameLineBridge(lineBridge) > 0) {
            throw new IllegalArgumentException("상행역과 하행역이 이미 노선에 등록되어 있으면 구간을 추가할 수 없습니다.");
        }
    }

    private boolean isFirstLineBridge() {
        return lineBridges.size() == 0;
    }

    private long countSameLineBridge(LineBridge lineBridge) {
        return lineBridges.stream()
                .filter(element -> element.isSame(lineBridge))
                .count();
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

    public LinkedList<LineBridge> getLineBridges() {
        return new LinkedList(lineBridges);
    }

    public void remove(Station station) {
        validateRemoval();
        if (isEndStation(station)) {
            lineBridges.remove(findLineBridge(station));
            return;
        }
        LineBridge lineBridge = findSectionByDownStation(station);
        removeIntermediateSection(lineBridge);
    }

    private void removeIntermediateSection(LineBridge lineBridge) {
        for (LineBridge element : lineBridges) {
            element.remove(lineBridge);
        }
        lineBridges.remove(lineBridge);
    }

    private void validateRemoval() {
        if (isLastSection()) {
            throw new IllegalArgumentException("노선의 마지막 구간은 삭제할 수 없습니다.");
        }
    }

    private LineBridge findSectionByDownStation(Station station) {
        return lineBridges.stream()
                .filter(lineBridge -> lineBridge.isSameDownStation(station))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("해당역을 포함하는 구간이 존재하지 않습니다."));
    }

    private LineBridge findLineBridge(Station station) {
        return lineBridges.stream()
                .filter(lineBridge -> lineBridge.hasStation(station))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("해당역을 포함하는 구간이 존재하지 않습니다."));
    }

    private boolean isEndStation(Station station) {
        long stationCount = lineBridges.stream()
                .filter(lineBridge -> lineBridge.hasStation(station))
                .count();
        return stationCount == ONLY_HAS_ONE_ENTRY;
    }

    private boolean isLastSection() {
        return lineBridges.size() == ONLY_HAS_ONE_ENTRY;
    }
}
