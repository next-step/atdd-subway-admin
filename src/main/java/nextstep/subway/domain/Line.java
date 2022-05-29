package nextstep.subway.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import nextstep.subway.dto.LineRequest;

@Entity
public class Line extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String name;

    @Column(unique = true)
    private String color;

    private Long distance;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Section> sections = new ArrayList<>();

    protected Line() {

    }

    private Line(String name, String color, Long distance, Station upStation, Station downStation) {
        validateLine(name, color, distance);
        this.name = name;
        this.color = color;
        this.distance = distance;
        sections.add(Section.of(upStation, downStation, distance));
    }

    private void validateLine(String name, String color, Long distance) {
        if (Objects.isNull(name)) {
            throw new IllegalArgumentException("지하철 노선명이 없습니다.");
        }

        if (Objects.isNull(color)) {
            throw new IllegalArgumentException("지하철 노선 색상이 없습니다.");
        }

        if (Objects.isNull(distance) || distance < 1) {
            throw new IllegalArgumentException("노선사이의 거리가 없습니다.");
        }
    }

    public static Line of(LineRequest lineRequest, Station upStation, Station downStation) {
        return new Line(lineRequest.getName(), lineRequest.getColor(), lineRequest.getDistance(), upStation, downStation);
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

    public List<Section> getSections() {
        return sections;
    }
}
