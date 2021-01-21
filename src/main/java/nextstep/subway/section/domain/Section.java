package nextstep.subway.section.domain;

import nextstep.subway.common.BaseEntity;
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

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "up_station_id")
    private Station upStation;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "down_station_id")
    private Station downStation;

    @Embedded
    private Distance distance;

    protected Section() {
    }

    private Section(Line line, Station upStation, Station downStation, int distance) {
        this.line = line;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = new Distance(distance);
    }

    public static Section of(Line line, Station upStation, Station downStation, int distance) {
        validate(line, upStation, downStation);
        return new Section(line, upStation, downStation, distance);
    }

    private static void validate(Line line, Station upStation, Station downStation) {
        if(line == null) {
            throw new NullPointerException("지하철 노선 정보가 없습니다");
        }

        if(upStation == null || downStation == null) {
            throw new NullPointerException("지하철 역 정보가 없습니다.");
        }
    }

    public Line getLine() {
        return line;
    }

    public long getId() {
        return id;
    }

    public Station getUpStation() {
        return upStation;
    }

    public Station getDownStation() {
        return downStation;
    }

    public Stream<Station> getStations() {
        return Stream.of(upStation, downStation);
    }

    public void updateUpStation(Section section) {
        if(this.distance.isLessThanEqual(section.distance)) {
            throw new IllegalArgumentException(distance.distance + " 보다 적은 길이를 입력해야 합니다.");
        }

        this.upStation = section.downStation;
        this.distance = new Distance(this.distance.minus(section.distance));
    }

    public void updateDownStation(Section section) {
        if(this.distance.isLessThanEqual(section.distance)) {
            throw new IllegalArgumentException(distance.distance + " 보다 적은 길이를 입력해야 합니다.");
        }

        this.downStation = section.upStation;
        this.distance = new Distance(this.distance.minus(section.distance));
    }

    public boolean hasUpStation(Station downStation) {
        return this.downStation == downStation;
    }

    public boolean hasDownStation(Station upStation) {
        System.out.println("33:"+ this.upStation.getName());
        return this.upStation == upStation;
    }
}
