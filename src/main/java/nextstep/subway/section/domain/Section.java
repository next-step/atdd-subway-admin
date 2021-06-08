package nextstep.subway.section.domain;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.Station;

import javax.persistence.*;

@Entity
public class Section extends BaseEntity implements Comparable<Section> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "up_station_id")
    private Station upStation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "down_station_id")
    private Station downStation;

    private int distance;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "line_id")
    private Line line;

    protected Section() {
    }

    private Section(Station upStation, Station downStation, int distance) {
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public static Section create(Station upStation, Station downStation, int distance) {
        return new Section(upStation, downStation, distance);
    }

    public Long getId() {
        return id;
    }

    public Station getUpStation() {
        return upStation;
    }

    public Station getDownStation() {
        return downStation;
    }

    public void setLine(Line line) {
        this.line = line;
    }

    @Override
    public int compareTo(Section section) {
        if (this.upStation.equals(section.getDownStation())) {
            return 1;
        }

        if (this.equals(section)) {
            return 0;
        }
        return 1;
    }

    public boolean equalsUpStation(Section addSection) {
        return this.upStation.equals(addSection.getUpStation());
    }

    public boolean equalsDownStation(Section addSection) {
        return this.downStation.equals(addSection.getDownStation());
    }

    public void changeUpSection(Section addSection) {
        if (this.distance == addSection.distance) {
            throw new IllegalArgumentException("동일한 거리에 역이 존재합니다.");
        }

        reSizeDistance(addSection);
        this.upStation = addSection.downStation;
    }

    public void changeDownSection(Section addSection) {
        if (this.distance == addSection.distance) {
            throw new IllegalArgumentException("동일한 거리에 역이 존재합니다.");
        }

        reSizeDistance(addSection);
        this.downStation = addSection.upStation;
    }

    private void reSizeDistance(Section addSection) {
        this.distance -= addSection.distance;
    }
}
