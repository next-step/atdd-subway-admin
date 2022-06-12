package nextstep.subway.domain;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Embeddable
public class Sections {
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "line_id", foreignKey = @ForeignKey(name = "fk_section_to_line"))
    private List<Section> sections = new ArrayList<>();

    protected Sections() {
    }

    public Sections(List<Section> sections) {
        this.sections = sections;
    }

    public void add(Section section) {
        this.sections.stream()
                .filter(it -> it.upStationId().equals(section.upStationId()))
                .findFirst()
                .ifPresent(it ->
                        {
                            it.updateUpStationId(section.downStationId());
                            it.calculateDistance(section.distance());
                        }
                );
        this.sections.add(section);
    }

    public List<Long> toLineStationIds() {
        List<Long> stations = new ArrayList<>();
        Long stationId = findFirstStation(this.sections.get(0).upStationId());
        stations.add(stationId);
        while (hasNextStation(stationId)) {
            stations.add(nextStationId(stationId));
            stationId = nextStationId(stationId);
        }
        return stations;
    }

    private Long findFirstStation(Long stationId) {
        if (hasPrevStation(stationId)) {
            stationId = prevStationId(stationId);
        }
        return stationId;
    }

    private Long prevStationId(Long stationId) {
        return this.sections.stream()
                .filter(it -> it.downStationId().equals(stationId))
                .findFirst().orElseThrow(() -> new IllegalArgumentException("이전 역이 없습니다.")).upStationId();
    }

    private boolean hasPrevStation(Long stationId) {
        return this.sections.stream().anyMatch(it -> it.downStationId().equals(stationId));
    }

    private Long nextStationId(Long stationId) {
        return this.sections.stream()
                .filter(it -> it.upStationId().equals(stationId))
                .findFirst().orElseThrow(() -> new IllegalArgumentException("다음 역이 없습니다.")).downStationId();
    }

    private boolean hasNextStation(Long stationId) {
        return this.sections.stream().anyMatch(it -> it.upStationId().equals(stationId));
    }
}
