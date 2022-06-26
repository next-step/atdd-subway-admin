package nextstep.subway.domain;

import nextstep.subway.dto.LineUpdateRequest;

import javax.persistence.*;

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
    private Sections sections;

    public Line() {
    }

    public Line(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public void initSections(Section section) {
        sections = new Sections(this, section);
    }

    public void addSection(Section section) {
        sections.add(section);
    }

    public Sections getSections() {
        return sections;
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

    public void update(LineUpdateRequest lineUpdateRequest) {
        if (lineUpdateRequest.getName() != null) {
            this.name = lineUpdateRequest.getName();
        }

        if (lineUpdateRequest.getColor() != null) {
            this.color = lineUpdateRequest.getColor();
        }
    }
}
