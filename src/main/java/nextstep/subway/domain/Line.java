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
        this.id = lineBuilder.id;
        this.name = lineBuilder.name;
        this.color = lineBuilder.color;
        this.distance = lineBuilder.distance;
    }

    public static LineBuilder builder(String name, String color, int distance) {
        return new LineBuilder(name, color, distance);
    }

    public static class LineBuilder {
        private Long id;
        private final String name;
        private final String color;
        private final int distance;

        private LineBuilder(String name, String color, int distance) {
            validateParameter(name, color);
            this.name = name;
            this.color = color;
            this.distance = distance;
        }

        public LineBuilder id(Long id) {
            this.id = id;
            return this;
        }

        private void validateParameter(String name, String color) {
            validateNameNotNull(name);
            validateColorNotNull(color);
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

        public Line build() {
            return new Line(this);
        }
    }

    public Line addUpStation(Station upStation) {
        validateUpStationNotNull(upStation);
        this.upStation = upStation;
        return this;
    }

    public Line addDownStation(Station downStation) {
        validateDownStationNotNull(downStation);
        this.downStation = downStation;
        return this;
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
