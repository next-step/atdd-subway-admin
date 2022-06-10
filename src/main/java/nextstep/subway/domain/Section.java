package nextstep.subway.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import org.springframework.dao.DataIntegrityViolationException;

@Entity
public class Section extends BaseEntity {
    public static final String ERROR_MESSAGE_DISTANCE_OVER = "중간 구간의 길이가 상위 구간의 길이보다 길거나 같습니다.";
    public static final String ERROR_MESSAGE_DISTANCE_ZERO_OR_NEGATIVE = "역 간 거리는 0 이하가 될 수 없습니다.";
    public static final String ERROR_MESSAGE_UP_STATION_NULL = "상행역 변수 값이 null 입니다.";
    public static final String ERROR_MESSAGE_DOWN_STATION_NULL = "하행역 변수 값이 null 입니다.";
    public static final String ERROR_MESSAGE_DISTANCE_NULL = "역 간 거리의 값이 null 입니다.";
    public static final String ERROR_MESSAGE_NOT_MATCHED = "업데이트 대상 구간이 겹치지 않는 구간입니다.";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "line_id")
    private Long lineId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "up_station_id")
    private Station upStation;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "down_station_id")
    private Station downStation;
    private Integer distance;

    protected Section() {
    }

    public Section(Long lineId, Station upStation, Station downStation, Integer distance) {
        validateConstructor(upStation, downStation, distance);
        
        this.lineId = lineId;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }
    
    private void validateConstructor(Station upStation, Station downStation, Integer distance) {
        validateNull(upStation, ERROR_MESSAGE_UP_STATION_NULL);
        validateNull(downStation, ERROR_MESSAGE_DOWN_STATION_NULL);
        validateNull(distance, ERROR_MESSAGE_DISTANCE_NULL);

        validateDistanceZeroOrNegative(distance);
    }
    
    private void validateNull(Object object, String errorMessage) {
        if(object == null) {
            throw new DataIntegrityViolationException(errorMessage);
        }
    }
    
    private void validateDistanceZeroOrNegative(Integer distance) {
        if(distance <= 0) {
            throw new DataIntegrityViolationException(ERROR_MESSAGE_DISTANCE_ZERO_OR_NEGATIVE);
        }
    }

    public boolean match(Section section) {
        if (this.upStation.equals(section.getUpStation()) || this.downStation.equals(section.getDownStation())) {
            validateDistanceOver(section);
            return true;
        }

        return false;
    }

    private void validateDistanceOver(Section section) {
        if (distance <= section.getDistance()) {
            throw new DataIntegrityViolationException(ERROR_MESSAGE_DISTANCE_OVER);
        }
    }

    public boolean hasStation(Station station) {
        return equalUpStation(station) || equalDownStation(station);
    }

    public boolean equalUpStation(Station station) {
        if(station == null) {
            return false;
        }

        return upStation.equals(station);
    }

    public boolean equalDownStation(Station station) {
        if(station == null) {
            return false;
        }

        return downStation.equals(station);
    }

    public void updateForDivide(Section newSection) {
        validateDivide(newSection);

        if(upStation.equals(newSection.getUpStation())) {
            upStation = newSection.getDownStation();
        }

        if(downStation.equals(newSection.getDownStation())) {
            downStation = newSection.getUpStation();
        }

        distance = distance - newSection.getDistance();
    }

    private void validateDivide(Section newSection) {
        if(!match(newSection)) {
            throw new DataIntegrityViolationException(ERROR_MESSAGE_NOT_MATCHED);
        }
    }

    public Long getId() {
        return id;
    }

    public Long getLineId() {
        return lineId;
    }

    public Station getUpStation() {
        return upStation;
    }

    public Station getDownStation() {
        return downStation;
    }

    public Integer getDistance() {
        return distance;
    }
}
