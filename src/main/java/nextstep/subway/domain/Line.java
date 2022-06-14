package nextstep.subway.domain;

import org.apache.commons.lang3.StringUtils;

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

    protected Line() {

    }

    public Line(String name, String color, Section section) {
        this.name = name;
        this.color = color;
        sections.addInitialSection(section);
    }


    public void update(String name, String color) {
        validateName(name);
        this.name = name;
        this.color = color;
    }

    public void addSection(Section section) {
        sections.add(section);
    }

    public Station getUpStation() {
        return sections.getUpStation();
    }

    public Station getDownStation() {
        return sections.getDownStation();
    }

    public long getDistance() {
        return sections.getDistance();
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

    public void setName(String name) {
        this.name = name;
    }

    public List<Section> getSections() {
        return new ArrayList<>(sections.getSectionList());
    }

    private void validateName(String name) {
        if (StringUtils.isBlank(name)) {
            throw new IllegalArgumentException("이름이 입력되지 않았습니다.");
        }
    }

    public void removeSectionByStation(Station station) {
        sections.removeSectionByStation(station);
    }
}
