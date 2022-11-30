package nextstep.subway.domain;

import java.util.List;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Line extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String name;

    @Column(unique = true, nullable = false)
    private String color;

    @Embedded
    private Sections sections;

    protected Line() {
    }

    private Line(Long id, String name, String color, int distance, Station upStation, Station downStation) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.sections = new Sections(new Section(upStation, downStation, distance, this));
    }

    public static Line of(String name, String color, int distance, Station upStation, Station downStation) {
        return new Line(null, name, color, distance, upStation, downStation );
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getColor(){
        return color;
    }

    public void updateNameColor(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public void addSection(Section section) {
        validateSection(section);
        sections.add(section);
        section.setLine(this);
    }

    public List<Station> getStations() {
        return sections.getStations();
    }

    private boolean checkExistSection(Section section){
        return section.isExistsSections(sections.getStations());
    }

    private boolean checkIncludeStation(Section section){
        return section.isIncludeStation(sections.getStations());
    }
    private void validateSection(Section newSection) {
        if (checkExistSection(newSection)) {
            throw new IllegalArgumentException("상행역과 하행역이 모두 노선에 등록되어 있습니다.");
        }
        if (!checkIncludeStation(newSection)) {
            throw new IllegalArgumentException("상행역과 하행역 둘 중 하나도 포함되어있지 않습니다.");
        }
    }

    public void removeStation(Station station) {
        sections.removeStation(station);
    }
}
