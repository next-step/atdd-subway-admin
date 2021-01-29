package nextstep.subway.section.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.Station;
import nextstep.subway.util.Message;

import javax.persistence.*;
import java.util.Objects;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class Section {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;

    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    private Line line;

    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    private Station upStation;

    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    private Station downStation;

    private int distance;

    @Builder
    public Section(Line line, Station upStation, Station downStation, int distance) {
        this.line = line;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public void update(Station upStation, Station downStation, int distance) {
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public boolean isDownStationInSection(Station newDownStation) {
        return this.downStation.equals(newDownStation);
    }

    public boolean isDownStationInSection(Long stationId) {
        return this.downStation.isSameById(stationId);
    }

    public boolean isUpStationInSection(Station newUpStation) {
        if (this.upStation == null) {
            return false;
        }
        return this.upStation.equals(newUpStation);
    }

    public void updateUpStationToRemove(Station newUpStation, int newDistance) {
        this.upStation = newUpStation;
        if(Objects.isNull(upStation)) {
            this.distance = 0;
            return;
        }
        distance += newDistance;
    }

    public void updateUpStationToDownStation(Station downStation, int distance) {
        this.upStation = downStation;
        updateDistance(distance);
    }

    public void updateDownStationToUpStation(Station upStation, int distance) {
        this.downStation = upStation;
        updateDistance(distance);
    }

    private void updateDistance(int distance) {
        if (this.distance != 0) {
            validateDistance(distance);
            this.distance -= distance;
            return;
        }
        this.distance = distance;
    }

    private void validateDistance(int distance) {
        if (this.distance <= distance) {
            throw new IllegalArgumentException(Message.DISTANCE_EXCESS_MESSAGE);
        }
    }

    public void addLine(Line line) {
        this.line = line;
    }
}
