package nextstep.subway.line.domain;

import lombok.Builder;
import nextstep.subway.station.domain.Station;

import javax.persistence.*;
import java.util.Arrays;
import java.util.List;


@Entity
@Table(indexes = {
        @Index(unique = true, columnList = "up_station_id,down_station_id")
})
public class Section {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Line line;

    @ManyToOne(fetch = FetchType.LAZY)
    private Station upStation;

    @ManyToOne(fetch = FetchType.LAZY)
    private Station downStation;

    @Embedded
    private Distance distance;

    protected Section() {
    }

    @Builder
    private Section(Line line, Station upStation, Station downStation, int distance) {
        validate(line, upStation, downStation, distance);
        this.line = line;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = new Distance(distance);
    }

    public void updateUpStation(Station upStation) {
        this.upStation = upStation;
    }

    public void updateDownStation(Station downStation) {
        this.downStation = downStation;
    }

    private void validate(Line line, Station upStation, Station downStation, int distance) {
        validateRequired(line, upStation, downStation);
    }

    private void validateRequired(Line line, Station upStation, Station downStation) {
        if (line == null || upStation == null || downStation == null) {
            throw new IllegalArgumentException("필수값 누락입니다.");
        }
    }

    public boolean isSameDownStation(Section section) {
        return this.downStation == section.downStation;
    }

    public boolean isSameUpStation(Section section) {
        return this.upStation == section.upStation;
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

    public List<Station> getStations() {
        return Arrays.asList(upStation, downStation);
    }

    public void replaceDownStation(Section section) {
        distance.validateLongerAndEqualsThan(section);
        updateDownStation(section.getUpStation());
        distance.calculateMinus(section.getDistance());

    }

    public void replaceUpStation(Section section) {
        distance.validateLongerAndEqualsThan(section);
        updateUpStation(section.getDownStation());
    }
}
