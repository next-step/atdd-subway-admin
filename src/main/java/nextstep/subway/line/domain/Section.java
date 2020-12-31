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

    public void separate(final Section other) {
        if (isSameUpStation(other)) {
            this.upStation = other.downStation;
            this.distance = distance.subtract(other.distance);
        }

        if (isSameDownStation(other)) {
            this.downStation = other.upStation;
            this.distance = distance.subtract(other.distance);
        }
    }

    public boolean canSeparate(final Section other) {
        boolean sameUpStation = isSameUpStation(other);
        boolean sameDownStation = isSameDownStation(other);

        if (sameUpStation && sameDownStation) {
            return false;
        }

        return sameUpStation || sameDownStation;
    }

    private boolean isSameUpStation(final Section other) {
        return other.isUpStation(upStation);
    }

    private boolean isSameDownStation(final Section other) {
        return other.isDownStation(downStation);
    }

    public void merge(final Section other) {
        if (!canMerge(other)) {
            throw new IllegalStateException("병합할 수 없는 구간입니다.");
        }

        if (isConnectUp(other)) {
            this.upStation = other.upStation;
        }

        if (isConnectDown(other)) {
            this.downStation = other.downStation;
        }

        this.distance = this.distance.add(other.distance);
    }

    public boolean canMerge(final Section other) {
        boolean connectUp = isConnectUp(other);
        boolean connectDown = isConnectDown(other);

        if (connectUp && connectDown) {
            return false;
        }

        return connectUp || connectDown;
    }

    private boolean isConnectUp(final Section other) {
        return other.isDownStation(upStation);
    }

    private boolean isConnectDown(final Section other) {
        return other.isUpStation(downStation);
    }
}
