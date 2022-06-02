package nextstep.subway.domain;

import javax.persistence.*;

@Entity
public class Line extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private String color;

    public Line() {
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

    public void update(Line line) {
        this.name = line.getName();
        this.color = line.getColor();
    }
}
