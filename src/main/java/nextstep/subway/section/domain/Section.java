package nextstep.subway.section.domain;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.common.exception.MyException;
import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.Station;

import javax.persistence.*;
import java.util.stream.Stream;

@Entity
public class Section extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "line_id")
    private Line line;

    @ManyToOne
    @JoinColumn(name = "up_station_id")
    private Station upStation;

    @ManyToOne
    @JoinColumn(name = "down_station_id")
    private Station downStation;

    private Distance distance;

    protected Section() {
    }

    private Section(Line line, Station upStation, Station downStation, long distance) {
        validate(line, upStation, downStation);
        this.line = line;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = new Distance(distance);
    }

    public static Section of(Line line, Station upStation, Station downStation, long distance) {
        return new Section(line, upStation, downStation, distance);
    }

    public Stream<Station> getStations() {
        return Stream.of(upStation, downStation);
    }

    private void validate(Line line, Station upStation, Station downStation) {
        if (line == null) {
            throw new MyException("지하철 노선 정보가 없습니다.");
        }
        if (upStation == null || downStation == null) {
            throw new MyException("지하철 역 정보가 없습니다.");
        }
    }

    public void updateUpStation(Section section) {
        if (this.distance.isLessThanEqual(section.distance)) {
            throw new MyException("역과 역 사이의 거리보다 좁은 거리를 입력해주세요");
        }
        this.upStation = section.downStation;
        this.distance = new Distance(this.distance.minus(section.distance));
    }

    public void updateDownStation(Section section) {
        if (this.distance.isLessThanEqual(section.distance)) {
            throw new MyException("역과 역 사이의 거리보다 좁은 거리를 입력해주세요");
        }
        this.downStation = section.upStation;
        this.distance = new Distance(this.distance.minus(section.distance));
    }

    public boolean equalUpStation(Station upStation) {
        return this.upStation == upStation;
    }

    public boolean equalDownStation(Station downStation) {
        return this.downStation == downStation;
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

    public boolean isEqualsUpStation(Long stationId) {
        return this.upStation.getId() == stationId;
    }

    public boolean isEqualsDownStation(Long stationId) {
        return this.downStation.getId() == stationId;
    }

    public Section merge(Section downSection) {
        return Section.of(this.line, this.upStation, downSection.getDownStation(), this.distance.plus(downSection.distance));
    }
}
