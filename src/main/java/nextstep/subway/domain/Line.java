package nextstep.subway.domain;

import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;

@Entity
public class Line extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String color;

    @Embedded
    private final Sections sections = new Sections();

    public Sections getSections() {
        return sections;
    }

    public void addSection(Section section) {
        this.sections.addSection(section);
        section.toLine(this);
    }

    public void addFirstSection(Section section) {
        this.sections.addFisrtSection(section);
        section.toLine(this);
    }

    public Line(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public Line() {

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


    public void updateLine(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public List<Station> getStations() {
        List<Station> stations = new ArrayList<>();
        this.sections.getSections().forEach(section -> {
            Station upStation = section.getUpStation();
            Station downStation = section.getDownStation();
            if (!stations.contains(upStation)) {
                stations.add(upStation);
            }

            if (!stations.contains(downStation)) {
                stations.add(downStation);
            }
        });

        return stations;
    }
}
