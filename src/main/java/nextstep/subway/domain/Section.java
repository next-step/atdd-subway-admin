package nextstep.subway.domain;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "line_station")
public class Section extends BaseEntity {
    private static final String EXCEPTION_MESSAGE_FOR_OVER_DISTANCE = "기존 역 사이 길이보다 새로운 역의 구간 길이가 깁니다!";
    private static final int DISTANCE_LIMIT = 0;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @Fetch(FetchMode.JOIN)
    @JoinColumn(name = "pre_station_id", foreignKey = @ForeignKey(name = "fk_line_next_station"))
    private Station preStation;
    @ManyToOne(fetch = FetchType.LAZY)
    @Fetch(FetchMode.JOIN)
    @JoinColumn(name = "station_id", foreignKey = @ForeignKey(name = "fk_line_current_station"))
    private Station station;
    private Integer distance;

    protected Section() {
    }

    public Section(Station preStation, Station station, Integer distance) {
        this.preStation = preStation;
        this.station = station;
        this.distance = distance;
    }

    public Long getId() {
        return id;
    }

    public Station getPreStation() {
        return preStation;
    }

    public Station getStation() {
        return station;
    }

    public Integer getDistance() {
        return distance;
    }

    public void updateSection(Section newSection) {
        validateDistance(newSection);
        this.preStation = newSection.station;
        this.distance = this.distance - newSection.distance;
    }

    public void updatePreSection(Section newSection) {
        if (this.preStation != null) {
            validateDistance(newSection);
            this.distance = this.distance - newSection.distance;
        }
        this.station = newSection.preStation;
    }

    private void validateDistance(Section newSection) {
        if (this.distance - newSection.distance <= DISTANCE_LIMIT) {
            throw new IllegalArgumentException(EXCEPTION_MESSAGE_FOR_OVER_DISTANCE);
        }
    }

    public boolean isSame(Section that) {
        return this.equals(that);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Section)) return false;
        Section that = (Section) o;
        return Objects.equals(station, that.station) && Objects.equals(preStation, that.preStation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(station, preStation);
    }
}
