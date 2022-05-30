package nextstep.subway.domain;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import nextstep.subway.exception.CreateSectionException;

@Entity
public class LineStation extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "line_id", foreignKey = @ForeignKey(name = "fk_linestation_to_line"))
    private Line line;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "up_station_id")
    private Station upStation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "down_station_id")
    private Station downStation;

    private Long distance;

    protected LineStation(){

    }

    public LineStation(Line line, Station upStation, Station downStation, Long distance) {
        this.line = line;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public void updateDownStation(Station station, long distance) {
        validateDistance(distance);
        this.downStation = station;
        this.distance = distance;
    }

    private void validateDistance(long distance) {
        if (this.distance < distance) {
            throw new CreateSectionException("[ERROR] 이미 존재하는 구간보다 길이가 길 수 없습니다.");
        }
    }

    public long calcNewSectionDistance(long distance) {
        if(this.distance == distance){
            throw new CreateSectionException("[ERROR] 이미 존재하는 구간과 길이가 동일 할 수 없습니다.");
        }
        return this.distance - distance;
    }

    public Station getUpStation() {
        return upStation;
    }

    public Station getDownStation() {
        return downStation;
    }

    public void setLine(Line line) {
        this.line = line;
    }
}
