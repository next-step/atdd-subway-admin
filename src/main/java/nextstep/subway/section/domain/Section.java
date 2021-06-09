package nextstep.subway.section.domain;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.Station;

import javax.persistence.*;

@Entity
public class Section extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int sequence;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Line line;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "up_station_id")
    private Station upStation;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "down_station_id")
    private Station downStation;

    private int distance;

    protected Section() {
    }

    public Section(Station upStation, Station downStation, int distance) {
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public Line getLine() {
        return line;
    }

    public Station getUpStation() {
        return upStation;
    }

    public Station getDownStation() {
        return downStation;
    }

    public void changeUpStation(Station upStation) {
        this.upStation = upStation;
    }

    public void changeDownStation(Station downStation) {
        this.downStation = downStation;
    }

    public int getDistance() {
        return distance;
    }

    public void minusDistance(int distance){
        if (this.getDistance() > distance){
            throw new IllegalArgumentException("등록할 수 없는 구간입니다.");
        }
        this.distance = this.distance - distance;
    }

    public boolean isUpStationEquals(Section section){
        return this.getUpStation().equals(section.getUpStation());
    }

    public boolean isDownStationEquals(Section section){
        return this.getDownStation().equals(section.getDownStation());
    }

    public boolean isUpStationAndTargetDownStationEquals(Section section){
        return this.getUpStation().equals(section.getDownStation());
    }

    public boolean isDownStationAndTargetUpStationEquals(Section section){
        return this.getDownStation().equals(section.getUpStation());
    }

    public void setLine(Line line) {
        this.line = line;
    }

    public int getSequence() {
        return sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }
}
