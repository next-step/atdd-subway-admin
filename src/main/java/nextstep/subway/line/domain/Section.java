package nextstep.subway.line.domain;

import nextstep.subway.station.domain.Station;

import javax.persistence.*;

import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.IDENTITY;
import static nextstep.subway.line.application.exception.InvalidSectionException.error;

@Entity
public class Section {

    public static final String SECTION_DUPLICATION = "같은 상행역과 하행역으로 등록된 구간이 이미 존재합니다.";

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(nullable = false)
    private Station upStation;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(nullable = false)
    private Station downStation;

    @Embedded
    private Distance distance;

    @ManyToOne(fetch = FetchType.LAZY)
    private Line line;

    protected Section() {
    }

    private Section(Station upStation, Station downStation, int distance) {
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = new Distance(distance);
    }

    public static Section of(Station upStation, Station downStation, int distance) {
        return new Section(upStation, downStation, distance);
    }

    public boolean isConnectable(Section section) {
        if (isDuplicate(section)) {
            throw error(SECTION_DUPLICATION);
        }

        return isTerminusExtend(section) || isBetweenStations(section);
    }

    public Section connect(Section section) {
        if (isBetweenStations(section) && distance.divisible(section)) {
            changeStationLink(section);
            distance.minus(section.getDistance());
        }
        return section;
    }

    private void changeStationLink(Section section) {
        if (upStation.equals(section.upStation)) {
            upStation = section.downStation;
        }

        if (downStation.equals(section.downStation)) {
            downStation = section.upStation;
        }
    }

    private boolean isDuplicate(Section section) {
        return upStation.equals(section.upStation) && downStation.equals(section.downStation);
    }

    private boolean isTerminusExtend(Section section) {
        return upStation.equals(section.downStation) || downStation.equals(section.upStation);
    }

    public boolean isBetweenStations(Section section) {
        return upStation.equals(section.upStation) || downStation.equals(section.downStation);
    }

    public void setLine(Line line) {
        this.line = line;
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

    public int getDistance() {
        return distance.getDistance();
    }

    public Line getLine() {
        return line;
    }
}
