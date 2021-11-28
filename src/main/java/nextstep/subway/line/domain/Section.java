package nextstep.subway.line.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import nextstep.subway.common.BaseEntity;
import nextstep.subway.station.domain.Station;

@Entity
public class Section extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "up_station_id",
        nullable = false,
        foreignKey = @ForeignKey(name = "fk_section_to_up_station")
    )
    private Station upStation;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "down_station_id",
        nullable = false,
        foreignKey = @ForeignKey(name = "fk_section_to_down_station")
    )
    private Station downStation;

    @Column(name = "distance")
    private int distance;

    protected Section() {
    }

    public Section(
        final Station upStation,
        final Station downStation,
        final int distance
    ) {
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public Long getId() {
        return id;
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
