package nextstep.subway.domain;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;

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

    public static final int MIN_LINE_DISTANCE = 0;

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
        if (distance <= MIN_LINE_DISTANCE) {
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

    public Line withSection(Section section) {
        section.updateLine(this);
        sections.addSection(section);
        return this;
    }

    public void addSection(Section section) {
        section.updateLine(this);
        sections.addSection(section);
        sections.resetAllSequences();
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
