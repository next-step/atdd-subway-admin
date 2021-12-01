package nextstep.subway.line.domain;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.station.domain.Station;

import javax.persistence.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Entity
public class Section extends BaseEntity  {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "up_station_id")
    private Station upStation;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "down_station_id")
    private Station downStation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "line_id")
    private Line line;

    @Embedded
    private Distance distance;

    protected Section() {

    }

    public Section(Line line, Station upStation, Station downStation, Distance distance) {
        this.line = line;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public Distance getDistance() {
        return distance;
    }

    public Station getUpStation() {
        return upStation;
    }

    public List<Station> getStations(){
        return Collections.unmodifiableList(Arrays.asList(upStation, downStation));
    }

    public Station getDownStation() {
        return downStation;
    }

    public Long getId() {
        return id;
    }

    public void addLine(Line line){
        this.line = line;
    }

    public void minusDistance(Distance distance) {
        this.distance = this.distance.minus(distance);
    }

    public Distance sumDistance(Distance distance) {
        this.distance = this.distance.sum(distance);
        return this.distance;
    }

    public Line getLine() {
        return line;
    }

    public void addInnerSection(Section newSection) {
        if(this.getUpStation() == newSection.getUpStation()){
            this.upStation = newSection.getDownStation();
            this.minusDistance(newSection.getDistance());
        }

        if(this.getDownStation() == newSection.getDownStation()){
            this.downStation = newSection.getUpStation();
            this.minusDistance(newSection.getDistance());
        }
    }

    public int getDistanceNumber(){
        return this.distance.getDistance();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Section)) return false;
        Section section = (Section) o;
        return Objects.equals(getUpStation(), section.getUpStation()) && Objects.equals(getDownStation(), section.getDownStation()) && Objects.equals(getLine(), section.getLine());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUpStation(), getDownStation(), getLine());
    }

}
