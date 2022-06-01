package nextstep.subway.domain;

import static nextstep.subway.message.ErrorMessage.SECTION_HAS_DISTANCE_STATION_ESSENTIAL;
import static nextstep.subway.message.ErrorMessage.SECTION_HAS_DOWN_STATION_ESSENTIAL;
import static nextstep.subway.message.ErrorMessage.SECTION_HAS_UP_STATION_ESSENTIAL;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;


@Entity
public class Section {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "upStationId")
    private Station upStation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "downStationId")
    private Station downStation;

    @Embedded
    private Distance distance;

    protected Section() {

    }

    private Section(Station upStation, Station downStation, Distance distance) {
        valid(upStation, downStation, distance);
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    private void valid(Station upStation, Station downStation, Distance distance) {
        if (upStation == null) {
            throw new IllegalArgumentException(SECTION_HAS_UP_STATION_ESSENTIAL.toMessage());
        }
        if (downStation == null) {
            throw new IllegalArgumentException(SECTION_HAS_DOWN_STATION_ESSENTIAL.toMessage());
        }
        if (distance == null) {
            throw new IllegalArgumentException(SECTION_HAS_DISTANCE_STATION_ESSENTIAL.toMessage());
        }
    }

    public Station getUpStation() {
        return upStation;
    }

    public Station getDownStation() {
        return downStation;
    }

    public Distance getDistance() {
        return distance;
    }

    public static class Builder {
        private Station upStation;
        private Station downStation;
        private Distance distance;

        public Builder upStation(Station station) {
            this.upStation = station;
            return this;
        }

        public Builder downStation(Station station) {
            this.downStation = station;
            return this;
        }

        public Builder distance(int distance) {
            this.distance = Distance.of(distance);
            return this;
        }

        public Builder distance(Distance distance) {
            this.distance = distance;
            return this;
        }

        public Section build() {
            return new Section(upStation, downStation, distance);
        }
    }

    public static Builder builder() {
        return new Builder();
    }

}
