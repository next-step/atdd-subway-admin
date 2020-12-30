package nextstep.subway.line.domain;

import lombok.*;
import nextstep.subway.line.exception.SectionDuplicatedException;
import nextstep.subway.station.domain.Station;

import javax.persistence.*;
import java.util.Objects;

@Getter
@EqualsAndHashCode(of = {"upStation", "downStation"})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class Section {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "up_station_id")
    private Station upStation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "down_station_id")
    private Station downStation;

    @Column(name = "distance")
    private Distance distance;

    @Builder
    private Section(final Station upStation, final Station downStation, final Distance distance) {
        if (upStation.equals(downStation)) {
            String message = String.format("구간의 상행과 하행은 같은 역일 수 없습니다. (입력 역:%s)", upStation.getName());
            throw new SectionDuplicatedException(message);
        }

        this.upStation = Objects.requireNonNull(upStation);
        this.downStation = Objects.requireNonNull(downStation);
        this.distance = Objects.requireNonNull(distance);
    }

    public boolean isUpStation(final Station station) {
        return this.upStation.equals(station);
    }

    public boolean isDownStation(final Station station) {
        return this.downStation.equals(station);
    }

    public void update(final Section other) {
        if (canConnectUpStation(other)) {
            this.upStation = other.downStation;
            this.distance = distance.subtract(other.distance);
        }

        if (canConnectDownStation(other)) {
            this.downStation = other.upStation;
            this.distance = distance.subtract(other.distance);
        }
    }

    private boolean canConnectUpStation(final Section other) {
        return other.isUpStation(upStation);
    }

    private boolean canConnectDownStation(final Section other) {
        return other.isDownStation(downStation);
    }

    public boolean canAddBetweenSection(final Section other) {
        boolean connectUpStation = canConnectUpStation(other);
        boolean connectDownStation = canConnectDownStation(other);

        if (canConnectAll(connectUpStation, connectDownStation)) {
            return false;
        }

        return canConnectOne(connectUpStation, connectDownStation);
    }

    private boolean canConnectAll(final boolean connectUpStation, final boolean connectDownStation) {
        return connectUpStation && connectDownStation;
    }

    private boolean canConnectOne(final boolean connectUpStation, final boolean connectDownStation) {
        return connectUpStation || connectDownStation;
    }
}
