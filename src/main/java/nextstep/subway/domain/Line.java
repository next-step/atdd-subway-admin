package nextstep.subway.domain;

import org.springframework.util.StringUtils;

import javax.persistence.*;
import java.util.Objects;

@Entity
public class Line extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false, unique = true)
    private String color;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "up_station_id",nullable = false)
    private Station upStation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "down_station_id",nullable = false)
    private Station downStation;

    @Column(nullable = false)
    private Long distance;

    protected Line() {
    }

    public Line(String name, String color, Long distance) {
        this.name = name;
        this.color = color;
        this.distance = distance;
    }

    public void setStations(Station upStation, Station downStation){
        if(Objects.isNull(upStation) || Objects.isNull(downStation)){
            throw new IllegalArgumentException("지하철역은 비어있을 수 없습니다");
        }
        this.upStation = upStation;
        this.downStation = downStation;
    }

    public Station getUpStation() {
        return this.upStation;
    }

    public Station getDownStation() {
        return this.downStation;
    }

    public Long getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public String getColor() {
        return this.color;
    }

    public Long getDistance() {
        return this.distance;
    }

    public void modifyLine(String name, String color) {
        if(StringUtils.hasText(name) && !this.name.equals(name)){
            this.name = name;
        }
        if(StringUtils.hasText(color) && !this.color.equals(color)){
            this.color = color;
        }
    }
}
