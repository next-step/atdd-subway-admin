package nextstep.subway.domain;

import javax.persistence.*;
import java.util.List;
import java.util.stream.Stream;

@Entity
public class Section implements Comparable<Section>{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "upStation_id")
    private Station upStation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "downStation_id")
    private Station downStation;

    private int distance;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "line_id")
    Line line;

    protected Section() {}

    public Section(Station upStation, Station downStation, int distance,  Line line) {
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
        this.line = line;
    }

    public Section(Station upStation, Station downStation, int distance) {
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public static Section of(Station upStation, Station downStation, int distance) {
        return new Section(upStation, downStation, distance);
    }

    public void setLine(Line line) {
        this.line = line;
    }

    public boolean isExistSections(List<Station> stations) {
        return stations.contains(upStation) && stations.contains(downStation);
    }

    public boolean isIncludeStation(List<Station> stations) {
        return stations.contains(upStation) || stations.contains(downStation);
    }
    
    public boolean isCheckStation(Section newSection) {
        return checkUpStation(newSection) || checkDownStation(newSection);
    }

    private boolean checkDownStation(Section newSection) {
        return downStation.equals(newSection.downStation);
    }

    private boolean checkUpStation(Section newSection) {
        return upStation.equals(newSection.upStation);
    }

    public Stream<Station> getStations() {
        return Stream.of(upStation, downStation);
    }

    public void changeStation(Section newSection) {
        checkDistance(newSection);

        if (checkUpStation(newSection)) {
            this.upStation = newSection.downStation;
            return;
        }
        this.downStation = newSection.upStation;
    }

    private void checkDistance(Section newSection) {
        if (this.distance <= newSection.distance) {
            throw new IllegalArgumentException("역 사이에 새로운 역과의 길이가 기존 역 사이 길이보다 크거나 같을 수 없습니다.");
        }
        this.distance -= newSection.distance;
    }

    @Override
    public int compareTo(Section o) {
        return this.downStation == o.upStation ? -1 : 1;
    }
}
