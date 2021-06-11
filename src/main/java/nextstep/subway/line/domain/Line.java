package nextstep.subway.line.domain;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.line.section.Section;

import javax.persistence.*;

import java.util.List;

import static javax.persistence.FetchType.*;

@Entity
public class Line extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String name;
    private String color;

    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL)
    private List<Section> section;

    @OneToOne(fetch = LAZY)
    private Line downStation;

    @OneToOne(fetch = LAZY)
    private Line upStation;

    protected Line() { }

    public Line(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public void updateColor(String color) {
        this.color = color;
    }

    public Long id() {
        return id;
    }

    public String name() {
        return name;
    }

    public String color() {
        return color;
    }

    public Line setUpStation(Line upStation) {
        this.upStation = upStation;
        return this;
    }

    public Line setDownStation(Line downStation) {
        this.downStation = downStation;
        return this;
    }
}
