package nextstep.subway.section.domain;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.common.Distance;
import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.Station;

import javax.persistence.*;

@Entity
public class Section extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "up_station_id")
    private Station upStation;

    @ManyToOne
    @JoinColumn(name = "down_station_id")
    private Station downStation;

    @Embedded
    private Distance distance;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "line_id")
    private Line line;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "before_section_id")
    private Section beforeSection;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "next_section_id")
    private Section nextSection;

    protected Section() {
    }

    public Section(Station upStation, Station downStation, int distance) {
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = new Distance(distance);
    }

    public Section(Station upStation, Station downStation, Distance distance, Line line) {
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
        this.line = line;
    }

    public void setLine(Line line) {
        this.line = line;
    }

    private void updateUpStation(Station upStation) {
        this.upStation = upStation;
    }

    private void updateDownStation(Station downStation) {
        this.downStation = downStation;
    }

    private void updateDistance(Distance distance) {
        this.distance = distance;
    }

    private void updateBeforeSection(Section beforeSection) {
        this.beforeSection = beforeSection;
    }

    private void updateNextSection(Section nextSection) {
        this.nextSection = nextSection;
    }
    public void compareDistance(Distance distance) {
        this.distance.compareDistance(distance);
    }

    public Distance minusDistance(Distance distance) {
        return this.distance.minusDistance(distance);
    }

    public void upStationBeforeAdd(Section newSection) {
        updateBeforeSection(newSection);
        newSection.updateNextSection(this);
    }

    public void upStationAfterAdd(Section newSection) {
        Distance distance = newSection.distance();
        compareDistance(distance);
        updateUpStation(newSection.downStation());
        updateDistance(minusDistance(distance));
        updateBeforeSection(newSection);
        newSection.updateNextSection(this);
    }

    public void downStationBeforeAdd(Section newSection) {
        Distance distance = newSection.distance();
        compareDistance(distance);
        updateDownStation(newSection.upStation());
        updateDistance(minusDistance(distance));
        newSection.updateBeforeSection(this);
        updateNextSection(newSection);
    }

    public void downStationAfterAdd(Section newSection) {
        newSection.updateBeforeSection(this);
        updateNextSection(newSection);
    }

    public void upStationAfterDelete(Section deleteSection) {
        distance = distance.plusDistance(deleteSection.distance());
        this.updateDownStation(deleteSection.downStation());
    }

    public Long id() {
        return id;
    }

    public Distance distance() {
        return distance;
    }

    private Distance sumDistance(Distance distance) {
        return new Distance(this.distance.distance() + distance.distance());
    }

    public Station upStation() {
        return upStation;
    }

    public Station downStation() {
        return downStation;
    }

    public Section beforeSection() {
        return beforeSection;
    }

    public Section nextSection() {
        return nextSection;
    }
}
