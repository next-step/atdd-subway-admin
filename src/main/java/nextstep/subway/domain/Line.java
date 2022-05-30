package nextstep.subway.domain;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import org.apache.commons.lang3.StringUtils;

@Entity
public class Line extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String name;

    @Column(unique = true)
    private String color;

    @ManyToOne
    @JoinColumn(name = "up_station_id")
    private Station upStation;

    @ManyToOne
    @JoinColumn(name = "down_station_id")
    private Station downStation;

    protected Line() {
    }

    public Line(String name, String color, Station upStation, Station downStation) {
        this(null, name, color, upStation, downStation);
    }

    public Line(Long id, String name, String color, Station upStation, Station downStation) {
        validate(name, color, upStation, downStation);
        this.id = id;
        this.name = name;
        this.color = color;
        this.upStation = upStation;
        this.downStation = downStation;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public List<Station> getStations() {
        List<Station> stations = new ArrayList<>();
        stations.add(upStation);
        stations.add(downStation);
        return stations;
    }

    private void validate(String name, String color, Station upStation, Station downStation) {
        if (StringUtils.isEmpty(name)) {
            throw new IllegalArgumentException("지하철 노선의 이름은 필수입니다.");
        }
        if (StringUtils.isEmpty(color)) {
            throw new IllegalArgumentException("지하철 노선의 색깔은 필수입니다.");
        }
        validateStation(upStation, downStation);
    }

    private void validateStation(Station upStation, Station downStation) {
        if (upStation.equals(downStation)) {
            throw new IllegalArgumentException("상행종점역과 하행종점역은 같을 수 없습니다.");
        }
    }
}
