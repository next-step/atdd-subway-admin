package nextstep.subway.section.domain;

import nextstep.subway.line.domain.Line;

import javax.persistence.*;
import java.util.Objects;

@Entity
public class Section {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "line_id")
    private Line line;
    private Long upStationId;
    private Long downStationId;
    private int distance;

    public Section() {
    }

    public Section(Line line, Long upStationId, Long downStationId, int distance) {
        this.line = line;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public Long getId() {
        return id;
    }

    public Line getLine() {
        return line;
    }

    public Long getUpStationId() {
        return upStationId;
    }

    public Long getDownStationId() {
        return downStationId;
    }

    public int getDistance() {
        return distance;
    }

    public boolean hasStation(Long stationId) {
        return upStationId == stationId || downStationId == stationId;
    }

    public boolean validateDistance(Section section) {
        return this.distance > section.distance;
    }

    public boolean validateAddSection(Section section) {
        Long newUpStationId = section.getUpStationId();
        Long newDownStationId = section.getDownStationId();

        if (upStationId == newUpStationId && downStationId == newDownStationId) {
            return false;
        }
        if (upStationId != newUpStationId && downStationId != newDownStationId) {
            return false;
        }
        if (upStationId == newUpStationId || downStationId == newDownStationId) {
            return validateDistance(section);
        }
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Section section = (Section) o;
        return id.equals(section.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}


