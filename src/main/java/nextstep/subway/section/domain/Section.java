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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "line_id", foreignKey = @ForeignKey(name = "fk_section_to_line"))
    private Line line;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "upStation_id", foreignKey = @ForeignKey(name = "fk_section_to_upStation"))
    private Station upStation = new Station();

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "downStation_id", foreignKey = @ForeignKey(name = "fk_section_to_downStation"))
    private Station downStation = new Station();

    @Embedded
    private Distance distance;

    protected Section() {
    }

    public Section(Station upStation, Station downStation, Distance distance) {
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public boolean isUpFinalSection(Section newSection) {
        return newSection.isSameDownStation(upStation);
    }

    public boolean isDownFinalSection(Section newSection) {
        return newSection.isSameUpStation(downStation);
    }

    public void checkSameUpStationAndDownStation(Section newSection) {
        if(newSection.isSameUpStation(upStation) && newSection.isSameDownStation(downStation)){
            throw new IllegalArgumentException("상행역과 하행역이 모두 동일한 구간은 추가할 수 없습니다.");
        }
    }

    public boolean isUpSection(Section newSection) {
        if(newSection.isSameUpStation(upStation)){
            newSection.checkShortDistance(distance);
            this.upStation = newSection.getDownStation();
            return true;
        }
        return false;
    }

    public boolean isDownSection(Section newSection) {
        if(newSection.isSameDownStation(downStation)){
            newSection.checkShortDistance(distance);
            this.downStation = newSection.getUpStation();
            return true;
        }
        return false;
    }

    private void checkShortDistance(Distance originDistance) {
        if (!distance.isShortDistance(originDistance)) {
            throw new IllegalArgumentException("신규 구간은 기존 구간보다 길이가 짧아야 합니다.");
        }
        originDistance.adjustmentDistance(distance);
    }

    private boolean isSameUpStation(Station originUpStation) {
        return this.upStation.equals(originUpStation);
    }

    private boolean isSameDownStation(Station originDownStation) {
        return this.downStation.equals(originDownStation);
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

    public Distance getDistance() {
        return distance;
    }

    public void setLine(Line line) {
        this.line = line;
    }
}
