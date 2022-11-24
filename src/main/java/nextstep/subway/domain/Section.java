package nextstep.subway.domain;

import nextstep.subway.dto.SectionResponse;
import nextstep.subway.exception.InvalidParameterException;

import javax.persistence.*;

@Entity
public class Section {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "upStationId")
    private Station upStation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "downStationId")
    private Station downStation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lineId")
    private Line line;

    private int distance;

    protected Section() {}

    public Section(Station upStation, Station downStation, Line line, int distance) {
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
        this.line = line;
    }

    public void changed(Section infixSection) {
        if (isSameUpStation(infixSection) || isSameDownStation(infixSection)) {
            checkDistanceCondition(infixSection);
            changedStation(infixSection);
            changedDistance(infixSection);
        }
    }

    private void changedStation(Section infixSection) {
        if (isSameUpStation(infixSection)) {
            this.upStation = infixSection.downStation;
        }
        if (isSameDownStation(infixSection)) {
            this.downStation = infixSection.upStation;
        }
    }

    private void changedDistance(Section infixSection) {
        this.distance = distance - infixSection.distance;
    }

    public boolean isSameUpStation(Section infixSection) {
        return upStation.equalsById(infixSection.upStation);
    }

    public boolean isSameDownStation(Section infixSection) {
        return downStation.equalsById(infixSection.downStation);
    }

    public boolean isPreStation(Section section) {
        return downStation.equalsById(section.getUpStation());
    }

    public boolean isPostStation(Section firstSection) {
        return upStation.equalsById(firstSection.getDownStation());
    }

    private void checkDistanceCondition(Section infixSection) {
        if (isLessThanDistance(infixSection)) {
            throw new InvalidParameterException("기존의 역 사이보다 더 긴 길이의 역을 등록할 수 없습니다.");
        }
    }

    private boolean isLessThanDistance(Section infixSection) {
        return infixSection.distance > this.distance;
    }

    public boolean isUpStationEqualsId(Station station) {
        return upStation.equalsById(station);
    }

    public boolean isDownStationEqualsId(Station station) {
        return downStation.equalsById(station);
    }

    public boolean isContainsPreStation(boolean isContains, Section infixSection) {
        if (isContains) {
            return true;
        }
        if (upStation.equalsById(infixSection.upStation) ||
                downStation.equalsById(infixSection.upStation)) {
            return true;
        }
        return false;
    }

    public boolean isContainsPostStation(boolean isContains, Section infixSection) {
        if (isContains) {
            return true;
        }
        if (upStation.equalsById(infixSection.downStation) ||
                downStation.equalsById(infixSection.downStation)) {
            return true;
        }
        return false;
    }

    public Long getId() {
        return id;
    }

    public int getDistance() {
        return distance;
    }

    public Station getDownStation() {
        return downStation;
    }

    public Station getUpStation() {
        return upStation;
    }

    public Long getUpStationId() {
        return upStation.getId();
    }

    public Long getDownStationId() {
        return downStation.getId();
    }

}
