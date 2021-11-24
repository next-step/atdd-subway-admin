package nextstep.subway.line.domain;

import nextstep.subway.common.exception.ServiceException;

import javax.persistence.Embeddable;

@Embeddable
public class Distance {
    private Integer distance;

    protected Distance() {
    }

    public Distance(Integer distance) {
        if (distance == null || distance <= 0) {
            throw new ServiceException("distance는 1이상의 값을 입력하세요");
        }
        this.distance = distance;
    }

    public Integer value() {
        return distance;
    }

    public void minus(Integer distance) {
        int result = this.distance - distance;
        if (result <= 0) {
            throw new ServiceException(String.format("새로 추가되는 길이가 총 길이보다 같거나 큽니다. 총 길이: %d, 추가된 길이: %d", this.distance, distance));
        }
        this.distance = result;
    }
}
