package nextstep.subway.section.domain;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.Station;

import javax.persistence.Column;
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

    @ManyToOne
    @JoinColumn(name = "line_id", foreignKey = @ForeignKey(name = "fk_section_to_line"))
    private Line line;

    @ManyToOne
    @JoinColumn(name = "up_station_id", foreignKey = @ForeignKey(name = "fk_up_section_to_station"))
    private Station upStation;

    @ManyToOne
    @JoinColumn(name = "down_station_id", foreignKey = @ForeignKey(name = "fk_down_section_to_station"))
    private Station downStation;

    @Column(name = "distance")
    private Integer distance;

    public Section updateSection(Section that) {


        int calculatedDistance = this.distance - that.distance;

        if (this.upStation.isSameName(that.upStation)) {

            Section tempSection = new Section(this.line, this.upStation, this.downStation, this.distance);
            this.downStation = that.downStation;
            this.distance = that.distance;

            that.downStation = tempSection.downStation;
            that.upStation = this.downStation;
            that.distance = calculatedDistance;
        }

        if (this.downStation.isSameName(that.downStation)) {
            this.downStation = that.upStation;
            this.distance = calculatedDistance;
        }

        return that;
    }

    protected Section() {

    }

    public Section(Line line, Station upStation, Station downStation, Integer distance) {
        this.line = line;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public Boolean isSameNameWithUpStation(Section that) {
        return this.upStation.isSameName(that.upStation);
    }

    public Boolean isSameNameWithDownStation(Section that) {
        return this.downStation.isSameName(that.downStation);
    }

    public Boolean isInteractiveWithStation(Section that) {
        return this.upStation.isSameName(that.upStation) ||
                this.upStation.isSameName(that.downStation) ||
                this.downStation.isSameName(that.upStation) ||
                this.downStation.isSameName(that.downStation);
    }

    public Boolean isLongerThanEqual(Section that) {
        return this.distance <= that.distance;
    }


    public Long getId() {
        return id;
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
}
