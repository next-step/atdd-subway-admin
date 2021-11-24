package nextstep.subway.line.domain;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.station.domain.Station;

import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.util.Objects;

@Entity
@Embeddable
@Table(
        indexes = @Index(
                name = "up_station_id_down_station_id_unique_index",
                columnList = "upStationId,downStationId",
                unique = true
        )
)
public class Section extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private static final String UP_STATION_NOT_NULL_ERROR_MESSAGE = "상행역은 빈값일 수 없습니다.";
    private static final String DOWN_STATION_NOT_NULL_ERROR_MESSAGE = "하행역은 빈값일 수 없습니다.";
    @Embedded
    private Distance distance;

    @ManyToOne(optional = false)
    @JoinColumn(name = "upStationId", foreignKey = @ForeignKey(name = "fk_section_up_station"))
    private Station upStation;

    @ManyToOne(optional = false)
    @JoinColumn(name = "downStationId", foreignKey = @ForeignKey(name = "fk_section_down_station"))
    private Station downStation;

    protected Section() {
    }

    public Section(int distance, Station upStation, Station downStation) {
        validate(upStation, downStation);
        this.distance = Distance.of(distance);
        this.upStation = upStation;
        this.downStation = downStation;
    }

    private void validate(Station upStation, Station downStation) {
        if (Objects.isNull(upStation)) {
            throw new IllegalArgumentException(UP_STATION_NOT_NULL_ERROR_MESSAGE);
        }
        if (Objects.isNull(downStation)) {
            throw new IllegalArgumentException(DOWN_STATION_NOT_NULL_ERROR_MESSAGE);
        }
    }

    public static Section of(int distance, Station upStation, Station downStation) {
        return new Section(distance, upStation, downStation);
    }

    public Station getUpStation() {
        return upStation;
    }

    public Station getDownStation() {
        return downStation;
    }
    public Long getId() {
        return id;
    }
    @Override
    public String toString() {
        return "Section{" +
                "distance=" + distance +
                ", upStation=" + upStation +
                ", downStation=" + downStation +
                "}\n";
    }
}
