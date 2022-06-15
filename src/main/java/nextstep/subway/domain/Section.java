package nextstep.subway.domain;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;

@Entity
public class Section extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "up_station_id", nullable = false)
    private Station upStation;

    @ManyToOne
    @JoinColumn(name = "down_station_id", nullable = false)
    private Station downStation;

    @ManyToOne
    @JoinColumn(name = "line_id", nullable = false)
    private Line line;

    private Integer distance;

    public static final int MIN_SECTION_DISTANCE = 0;

    protected Section() {
    }

    private Section(Station upStation, Station downStation, Line line, Integer distance) {
        validateDistance(distance);
        validateUpDownStations(upStation, downStation);

        this.upStation = upStation;
        this.downStation = downStation;
        this.line = line;
        this.distance = distance;
    }

    public static Section of(Station upStation, Station downStation, Line line, Integer distance) {
        return new Section(upStation, downStation, line, distance);
    }

    public static Section of(Station upStation, Station downStation, Integer distance) {
        return new Section(upStation, downStation, null, distance);
    }

    private void validateDistance(Integer distance) {
        if (distance <= MIN_SECTION_DISTANCE) {
            throw new IllegalArgumentException("지하철 구간의 거리는 양수만 입력해 주세요.");
        }
    }

    private void validateUpDownStations(Station upFinalStation, Station downFinalStation) {
        if (upFinalStation.equals(downFinalStation)) {
            throw new IllegalArgumentException("상행역과 하행역은 같을 수 없습니다.");
        }
    }

    public void updateLine(Line line) {
        this.line = line;
    }

    public boolean matchUpAndDownStation(List<Section> sections) {
        return sections.stream().anyMatch(section -> section.containsStation(upStation))
                && sections.stream().anyMatch(section -> section.containsStation(downStation));
    }

    public boolean matchUpOrDownStation(List<Section> sections) {
        return sections.stream().anyMatch(section -> section.containsStation(upStation))
                || sections.stream().anyMatch(section -> section.containsStation(downStation));
    }

    public boolean containsStation(Station station) {
        return upStation.equals(station) || downStation.equals(station);
    }

    public boolean isBeforeUpFinalSection() {
        Section upFinalSection = line.getUpFinalSection();
        return downStation.equals(upFinalSection.upStation);
    }

    public boolean isAfterDownFinalSection() {
        Section downFinalSection = line.getDownFinalSection();
        return upStation.equals(downFinalSection.downStation);
    }

    public boolean hasSameUpStation(Section section) {
        return upStation.equals(section.upStation);
    }

    public boolean isLongerThan(Section section) {
        return this.distance >= section.distance;
    }

    public void updateUpStation(Section section) {
        this.upStation = section.downStation;
        this.distance = this.distance - section.distance;
    }

    public Long getId() {
        return id;
    }

    public Station getUpStation() {
        return upStation;
    }

    public Station getDownStation() {
        return downStation;
    }

    public Line getLine() {
        return line;
    }

    public Integer getDistance() {
        return distance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Section section = (Section) o;
        return Objects.equals(id, section.id)
                && Objects.equals(upStation, section.upStation)
                && Objects.equals(downStation, section.downStation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, upStation, downStation);
    }
}
