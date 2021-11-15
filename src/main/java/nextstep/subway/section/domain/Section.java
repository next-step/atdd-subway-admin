package nextstep.subway.section.domain;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import nextstep.subway.station.domain.Station;

@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"up_station", "down_station"}))
@Entity
public class Section {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "up_station")
    private Station upStation;

    @OneToOne
    @JoinColumn(name = "down_station")
    private Station downStation;

    @Embedded
    private Distance distance;

    private Section(Station upStation, Station downStation, Distance distance) {
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public static Section valueOf(Station upStation, Station downStation, Distance distance) {
        return new Section(upStation, downStation, distance);
    }

    public Long getId() {
        return this.id;
    }

    public Station getUpStation() {
        return this.upStation;
    }

    public Station getDownStation() {
        return this.downStation;
    }

    public Distance getDistance() {
        return this.distance;
    }
}
