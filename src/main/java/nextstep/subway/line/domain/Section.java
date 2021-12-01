package nextstep.subway.line.domain;

import nextstep.subway.Exception.CannotUpdateSectionException;
import nextstep.subway.station.domain.Station;

import javax.persistence.*;

@Entity
public class Section {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Line line;

    @ManyToOne
    private Station upStation;

    @ManyToOne
    private Station downStation;

    private int distance;

    protected Section() {

    }

    public Section(Line line, Station upStation, Station downStation, int distance) {
        this.line = line;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public Station getUpStation() {
        return upStation;
    }

    public Station getDownStation() {
        return downStation;
    }


    public boolean equalUpStation(Station station) {
        return upStation.equals(station);
    }

    public boolean equalDownStation(Station station) {
        return downStation.equals(station);
    }

    public void updateUpStation(Station downStation, int distance) {
        if (this.distance <= distance) {
            throw new CannotUpdateSectionException("역과 역 사이의 거리보다 좁은 거리를 입력해주세요");
        }
        this.upStation = downStation;
        this.distance -= distance;
    }

    public void updateDownStation(Station upStation, int distance) {
        if (this.distance <= distance) {
            throw new CannotUpdateSectionException("역과 역 사이의 거리보다 좁은 거리를 입력해주세요");
        }
        this.downStation = upStation;
        this.distance -= distance;
    }
}
