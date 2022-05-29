package nextstep.subway.domain;

import nextstep.subway.dto.LineUpdateRequest;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
public class Line extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String name;
    @NotNull
    private String color;
    @OneToOne(fetch = FetchType.LAZY, mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    private Section section;

    protected Line() {
    }

    public Line(String name, String color) {
        this.name = name;
        this.color = color;
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

    public Section getSection() {
        return section;
    }

    public void modifyBy(LineUpdateRequest request) {
        this.name = request.getName();
        this.color = request.getColor();
    }

    public void addSection(Station upStation, Station downStation) {
        this.section = new Section(this, upStation, downStation);
    }
}
