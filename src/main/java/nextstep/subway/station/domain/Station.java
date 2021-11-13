package nextstep.subway.station.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import nextstep.subway.common.BaseEntity;
import nextstep.subway.section.domain.Section;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Station extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String name;

    @JsonIgnore
    @OneToMany(mappedBy = "station")
    private List<Section> sections = new ArrayList<>();

    public Station() {
    }

    public Station(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<Section> getSections() {
        return sections;
    }

    public void addSection(Section section) {
        if (!sections.contains(section)) {
            this.sections.add(section);
        }
    }
}
