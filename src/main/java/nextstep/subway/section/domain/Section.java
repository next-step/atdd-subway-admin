package nextstep.subway.section.domain;

import lombok.*;
import nextstep.subway.common.BaseEntity;
import nextstep.subway.station.domain.Station;

import javax.persistence.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Section extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "up_station_id")
    private Station upStation;

    @ManyToOne
    @JoinColumn(name = "down_station_id")
    private Station downStation;

    @Embedded
    private Distance distance;

    @Builder
    public Section(Station upStation, Station downStation, Distance distance) {
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public void updateUpStation(Section newSection) {
        this.upStation = newSection.getDownStation();
        this.distance = this.distance.minus(newSection.getDistance());
    }

    public void updateDownStation(Section newSection) {
        this.downStation = newSection.getUpStation();
        this.distance = this.distance.minus(newSection.getDistance());
    }

    public boolean equalUpUpStation(Section otherSection) {
        return upStation.equals(otherSection.getUpStation());
    }

    public boolean equalDownDownStation(Section otherSection) {
        return downStation.equals(otherSection.getDownStation());
    }

    public boolean equalUpStation(Station station) {
        return upStation.equals(station);
    }

    public boolean equalDownStation(Station station) {
        return downStation.equals(station);
    }
}
