package nextstep.subway.section.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nextstep.subway.common.BaseEntity;
import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.Station;
import javax.persistence.*;
import java.util.Objects;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Section extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "line_id")
    private Line line;

    @ManyToOne
    private Station upStation;

    @ManyToOne
    private Station downStation;

    @Embedded
    private Distance distance;

    @Builder
    public Section(Line line, Station upStation, Station downStation, Distance distance) {
        this.line = line;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public void addLine(Line line) {
        this.line = line;
    }

    public boolean isUpStationInSection(Station newUpStation) {
        if (this.upStation == null) {
            return false;
        }
        return this.upStation.equals(newUpStation);
    }

    public boolean isDownStationInSection(Station newDownStation) {
        return this.downStation.equals(newDownStation);
    }

    public boolean isDownStationInSection(Long stationId) {
        return this.downStation.isSameById(stationId);
    }

    public void updateUpStationToDownStation(Station downStation, Distance newDistance) {
        this.upStation = downStation;
        this.distance.updateDistance(newDistance);
    }

    public void updateDownStationToUpStation(Station upStation, Distance newDistance) {
        this.downStation = upStation;
        this.distance.updateDistance(newDistance);
    }

    public void updateUpStationToRemove(Station newUpStation, Distance newDistance) {
        this.upStation = newUpStation;
        if(Objects.isNull(upStation)) {
            this.distance.initDistance();
            return;
        }
        this.distance.addDistance(newDistance);
    }

    public Station getStation() {
        return downStation;
    }

    public int getSectionDistance() {
        return distance.getDistance();
    }
}
