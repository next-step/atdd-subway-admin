package nextstep.subway.line.domain;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.section.domain.Section;
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

    @Column(unique = true)
    private String color;

    @OneToMany(mappedBy = "line")
    private List<Section> sections = new ArrayList<>();
//    @Column(unique = true)
//    private Long upStationId;
//
//    @Column(unique = true)
//    private Long downStationId;
//
//    @Column(unique = true)
//    private int distance;

    public Line() {
    }

    public Line(String name, String color, List<Section> sections) {
        this.name = name;
        this.color = color;
        this.sections = sections;
//        this.upStationId = upStationId;
//        this.downStationId = downStationId;
//        this.distance = distance;
    }

    public void update(Line line) {
        this.name = line.getName();
        this.color = line.getColor();
        this.sections = line.getSections();
//        this.upStationId = line.getUpStationId();
//        this.downStationId = line.getDownStationId();
//        this.distance = line.getDistance();
    }

    public List<Section> getSections() {
        return this.sections;
    }

    public List<Station> getStations() {
        List<Station> stations = new ArrayList<>();
        for (int i = 0; i < sections.size(); ++i) {
            stations.add(sections.get(i).getDo);
        }
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

//    public Long getUpStationId() {
//        return upStationId;
//    }
//
//    public Long getDownStationId() {
//        return downStationId;
//    }
//
//    public int getDistance() {
//        return distance;
//    }
}
