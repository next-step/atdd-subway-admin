package nextstep.subway.domain;

import java.util.List;
import java.util.stream.Stream;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class Section implements Comparable<Section> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private int distance;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "up_station_id", foreignKey = @ForeignKey(name = "fk_section_up_station_to_station"))
    private Station upStation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "down_station_id", foreignKey = @ForeignKey(name = "fk_section_down_station_to_station"))
    private Station downStation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "line_id", foreignKey = @ForeignKey(name = "fk_section_to_line"))
    private Line line;

    protected Section() {
    }

    public Section(Station upStation, Station downStation, int distance, Line line) {
        this.distance = distance;
        this.upStation = upStation;
        this.downStation = downStation;
        this.line = line;
    }

    public Section(Station upStation, Station downStation, int distance) {
        this.distance = distance;
        this.upStation = upStation;
        this.downStation = downStation;
    }

    public static Section of(Station upStation, Station downStation, int distance) {
        return new Section(upStation, downStation, distance);
    }

    public boolean containsStation(Section newSection) {
        return compareUpStation(newSection) || compareDownStation(newSection);
    }

    private boolean compareUpStation(Section newSection) {
        return upStation.equals(newSection.upStation);
    }

    private boolean compareDownStation(Section newSection) {
        return downStation.equals(newSection.downStation);
    }

    public void swapStation(Section newSection) {
        validateDistance(newSection);
        this.distance -= newSection.distance;

        if (compareUpStation(newSection)) {
            this.upStation = newSection.downStation;
            return;
        }
        this.downStation = newSection.upStation;
    }

    private void validateDistance(Section newSection) {
        if (newSection.distance >= this.distance) {
            throw new IllegalArgumentException("입력된 거리가 기존역 사이보다 크거나 같습니다.");
        }
    }

    public Stream<Station> getStations() {
        return Stream.of(upStation, downStation);
    }

    public void setLine(Line line) {
        this.line = line;
    }

    public boolean isExistsSections(List<Station> stations) {
        return stations.contains(upStation) && stations.contains(downStation);
    }

    public boolean isIncludeStation(List<Station> stations) {
        return stations.contains(upStation) || stations.contains(downStation);
    }

    @Override
    public int compareTo(Section other) {
        return this.downStation == other.upStation ? -1 : 1;
    }

    public boolean isUpStation(Station station) {
        return upStation.equals(station);
    }

    public boolean isDownStation(Station station) {
        return downStation.equals(station);
    }

    public void union(Section downSection) {
        this.downStation = downSection.downStation;
        this.distance += downSection.distance;
    }
}
