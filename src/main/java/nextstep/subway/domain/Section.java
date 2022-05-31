package nextstep.subway.domain;

import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class Section extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private int distance;

    @ManyToOne
    @JoinColumn(name = "up_station_id", foreignKey = @ForeignKey(name = "fk_section_to_upstation"))
    private Station upStation;

    @ManyToOne
    @JoinColumn(name = "down_station_id", foreignKey = @ForeignKey(name = "fk_section_to_downstation"))
    private Station downStation;

    @ManyToOne
    @JoinColumn(name = "line_id", foreignKey = @ForeignKey(name = "fk_section_to_line"))
    private Line line;

    public Section() {
    }

    public Section(Station upStation, Station downStation, int distance) {
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public static Section of(Station upStation, Station downStation, int distance) {
        return new Section(upStation, downStation, distance);
    }

    public void addLine(Line line) {
        this.line = line;
    }

    public Station getUpStation() {
        return upStation;
    }

    public Station getDownStation() {
        return downStation;
    }

    public int getDistance() {
        return distance;
    }

    public void setLine(Line line) {
        this.line = line;
    }

    public void calculate(Section newSection) {
        if (this.upStation.equals(newSection.getUpStation())) {
            this.upStation = newSection.getDownStation();
            calculateDistance(newSection);
        }
        if (this.downStation.equals(newSection.getDownStation())) {
            this.downStation = newSection.getUpStation();
            calculateDistance(newSection);
        }
    }

    private void calculateDistance(Section newSection) {
        int newDistance = this.distance - newSection.distance;
        if (newDistance <= 0) {
            throw new IllegalArgumentException("기존 역 사이 거리보다 작을 수 없습니다.");
        }
        this.distance = newDistance;
    }
}
