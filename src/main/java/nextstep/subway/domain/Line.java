package nextstep.subway.domain;

import java.util.Objects;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import nextstep.subway.exception.NotFoundException;
import org.apache.commons.lang3.StringUtils;

@Entity
public class Line extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true, nullable = false)
    private String name;
    @Column(nullable = false)
    private String color;
    @Column(nullable = false)
    private int distance;
    @ManyToOne(optional = false, cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "UP_STATION_ID", foreignKey = @ForeignKey(name = "fk_line_up_station"))
    private Station upStation;

    @ManyToOne(optional = false, cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "DOWN_STATION_ID", foreignKey = @ForeignKey(name = "fk_line_down_station"))
    private Station downStation;

    protected Line() {
    }

    private Line(LineBuilder lineBuilder) {
        this.name = lineBuilder.name;
        this.color = lineBuilder.color;
        this.upStation = lineBuilder.upStation;
        this.downStation = lineBuilder.downStation;
        this.distance = lineBuilder.distance;
    }

    public static LineBuilder builder(String name, String color, Station upStation, Station downStation, int distance) {
        return new LineBuilder(name, color, upStation, downStation, distance);
    }

    public static class LineBuilder {
        private Long id;
        private final String name;
        private final String color;
        private final Station upStation;
        private final Station downStation;
        private final int distance;

        private LineBuilder(String name, String color, Station upStation, Station downStation, int distance) {
            validateParameter(name, color, upStation, downStation);
            this.name = name;
            this.color = color;
            this.upStation = upStation;
            this.downStation = downStation;
            this.distance = distance;
        }

        public LineBuilder id(Long id) {
            this.id = id;
            return this;
        }

        private void validateParameter(String name, String color, Station upStation, Station downStation) {
            validateNameNotNull(name);
            validateColorNotNull(color);
            validateUpStationNotNull(upStation);
            validateDownStationNotNull(downStation);
        }

        private void validateNameNotNull(String name) {
            if (StringUtils.isEmpty(name)) {
                throw new NotFoundException("이름 정보가 없습니다.");
            }
        }

        private void validateColorNotNull(String color) {
            if (StringUtils.isEmpty(color)) {
                throw new NotFoundException("칼라 정보가 없습니다.");
            }
        }

        private void validateUpStationNotNull(Station upStation) {
            if (Objects.isNull(upStation)) {
                throw new NotFoundException("상행역 정보가 없습니다.");
            }
        }

        private void validateDownStationNotNull(Station downStation) {
            if (Objects.isNull(downStation)) {
                throw new NotFoundException("하행역 정보가 없습니다.");
            }
        }

        public Line build() {
            return new Line(this);
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

    public int getDistance() {
        return distance;
    }
}
