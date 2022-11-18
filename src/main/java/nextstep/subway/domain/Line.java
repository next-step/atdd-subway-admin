package nextstep.subway.domain;

import java.util.List;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import nextstep.subway.dto.LineEditRequest;

@Entity
public class Line extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String name;

    @Column(unique = true)
    private String color;

    @Embedded
    private Sections sections = new Sections();

    protected Line(){}

    public Line(String name, String color, Station preStation, Station station, Integer distance) {
        this(name, color);
        sections.add(null, preStation, 0);
        sections.add(preStation, station, distance);
    }

    public Line(String name, String color) {
        this(null, name, color);
    }

    public Line(Long id, String name, String color) {
        this.id = id;
        this.name = name;
        this.color = color;
    }

    public void editLine(LineEditRequest lineEditRequest){
        this.name = lineEditRequest.getName();
        this.color = lineEditRequest.getColor();
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

    public void addSection(Station preStation, Station station, Integer distance) {
        sections.addSection(preStation, station, distance);
    }

    public List<Section> getOrderStations(){
        return sections.getOrderStations();
    }

    public Sections getSections() {
        return sections;
    }
}
