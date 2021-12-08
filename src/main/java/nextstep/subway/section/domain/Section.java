package nextstep.subway.section.domain;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.line.domain.Line;

import javax.persistence.*;

@Entity
public class Section extends BaseEntity implements Comparable<Section> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private int distance;
    private Long upStationId;
    private Long downStationId;
    @ManyToOne
    private Line line;

    public Section() {
    }

    public Section(Long upStationId, Long downStationId, int distance) {
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public int getDistance() {
        return distance;
    }

    public Long getUpStationId() {
        return upStationId;
    }

    public Long getDownStationId() {
        return downStationId;
    }

    public Line getLine() {
        return line;
    }

    public void setLine(Line line) {
        this.line = line;
    }

    public void reMeasurement(int distance) {
        if (this.distance <= distance) {
            throw new IllegalArgumentException("등록하려는 구간의 길이가 너무 큽니다.");
        }
        this.distance = this.distance - distance;
    }

    public boolean isExistUpStation(Long stationId) {
        return upStationId.equals(stationId);
    }

    public boolean isExistDownStation(Long stationId) {
        return downStationId.equals(stationId);
    }

    public void updateUpStation(Section section) {
        this.upStationId = section.getDownStationId();
        reMeasurement(section.getDistance());
    }

    public void updateDownStation(Section section) {
        this.downStationId = section.getUpStationId();
        reMeasurement(section.getDistance());
    }

    @Override
    public int compareTo(Section o) {
        if (this.getUpStationId().equals(o.getDownStationId())) {
            return 1;
        }
        if (this.getDownStationId().equals(o.getUpStationId())) {
            return -1;
        }
        return 0;
    }
}
