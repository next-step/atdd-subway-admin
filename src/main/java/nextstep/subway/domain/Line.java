package nextstep.subway.domain;

import javax.persistence.*;

@Entity
public class Line {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String color;
    @ManyToOne
    @JoinColumn(name = "upStation_id")
    private Station upStation;
    @ManyToOne
    @JoinColumn(name = "downStation_id")
    private Station downStation;
    private int distance;

    protected Line(){
    }

    public Line(String name, String color, Station upStation, Station downStation, int distance) {
        this.name = name;
        this.color = color;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public void update(Line newLine) {
        updateName(newLine.getName());
        updateColor(newLine.getColor());
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
        return upStation;
    }

    public Station getDownStation() {
        return downStation;
    }

    public int getDistance() {
        return distance;
    }

    private void updateName(String name) {
        this.name = name;
    }

    private void updateColor(String color) {
        this.color = color;
    }
}
