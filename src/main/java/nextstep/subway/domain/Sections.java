package nextstep.subway.domain;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Embeddable
public class Sections {
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "line_id", foreignKey = @ForeignKey(name = "fk_section_to_line"))
    private List<Section> sections = new ArrayList<>();

    protected Sections() {
    }

    public void add(Section section) {
        checkValidation(section);
        checkAddBetweenExistSection(section);
        this.sections.add(section);
    }

    public List<Long> toLineStationIds() {
        List<Long> stations = new ArrayList<>();
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

        Set<Long> uniqueIds = new HashSet<>();
        this.sections.forEach( it -> {
                    uniqueIds.add(it.getUpStationId());
                    uniqueIds.add(it.getDownStationId());
        });

        if (uniqueIds.contains(target.getUpStationId()) && uniqueIds.contains(target.getDownStationId())) {
            throw new IllegalArgumentException("이미 추가된 역입니다.");
        }

        if (!uniqueIds.contains(target.getUpStationId()) && !uniqueIds.contains(target.getDownStationId())) {
            throw new IllegalArgumentException("노선에 등록된 역이 없습니다.");
        }
    }

    private void checkAddBetweenExistSection(Section section) {
        if (hasNextStation(section.getUpStationId())) {
            this.sections.stream()
                    .filter(it -> it.sameUpStation(section.getUpStationId()))
                    .findFirst()
                    .ifPresent(it ->
                            {
                                it.updateUpStationId(section.getDownStationId());
                                it.calculateDistance(section.getDistance());
                            }
                    );
        } else if (hasPrevStation(section.getDownStationId())) {
            this.sections.stream()
                    .filter(it -> it.sameDownStation(section.getDownStationId()))
                    .findFirst()
                    .ifPresent(it ->
                            {
                                it.updateDownStationId(section.getUpStationId());
                                it.calculateDistance(section.getDistance());
                            }
                    );
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
