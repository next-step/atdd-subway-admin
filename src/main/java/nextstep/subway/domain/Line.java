package nextstep.subway.domain;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;

@Entity
public class Line extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String name;
    private String color;

    @OneToMany(cascade =  CascadeType.ALL)
    private List<Section> sections = new ArrayList<>();

    public Line(String name, String color, Station upStation, Station downStation, Long distance) {
        this.name = requireNonNull(name, "이름이 비었습니다");
        this.color = requireNonNull(color, "색상이 비었습니다");
        this.sections.add(new Section(upStation, downStation, distance));
    }

    protected Line() {
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

    public List<Station> getStations() {
        return sections.stream()
                       .flatMap(s -> Stream.of(s.getUpStation(), s.getDownStation()))
                       .collect(Collectors.toList());
    }
}
