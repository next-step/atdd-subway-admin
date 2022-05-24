package nextstep.subway.domain;

import javax.persistence.*;

import static java.util.Objects.requireNonNull;

@Entity
public class Section extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long lineId;

    @ManyToOne
    private Station upStation;

    @ManyToOne
    private Station downStation;

    @Embedded
    private Distance distance;

    public Section(Station upStation, Station downStation, Long distance) {
        this.upStation = requireNonNull(upStation, "상행 지하철역이 비었습니다");
        this.downStation = requireNonNull(downStation, "하행 지하철역이 비었습니다");
        this.distance = new Distance(distance);
    }

    protected Section() {
    }

    public void bindLine(Line line) {
        requireNonNull(line, "지하철 노선이 비었습니다");
        this.lineId = line.getId();
    }

    public boolean isConnectable(Section other) {
        boolean upConnected = isConnectable(this.upStation, other);
        boolean downConnected = isConnectable(this.downStation, other);
        return !((upConnected && downConnected) || (!upConnected && !downConnected));
    }

    private boolean isConnectable(Station station, Section otherSection) {
        return station.equals(otherSection.getUpStation()) || station.equals(otherSection.getDownStation());
    }

    public void relocate(Section section) {
        if (this.upStation.equals(section.upStation) && !this.downStation.equals(section.downStation)) {
            relocateUpStation(section);
            return;
        }
        if (this.downStation.equals(section.downStation) && !this.upStation.equals(section.upStation)) {
            relocateDownStation(section);
            return;
        }
    }

    private void relocateUpStation(Section section) {
        Station tempStation = this.downStation;
        this.downStation = section.downStation;
        section.upStation = this.downStation;
        section.downStation = tempStation;
        distance.minus(section.distance);
    }

    private void relocateDownStation(Section section) {
        this.downStation = section.upStation;
        distance.minus(section.distance);
    }

    public Station getUpStation() {
        return upStation;
    }

    public Station getDownStation() {
        return downStation;
    }
}
