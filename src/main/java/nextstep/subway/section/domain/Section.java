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
    private int distance;

    protected Section() {

    }

    public Section(Line line, Station upStation, Station downStation, int distance) {
        this.line = line;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public boolean isDuplicateSection(Section that) {
        return this.upStation.equals(that.upStation) && this.downStation.equals(that.downStation);
    }

    public Boolean isInteractiveStation(Section that) {
        return this.upStation.equals(that.upStation) ||
                this.upStation.equals(that.downStation) ||
                this.downStation.equals(that.upStation) ||
                this.downStation.equals(that.downStation);
    }

    public Boolean isLongerThanEqual(Section that) {
        return this.distance <= that.distance;
    }

    public void updateExistingSection(Section newSection) {
        if (this.upStation.equals(newSection.upStation)) {
            this.upStation = newSection.downStation;
            this.distance -= newSection.distance;
            return;
        }

        if (this.downStation.equals(newSection.downStation)) {
            this.downStation = newSection.upStation;
            this.distance -= newSection.distance;
        }
    }

    public Section updateNewSection() {
        return new Section(
                this.line,
                this.upStation,
                this.downStation,
                this.distance
        );
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

    public int getDistance() {
        return distance;
    }
}
