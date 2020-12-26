package nextstep.subway.line.domain;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.station.domain.Station;

import javax.persistence.*;

@Entity
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"up_station_id", "down_station_id"})})
public class Section extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    private Line line;
    @ManyToOne(fetch = FetchType.LAZY)
    private Station upStation;
    @ManyToOne(fetch = FetchType.LAZY)
    private Station downStation;
    private Integer distance;
    private Integer sectionNumber;

    private Section(){}

    public Section(Line line, Station upStation, Station downStation, Integer distance, Integer sectionNumber) {
        setLine(line);
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
        this.sectionNumber = sectionNumber;
    }

    public void setLine(Line line) {
        if ( this.line != null ) {
            this.line.getSections().remove(this);
        }
        line.getSections().add(this);
        this.line = line;
    }

    public Station getUpStation() {
        return upStation;
    }

    public void updateUpStation(Station upStation) {
        this.upStation = upStation;
    }

    public Station getDownStation() {
        return downStation;
    }

    public void updateDownStation(Station downStation) {
        this.downStation = downStation;
    }

    public Integer getDistance() {
        return distance;
    }

    public Integer getSectionNumber() {
        return sectionNumber;
    }

    public void updateSectionNumber(Integer sectionNumber) {
        this.sectionNumber = sectionNumber;
    }

    @Override
    public String toString() {
        return "Section{" +
                "id=" + id +
                ", line=" + line +
                ", upStation=" + upStation +
                ", downStation=" + downStation +
                ", distance=" + distance +
                '}';
    }
}
