package nextstep.subway.domain;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import org.springframework.util.ObjectUtils;

@Entity
public class Line extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String color;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "upStationId")
    private Station upStation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "downStationId")
    private Station downStation;

    @Embedded
    private Distance distance;

    protected Line() {

    }

    public Line(String name, String color, Distance distance) {
        valid(name, color);
        this.name = name;
        this.color = color;
        this.distance = distance;
    }
    public Line(String name, String color, Station upStation, Station downStation, Distance distance) {
        valid(name, color);
        this.name = name;
        this.color = color;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public Line withUpStation(Station upStation) {
        this.upStation = upStation;
        return this;
    }

    public Line withDownStation(Station downStation) {
        this.downStation = downStation;
        return this;
    }


    private void valid(String name, String color) {
        validEmpty(name, "노선의 이름은 필수 입니다.");
        validEmpty(color, "색상은 필수 입니다.");
    }

    private void validEmpty(String name, String msg) {
        if (ObjectUtils.isEmpty(name)) {
            throw new IllegalArgumentException(msg);
        }
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Station getDownStation() {
        return downStation;
    }

    public Station getUpStation() {
        return upStation;
    }

    public int getDistance() {
        return distance.getDistance();
    }

    public String getColor() {
        return color;
    }


}
