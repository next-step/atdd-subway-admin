package nextstep.subway.domain;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;

import static nextstep.subway.consts.Constraint.MIN_DISTANCE;

@Entity
public class Line extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    private String color;

    @ManyToOne
    @JoinColumn(name = "up_final_station_id")
    private Station upFinalStation;

    @ManyToOne
    @JoinColumn(name = "down_final_station_id")
    private Station downFinalStation;

    @Embedded
    private Sections sections = new Sections();

    @Column(nullable = false)
    private Integer distance;

    protected Line() {
    }

    private Line(String name, String color, Station upFinalStation, Station downFinalStation, Integer distance) {
        validateDistance(distance);
        validateUpDownStations(upFinalStation, downFinalStation);

        this.name = name;
        this.color = color;
        this.upFinalStation = upFinalStation;
        this.downFinalStation = downFinalStation;
        this.distance = distance;
    }

    private void validateDistance(Integer distance) {
        if (distance <= MIN_DISTANCE) {
            throw new IllegalArgumentException("지하철 노선의 거리는 양수만 입력해 주세요.");
        }
    }

    private void validateUpDownStations(Station upFinalStation, Station downFinalStation) {
        if (upFinalStation.equals(downFinalStation)) {
            throw new IllegalArgumentException("상행종점역과 하행종점역은 같을 수 없습니다.");
        }
    }

    public static Line of(String name, String color, Station upFinalStation, Station downFinalStation, Integer distance) {
        return new Line(name, color, upFinalStation, downFinalStation, distance);
    }

    public void update(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public void addSection(Section section) {
        validateSection(section);
        section.updateLine(this);

        Station newUpStation = section.getUpStation();
        Station newDownStation = section.getDownStation();

        if (sections.isEmpty()) {
            sections.addFirstSection(section);
            return;
        }
        if (upFinalStation.equals(newDownStation)) {
            sections.addUpFinalSection(section);
            updateUpFinalStation(newUpStation);
            return;
        }
        if (downFinalStation.equals(newUpStation)) {
            sections.addDownFinalSection(section);
            updateDownFinalStation(newDownStation);
            return;
        }
        sections.addMiddleSection(section);
    }

    private void validateSection(Section section) {
        Station newUpStation = section.getUpStation();
        Station newDownStation = section.getDownStation();
        Integer newDistance = section.getDistance();

        if (sections.isEmpty()) {
            return;
        }
        if (sections.containsSection(section)) {
            throw new IllegalArgumentException("추가할 구간은 모두 이미 노선에 등록되어 있습니다.");
        }
        if (!sections.containsStation(newUpStation) && !sections.containsStation(newDownStation)) {
            throw new IllegalArgumentException("추가할 구간의 상행역과 하행역 중 하나 이상은 노선에 포함되어 있어야 합니다.");
        }
        if (distance <= newDistance) {
            throw new IllegalArgumentException("추가할 구간의 길이는 노선의 길이보다 작아야 합니다.");
        }
    }

    public void updateDistance(Integer distance) {
        this.distance = distance;
    }

    public void updateUpFinalStation(Station upStation) {
        this.upFinalStation = upStation;
    }

    public void updateDownFinalStation(Station downStation) {
        this.downFinalStation = downStation;
    }

    public void addDistance(Integer distance) {
        this.distance += distance;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public Station getUpFinalStation() {
        return upFinalStation;
    }

    public Station getDownFinalStation() {
        return downFinalStation;
    }

    public Integer getDistance() {
        return distance;
    }

    public Sections getSections() {
        return sections;
    }

    public List<Section> getAllSections() {
        return sections.getSections();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Line line = (Line) o;
        return Objects.equals(id, line.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
