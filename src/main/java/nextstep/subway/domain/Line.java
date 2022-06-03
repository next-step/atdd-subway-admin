package nextstep.subway.domain;

import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.exception.InvalidStringException;
import org.apache.commons.lang3.StringUtils;

@Entity
public class Line extends BaseEntity {

    private static final String INVALID_LINE_NAME = "노선 이름정보가 존재하지 않습니다.";
    private static final String INVALID_LINE_COLOR = "노선 색상정보가 존재하지 않습니다.";
    private static final String INVALID_LINE_UP_STATION = "노선 상행역 정보가 존재하지 않습니다.";
    private static final String INVALID_LINE_DOWN_STATION = "노선 하행역 정보가 존재하지 않습니다.";

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

    @Column
    private Long distance;

    protected Line() {
    }

    public Line(String name, String color, Station upStation, Station downStation, Long distance) {
        validate(name, color, upStation, downStation);
        this.name = name;
        this.color = color;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public void update(String name, String color, Station upStation, Station downStation, Long distance) {
        this.name = name;
        this.color = color;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    private void validate(String name, String color, Station upStation, Station downStation) {
        validateName(name);
        validateColor(color);
        validateUpStation(upStation);
        validateDownStation(downStation);
    }

    private void validateName(String name) {
        if (StringUtils.isEmpty(name)) {
            throw new InvalidStringException(INVALID_LINE_NAME);
        }
    }

    private void validateColor(String color) {
        if (StringUtils.isEmpty(color)) {
            throw new InvalidStringException(INVALID_LINE_COLOR);
        }
    }

    private void validateUpStation(Station upStation) {
        if (Objects.isNull(upStation)) {
            throw new InvalidStringException(INVALID_LINE_UP_STATION);
        }
    }

    private void validateDownStation(Station downStation) {
        if (Objects.isNull(downStation)) {
            throw new InvalidStringException(INVALID_LINE_DOWN_STATION);
        }
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

    public Long getDistance() {
        return distance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Line line = (Line) o;
        return Objects.equals(id, line.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
