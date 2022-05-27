package nextstep.subway.domain;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Id;
import org.springframework.util.ObjectUtils;

@Entity
public class Line {
    @Id
    private Long id;
    private String name;
    private Long upStationId;
    private Long downStationId;
    @Embedded
    private Distance distance;

    public Line(String name, Long upStationId, Long downStationId, Distance distance) {
        validName(name);
        this.name = name;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Long getUpStationId() {
        return upStationId;
    }

    public Long getDownStationId() {
        return downStationId;
    }

    public int getDistance() {
        return distance.getDistance();
    }
    private void validName(String name) {
        if (ObjectUtils.isEmpty(name)) {
            throw new IllegalArgumentException("노선의 이름은 필수 입니다.");
        }
    }

}
