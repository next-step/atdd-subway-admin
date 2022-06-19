package nextstep.subway.domain;

import javax.persistence.*;
import java.util.*;

@Embeddable
public class Sections {
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "line_id", foreignKey = @ForeignKey(name = "fk_section_to_line"))
    private List<Section> sections = new ArrayList<>();

    protected Sections() {
    }

    public void add(Section section) {
        checkAddValidation(section);
        checkAddBetweenExist(section);
        this.sections.add(section);
    }

    public void delete(Long stationId) {
        checkDeleteValidation(stationId);
        deleteBetweenExist(stationId);
    }

    public Section findSectionUpStationId(Long upStationId) {
        return hasStation(upStationId, SectionExistType.UP_STATION).orElseThrow(IllegalArgumentException::new);
    }

    public List<Long> toLineStationIds() {
        return new ArrayList<>(getUniqueIdsOrderByUpStation());
    }

    private Set<Long> getUniqueIdsOrderByUpStation() {
        Set<Long> stations = new LinkedHashSet<>();
        Long stationId = findFirstStation(this.sections.get(0).getUpStationId());
        stations.add(stationId);
        while (hasNextStation(stationId)) {
            stations.add(nextStationId(stationId));
            stationId = nextStationId(stationId);
        }
        return stations;
    }

    private void checkAddValidation(Section target) {
        if (this.sections.isEmpty()) {
            return;
        }

        Set<Long> uniqueIds = getUniqueIdsOrderByUpStation();

        if (uniqueIds.contains(target.getUpStationId()) && uniqueIds.contains(target.getDownStationId())) {
            throw new IllegalArgumentException("이미 추가된 역입니다.");
        }

        if (!uniqueIds.contains(target.getUpStationId()) && !uniqueIds.contains(target.getDownStationId())) {
            throw new IllegalArgumentException("노선에 등록된 역이 없습니다.");
        }
    }

    private void checkAddBetweenExist(Section section) {
        SectionExistType existType = null;
        if (hasNextStation(section.getUpStationId())) {
            existType = SectionExistType.UP_STATION;
        }
        if (hasPrevStation(section.getDownStationId())) {
            existType = SectionExistType.DOWN_STATION;
        }
        relocationAddSection(section, existType);
    }

    private void relocationAddSection(Section section, SectionExistType existType) {
        if (existType != null) {
            this.sections.stream()
                    .filter(it -> it.sameStation(section, existType))
                    .findFirst()
                    .ifPresent(it -> it.updateExistOf(section, existType));
        }
    }

    private void checkDeleteValidation(Long stationId) {
        Set<Long> uniqueIds = getUniqueIdsOrderByUpStation();

        if (uniqueIds.size() <= 2) {
            throw new IllegalArgumentException("하나의 구간만 존재해서 삭제할 수 없습니다.");
        }

        if (!uniqueIds.contains(stationId)) {
            throw new IllegalArgumentException("노선에 등록된 역이 없습니다.");
        }
    }

    private void deleteBetweenExist(Long stationId) {
        if (hasPrevStation(stationId) && hasNextStation(stationId)) {
            Section deleteSection = hasStation(stationId, SectionExistType.UP_STATION)
                    .orElseThrow(IllegalArgumentException::new);
            relocationDeleteSection(hasStation(stationId, SectionExistType.DOWN_STATION)
                    .orElseThrow(IllegalArgumentException::new), deleteSection);
            removeSection(deleteSection);
            this.sections.remove(deleteSection);
            return;
        }

        if (hasPrevStation(stationId)) {
            removeSection(hasStation(stationId, SectionExistType.DOWN_STATION)
                    .orElseThrow(IllegalArgumentException::new));
        }

        if (hasNextStation(stationId)) {
            removeSection(hasStation(stationId, SectionExistType.UP_STATION)
                    .orElseThrow(IllegalArgumentException::new));
        }
    }

    private void removeSection(Section section) {
        this.sections.remove(section);
    }

    private void relocationDeleteSection(Section upSection, Section downSection) {
        upSection.updateDeleteExistOf(downSection);
    }

    private Long findFirstStation(Long stationId) {
        if (hasPrevStation(stationId)) {
            stationId = prevStationId(stationId);
            return findFirstStation(stationId);
        }
        return stationId;
    }

    private Long prevStationId(Long stationId) {
        return hasStation(stationId, SectionExistType.DOWN_STATION)
                .orElseThrow(() -> new IllegalArgumentException("이전 역이 없습니다.")).getUpStationId();
    }

    private boolean hasPrevStation(Long stationId) {
        return hasStation(stationId, SectionExistType.DOWN_STATION).isPresent();
    }


    private Long nextStationId(Long stationId) {
        return hasStation(stationId, SectionExistType.UP_STATION)
                .orElseThrow(() -> new IllegalArgumentException("다음 역이 없습니다.")).getDownStationId();
    }

    private boolean hasNextStation(Long stationId) {
        return hasStation(stationId, SectionExistType.UP_STATION).isPresent();
    }

    private Optional<Section> hasStation(Long stationId, SectionExistType existType) {
        return this.sections.stream()
                .filter(it -> it.sameStationId(stationId, existType))
                .findFirst();
    }
}
