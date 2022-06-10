package nextstep.subway.domain;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.ObjectUtils;

import javax.persistence.*;

@Entity
public class Line extends BaseEntity {
    private static final String ERROR_MSG_NAME_EMPTY = "노선 이름 정보가 존재하지 않습니다.";
    private static final String ERROR_MSG_UP_STATION_EMPTY = "노선 상행종점역 정보가 존재하지 않습니다.";
    private static final String ERROR_MSG_DOWN_STATION_EMPTY = "노선 하행종점역 정보가 존재하지 않습니다.";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String name;
    @Column
    private String color;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "up_station_id", foreignKey = @ForeignKey(name = "fk_line_up_station"))
    private Station upStation;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "down_station_id", foreignKey = @ForeignKey(name = "fk_line_down_station"))
    private Station downStation;

    public Line() {
    }

    public Line(String name, String color, Station upStation, Station downStation) {
        validate(name, upStation, downStation);
        this.name = name;
        this.color = color;
        this.upStation = upStation;
        this.downStation = downStation;
    }

    private void validate(String name, Station upStation, Station downStation) {
        validateName(name);
        validateUpStation(upStation);
        validateDownStation(downStation);
    }

    private void validateName(String name) {
        if (StringUtils.isEmpty(name)) {
            throw new IllegalArgumentException(ERROR_MSG_NAME_EMPTY);
        }
    }

    private void validateUpStation(Station upStation) {
        if (ObjectUtils.isEmpty(upStation)) {
            throw new IllegalArgumentException(ERROR_MSG_UP_STATION_EMPTY);
        }
    }

    private void validateDownStation(Station downStation) {
        if (ObjectUtils.isEmpty(downStation)) {
            throw new IllegalArgumentException(ERROR_MSG_DOWN_STATION_EMPTY);
        }
    }

    public void update(Line newLine) {
        this.name = newLine.getName();
        this.color = newLine.getColor();
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

    public Station getUpStation() {
        return upStation;
    }

    public Station getDownStation() {
        return downStation;
    }
}
