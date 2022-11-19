package nextstep.subway.domain;


import java.util.Arrays;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import nextstep.subway.value.ErrMsg;

@Entity
public class Section extends BaseEntity{

    protected Section() {
    }


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "up_station_id", nullable = false)
    private Station upStation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "down_station_id", nullable = false)
    private Station downStation;

    private int distance;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "line_id")
    private Line line;

    public Section(Station upStation, Station downStation, int distance) {
        this.upStation = upStation;
        this.downStation = downStation;
        if(distance<=0){
            throw new IllegalArgumentException(ErrMsg.INAPPROPRIATE_DISTANCE);
        }
        this.distance = distance;
    }

    public void addLine(Line line) {
        this.line = line;
    }

    public List<Station> getStations(){
        return Arrays.asList(upStation, downStation);
    }


    public Long getId() {
        return id;
    }

    public Long getUpStationId() {
        return upStation.getId();
    }

    public Long getDownStationId() {
        return downStation.getId();
    }

    public int getDistance() {
        return distance;
    }

    public Long getLineId() {
        return line.getId();
    }

    public Station getUpStation() {
        return upStation;
    }

    public Station getDownStation() {
        return downStation;
    }

    public void updateDownStation(Section section) {
        if(section.getDistance()>=this.distance){
            throw new IllegalArgumentException(ErrMsg.DISTANCE_TOO_LONG);
        }
        this.downStation = section.getUpStation();
        this.distance -= section.getDistance();
    }

    public void updateUpStation(Section section) {
        if(section.getDistance()>=this.distance){
            throw new IllegalArgumentException(ErrMsg.DISTANCE_TOO_LONG);
        }
        this.upStation = section.getDownStation();
        this.distance -= section.getDistance();
    }
}
