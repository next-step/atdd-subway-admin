package nextstep.subway.domain;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.util.Objects;

@Embeddable
public class Distance {
    private static final String ERROR_MESSAGE_EXCEEDED_DISTANCE = "기존 노선의 거리보다 작거나 같을 수 없습니다." ;

    private static final String ERROR_MESSAGE_NEGATIVE_DISTANCE = "거리는 0 이상이어야 합니다." ;

    private static final String ERROR_MESSAGE_NULL_DISTANCE = "거리는 필수 값 입니다." ;
    @Column(nullable = false)
    private int distance;

    protected Distance() {
    }

    public Distance(int distance) {
        isEmpty(distance);
        isValidNegative(distance);

        this.distance = distance;
    }

    private void isEmpty(int distance) {
        if(Objects.isNull(distance)){
            throw new IllegalArgumentException(ERROR_MESSAGE_NULL_DISTANCE);
        }
    }

    private void isValidNegative(int distance) {
        if( distance <= 0){
            throw new IllegalArgumentException(ERROR_MESSAGE_NEGATIVE_DISTANCE);
        }
    }

    public int getDistance() {
        return distance;
    }

    public Distance subtract(Distance distance) {

        try{
            return new Distance(this.distance - distance.distance);
        } catch (IllegalArgumentException e){
            throw new IllegalArgumentException(ERROR_MESSAGE_EXCEEDED_DISTANCE) ;
        }

    }
}
