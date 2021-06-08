package nextstep.subway.line.domain;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.station.domain.Station;

import javax.persistence.*;

@Entity
public class Section extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "line_id")
    private Line line;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "up_station_id")
    private Station upStation;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "down_station_id")
    private Station downStation;

    private int distance;

    private static final int MINIMUN_NEW_DISTANCE = 0;

    protected Section() {
    }

    public Section(Line line, Station upStation, Station downStation, int distance) {
        this.line = line;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public boolean hasSameUpStation(Station station) {
        return station == upStation;
    }

    public boolean hasSameDownStation(Station station) {
        return station == downStation;
    }

    public static Section makeAfterSection(Section preSection, Section section) {
        int calculatedDistance = calculateNewDistance(preSection, section);
        checkDistance(calculatedDistance);
        return new Section(preSection.line, section.downStation, preSection.downStation,
                calculatedDistance);
    }

    public static Section makeBeforeSection(Section preSection, Section section) {
        int calculatedDistance = calculateNewDistance(preSection, section);
        checkDistance(calculatedDistance);
        return new Section(preSection.line, preSection.upStation, section.upStation,
                calculatedDistance);
    }

    private static int calculateNewDistance(Section preSection, Section section) {
        return preSection.distance - section.distance;
    }

    private static void checkDistance(int calculatedDistance) {
        if (calculatedDistance <= MINIMUN_NEW_DISTANCE) {
            throw new IllegalArgumentException("기존 구간 내부에 구간 등록시 distance는 기존 구간보다 작아야 합니다.");
        }
    }

    public boolean isEqualsUpStation(Section section) {
        return this.upStation.equals(section.upStation);
    }

    public boolean isEqualsDownStation(Section section) {
        return this.downStation.equals(section.downStation);
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
