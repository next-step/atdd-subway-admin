package nextstep.subway.line.domain;

import java.util.Arrays;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.exception.BadRequestException;
import nextstep.subway.station.domain.Station;

@Entity
public class Section extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Station upStation;

    @ManyToOne
    private Station downStation;

    private int distance;

    protected Section() {
    }

    public Section(final Station upStation, final Station downStation, final int distance) {
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    boolean isNextSection(Section section) {
        return upStation.equals(section.downStation);
    }

    boolean isOverlapped(Section section) {
        return upStation.equals(section.getUpStation()) || downStation.equals(section.getDownStation());
    }

    void divideBy(Section section) {
        if (!isOverlapped(section)) {
            throw new IllegalArgumentException("section must be overlapped to divide");
        }
        if (section.getDistance() >= distance) {
            throw new BadRequestException("추가되는 구간의 길이가 기존 역 사이의 길이보다 크거나 같을 수 없습니다.");
        }
        if (upStation.equals(section.getUpStation())) {
            upStation = section.getDownStation();
        }
        if (downStation.equals(section.getDownStation())) {
            downStation = section.getUpStation();
        }
        distance = distance - section.getDistance();
    }

    void connectWith(final Section section) {
        if (!isNextSection(section) && !section.isNextSection(this)) {
            throw new IllegalArgumentException("section cannot be connected");
        }
        if (isNextSection(section)) {
            upStation = section.getUpStation();
        }
        if (section.isNextSection(this)) {
            downStation = section.getDownStation();
        }
        distance = distance + section.getDistance();
    }

    List<Station> getStations() {
        return Arrays.asList(upStation, downStation);
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
        return distance;
    }
}
