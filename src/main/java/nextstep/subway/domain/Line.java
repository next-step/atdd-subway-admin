package nextstep.subway.domain;

import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import static nextstep.subway.common.Messages.NOT_FOUND_SECTION;

@Entity
@Where(clause = "deleted=false")
public class Line {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String color;

    @Embedded
    private final Sections sections = new Sections();

    private Boolean deleted = false;

    protected Line() {

    }

    public Line(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public static Line of(String name, String color, Section section) {
        Line line = new Line(name, color);
        line.addSection(section);
        return line;
    }

    public void update(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public void delete() {
        this.deleted = true;
    }

    public boolean isDelete() {
        return this.deleted;
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

    public Sections getSections() {
        return sections;
    }

    public Section getFirstSection() {
        List<Station> upStation = new ArrayList<>();
        List<Station> downStation = new ArrayList<>();
        sections.getSections().forEach(section -> {
            upStation.add(section.getUpStation());
            downStation.add(section.getDownStation());
        });

        upStation.removeAll(downStation);
        return findFirstSection(upStation.get(0));
    }

    public List<Station> sortByStation() {
        List<Station> stations = new ArrayList<>();
        Section section = getFirstSection();

        stations.add(section.getUpStation());
        stations.add(section.getDownStation());

        // 다음역 찾기
        while (NextSection(section) != null) {
            Section nextSection = NextSection(section);
            stations.add(nextSection.getDownStation());
            section = nextSection;
        }

        return stations;
    }

    private Section findFirstSection(Station station) {
        return sections.getSections().stream()
                .filter(section -> section.getUpStation().equals(station))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException(NOT_FOUND_SECTION));
    }

    private Section NextSection(Section firstSection) {
        return sections.getSections().stream()
                .filter(section -> section.isEqualsUpStation(firstSection.getDownStation()))
                .findFirst().orElse(null);
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void addSection(Section section) {
        sections.addSections(section);
        section.setLine(this);
    }
}
