package nextstep.subway.line.domain;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.lineStation.domain.LineStation;
import nextstep.subway.section.domain.Section;
import nextstep.subway.section.domain.Sections;
import nextstep.subway.station.domain.Station;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Line extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String name;

    private String color;

    @Embedded
    private Sections sections = new Sections();

    @OneToMany(mappedBy = "line", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LineStation> lineStations = new ArrayList<>();

    protected Line() {
    }

    public Line(String name, String color, Section section) {
        this.name = name;
        this.color = color;
        this.addSection(section);
    }

    public void addSection(Section section) {
        this.sections.addSection(section);
        section.setLine(this);
    }

    public void addLineStation(LineStation lineStation){
        this.lineStations.add(lineStation);
        lineStation.setLine(this);
    }

    public void update(Line line) {
        this.name = line.getName();
        this.color = line.getColor();
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

    public List<Station> getStationList(){
        return sections.getStationList();
    }

    public void updateLine(Line updateLine) {
        this.name = updateLine.getName();
        this.color = updateLine.getColor();
        this.sections.getSection().clear();
        updateLine.getSections().getSection()
                .forEach(station -> this.addSection(station));
    }
}
