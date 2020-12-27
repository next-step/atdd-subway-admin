package nextstep.subway.line.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import nextstep.subway.common.BaseEntity;

import javax.persistence.*;
import org.apache.commons.lang3.StringUtils;

@Entity
public class Line extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String name;

    private String color;

    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LineStation> lineStations = new ArrayList<>();

    public Line() {
    }

    public Line(String name, String color) {
        validation(name, color);
        this.name = name;
        this.color = color;
    }

    public void add(LineStation lineStation) {
        this.lineStations.add(lineStation);
    }

    public void update(String name, String color) {
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

    public List<LineStation> getLineStations() {
        return Collections.unmodifiableList(lineStations);
    }

    private void validation(String name, String color) {
        if (StringUtils.isBlank(name) || StringUtils.isBlank(color)) {
            throw new IllegalArgumentException("Line의 name, color는 필수 값 입니다.");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Line line = (Line) o;
        return Objects.equals(id, line.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
