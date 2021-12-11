package nextstep.subway.line.domain;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.section.domain.Section;
import nextstep.subway.station.dto.StationResponse;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Entity
public class Line extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String name;
    private String color;

    @OneToMany(mappedBy = "line", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Section> sections = new ArrayList<>();

    public Line() {
    }

    public Line(String name, String color) {
        this.name = name;
        this.color = color;
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

    public List<Section> getSections() {
        return sections;
    }

    public void addSection(Section section) {
        section.setLine(this);
        sections.add(section);
    }

    public List<StationResponse> getStationResponses() {
        List<StationResponse> stationResponses = new ArrayList<>();
        getSortedSections().forEach(section -> {
            stationResponses.add(StationResponse.of(section.getUpStation()));
            stationResponses.add(StationResponse.of(section.getDownStation()));
        });
        return stationResponses;
    }

    private List<Section> getSortedSections() {
        return sections.stream().sorted(Comparator.comparingInt(Section::getOrderId)).collect(Collectors.toList());
    }
}
