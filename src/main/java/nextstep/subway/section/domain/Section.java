package nextstep.subway.section.domain;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.Station;

import javax.persistence.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Entity
public class Section extends BaseEntity implements Comparable<Section> {

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

    public Distance minusDistance(Distance distance) {
        return this.getDistance().minus(distance);
    }

    public Line getLine() {
        return line;
    }

    public boolean isSameUpStation(Section section) {
        return upStation != null && upStation.equals(section.getUpStation());
    }

    public boolean isSameDownStation(Section section) {
        return isDownStation(section.getDownStation());
    }

    public boolean isDownStation(Station station) {
        return downStation.equals(station);
    }

    public void updateUpStation(Section section) {
        this.upStation = section.getUpStation();
        this.distance = this.distance.minus(section.distance);
    }

    public void addInnerSection(Section newSection) {
        if(this.getUpStation() == newSection.getUpStation()){
            this.upStation = newSection.getDownStation();
        }

        if(this.getDownStation() == newSection.getDownStation()){
            this.downStation = newSection.getUpStation();
        }
    }

    public void updateDownStation(Section section) {
        this.downStation = section.getUpStation();
        this.distance =  section.minusDistance(this.distance);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Section)) return false;
        Section section = (Section) o;
        return Objects.equals(getId(), section.getId()) && Objects.equals(getUpStation(), section.getUpStation()) && Objects.equals(getDownStation(), section.getDownStation()) && Objects.equals(getLine(), section.getLine()) && Objects.equals(getDistance(), section.getDistance());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getUpStation(), getDownStation(), getLine(), getDistance());
    }

    @Override
    public int compareTo(Section o) {
        if (this.getUpStation() == o.getDownStation()) {
            return 1;
        }

        if (this.getDownStation() == o.getUpStation()) {
            return -1;
        }
        return 0;
    }
}
