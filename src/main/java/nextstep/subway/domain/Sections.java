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
        checkValidation(section);
        checkAddBetweenExist(section);
        this.sections.add(section);
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

    private void checkValidation(Section target) {
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
        } else if (hasPrevStation(section.getDownStationId())) {
            existType = SectionExistType.DOWN_STATION;
        }
        if (existType != null) {
            SectionExistType finalExistType = existType;
            this.sections.stream()
                    .filter(it -> it.sameStation(section, finalExistType))
                    .findFirst()
                    .ifPresent(it -> it.updateExistOf(section, finalExistType));
        }
    }

    private Long findFirstStation(Long stationId) {
        if (hasPrevStation(stationId)) {
            stationId = prevStationId(stationId);
            return findFirstStation(stationId);
        }
        return stationId;
    }

    private Long prevStationId(Long stationId) {
        return this.sections.stream()
                .filter(it -> it.sameDownStation(stationId))
                .findFirst().orElseThrow(() -> new IllegalArgumentException("이전 역이 없습니다.")).getUpStationId();
    }

    private boolean hasPrevStation(Long stationId) {
        return this.sections.stream().anyMatch(it -> it.sameDownStation(stationId));
    }

    private Long nextStationId(Long stationId) {
        return this.sections.stream()
                .filter(it -> it.sameUpStation(stationId))
                .findFirst().orElseThrow(() -> new IllegalArgumentException("다음 역이 없습니다.")).getDownStationId();
    }

    private boolean hasNextStation(Long stationId) {
        return this.sections.stream().anyMatch(it -> it.sameUpStation(stationId));
    }
}
