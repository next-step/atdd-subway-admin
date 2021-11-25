package nextstep.subway.line.domain;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.section.domain.Section;
import nextstep.subway.station.dto.StationResponse;

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
    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL)
    private List<Section> sections = new ArrayList<>();


    public Line() {
    }

    public Line(String name, String color, Section section) {
        this.name = name;
        this.color = color;
        addSection(section);
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

    public List<Section> getSections() {
        return sections;
    }

    public void update(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public void addSection(Section section) {
        sections.add(section);
        section.setLine(this);
    }

    public List<StationResponse> getStationResponse() {
        List<StationResponse> stationResponses = new ArrayList<>();

        for (int i = 0; i < sections.size(); i++) {
            stationResponses.add(StationResponse.of(sections.get(i).getUpStation()));
            stationResponses.add(StationResponse.of(sections.get(i).getDownStation()));
        }

        return stationResponses;
    }
}
