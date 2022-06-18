package nextstep.subway.domain;

import javax.persistence.*;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;

@Entity
public class Line extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    private String color;

    @Embedded
    private Sections sections = new Sections();

    public static final int MIN_LINE_DISTANCE = 0;

    protected Line() {
    }

    private Line(String name, String color, Station upStation, Station downStation, Integer distance) {
        validateDistance(distance);
        validateUpDownStations(upStation, downStation);

        this.name = name;
        this.color = color;
        sections.addSection(Section.of(upStation, downStation, this, distance));
    }

    private void validateDistance(Integer distance) {
        if (distance <= MIN_LINE_DISTANCE) {
            throw new IllegalArgumentException("지하철 노선의 거리는 양수만 입력해 주세요.");
        }
    }

    private void validateUpDownStations(Station upFinalStation, Station downFinalStation) {
        if (upFinalStation.equals(downFinalStation)) {
            throw new IllegalArgumentException("상행역과 하행역은 같을 수 없습니다.");
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
        section.updateLine(this);
        sections.addSection(section);
    }

    public void removeSectionByStation(Station station) {
        sections.removeSectionByStation(station);
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

    public List<Section> getAllSections() {
        return sections.getSectionsInOrder();
    }

    public Sections getSections() {
        return sections;
    }

    public Section getUpFinalSection() {
        Optional<Section> upFinalSection = sections.geUpFinalSection();
        return upFinalSection
                .orElseThrow(() -> new NoSuchElementException("상행종점 구간이 존재하지 않습니다."));
    }

    public Section getDownFinalSection() {
        Optional<Section> downFinalSection = sections.getDownFinalSection();
        return downFinalSection
                .orElseThrow(() -> new NoSuchElementException("하행종점 구간이 존재하지 않습니다."));
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
