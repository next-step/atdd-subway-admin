package nextstep.subway.section.domain;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.common.exception.BadRequestException;
import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.Station;

import javax.persistence.*;

@Entity
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"up_station_id", "down_station_id"})})
public class Section extends BaseEntity {
    @Transient
    private static final int SECTION_NUMBER_OFFSET = 1;
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

    public void validateDistance(Integer distance) {
        if ( this.distance <= distance ) {
            throw new BadRequestException("distance must be lower than " + this.distance);
        }
    }

    public Line getLine() {
        return line;
    }

    public void setLine(Line line) {
        if ( this.line != null ) {
            this.line.getSections().remove(this);
        }
        line.getSections().add(this);
        this.line = line;
    }

    public Long getId() {
        return id;
    }

    public void updateUpStation(Station upStation) {
        this.upStation = upStation;
    }

    public Station getUpStation() {
        return upStation;
    }

    public void updateDownStation(Station downStation) {
        this.downStation = downStation;
    }

    public Station getDownStation() {
        return downStation;
    }

    public Integer getDistance() {
        return distance;
    }

    public Integer getSectionNumber() {
        return sectionNumber;
    }

    public void incrementSectionNumber() {
        sectionNumber += SECTION_NUMBER_OFFSET;
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
