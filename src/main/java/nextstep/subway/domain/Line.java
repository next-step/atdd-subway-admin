package nextstep.subway.domain;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Embeddable
public class Line extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String name;
    @Column(unique = true)
    private String color;
    @Embedded
    private Stations stations = new Stations();
    @OneToMany(mappedBy = "line")
    private List<Section> sections = new ArrayList<>();

    protected Line() {
    }

    public Line(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public Line(String name, String color, Section section) {
        this.name = name;
        this.color = color;
        addSection(section);
    }

    private void addSection(Section section) {
        this.sections.add(section);
    }

    public Long getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public String getColor() {
        return this.color;
    }

    private void setName(String name) {
        this.name = name;
    }

    private void setColor(String color) {
        this.color = color;
    }

    public List<Station> getStations() {
        return stations.asList();
    }

    public void modify(String name, String color) {
        setName(name);
        setColor(color);
    }
}
