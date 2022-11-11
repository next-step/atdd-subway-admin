package nextstep.subway.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import org.springframework.util.StringUtils;

import nextstep.subway.dto.LineRequest;

@Entity
public class Line extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String name;
    private String color;
    @OneToMany(mappedBy = "line")
    private List<Station> stations = new ArrayList<>();

    public Line() {
    }

    public Line(String name) {
        this.name = name;
    }

    public static Line of(LineRequest lineRequest) {
        Line line = new Line();
        line.name = lineRequest.getName();
        line.color = lineRequest.getColor();
        return line;
    }

    public void addStation(Station station) {
        if (!stations.contains(station)) {
            stations.add(station);
        }
    }

    public void update(LineRequest lineRequest) {
        if (StringUtils.hasText(lineRequest.getName())) {
            this.name = lineRequest.getName();
        }
        if (StringUtils.hasText(lineRequest.getColor())) {
            this.color = lineRequest.getColor();
        }
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

    public Station getUpStation() {
        return stations.get(0);
    }

    public Station getDownStation() {
        return stations.get(1);
    }

}
