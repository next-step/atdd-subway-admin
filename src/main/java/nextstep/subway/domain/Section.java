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

    public Section(Station upStation, Station downStation, int distance) {
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public Section(Station upStation, Station downStation, Line line, int distance) {
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
        this.line = line;
    }

    public SectionResponse toSectionResponse() {
        return new SectionResponse(id, upStation.getId(), downStation.getId(), distance);
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
            checkSameOtherStation(downStation, infixSection.downStation);
            this.upStation = infixSection.downStation;
        }
        if (isSameDownStation(infixSection)) {
            checkSameOtherStation(upStation, infixSection.upStation);
            this.downStation = infixSection.upStation;
        }
    }

    private void checkSameOtherStation(Station station, Station targetStation) {
        if (station.equalsById(targetStation)) {
            throw new InvalidParameterException("상행역과 하행역이 모두 노선에 등록되어 있는 경우 새롭게 등록할 수 없습니다.");
        }
    }

    private void changedDistance(Section infixSection) {
        this.distance = distance - infixSection.distance;
    }

    private boolean isSameUpStation(Section infixSection) {
        return upStation.equalsById(infixSection.upStation);
    }

    private boolean isSameDownStation(Section infixSection) {
        return downStation.equalsById(infixSection.downStation);
    }

    private void checkDistanceCondition(Section infixSection) {
        if (isLessThanDistance(infixSection)) {
            throw new InvalidParameterException("기존의 역 사이보다 더 긴 길이의 역을 등록할 수 없습니다.");
        }
    }

    private boolean isLessThanDistance(Section infixSection) {
        return infixSection.distance > this.distance;
    }

    public boolean isUpStationEqualsId(Long id) {
        return upStation.getId().equals(id);
    }

    public boolean isDownStationEqualsId(Long id) {
        return downStation.getId().equals(id);
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

}
