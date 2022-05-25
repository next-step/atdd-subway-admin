package nextstep.subway.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import nextstep.subway.dto.LineUpdateRequest;

@Entity
public class Line extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private String name;

    private String color;

    private int distance;

    @OneToOne(mappedBy = "upLine")
    private Station upStation;

    @OneToOne(mappedBy = "downLine")
    private Station downStation;

    protected Line() {
    }

    public Line(long id, String name, String color, int distance) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.distance = distance;
    }

    public Line(String name, String color, int distance) {
        this.name = name;
        this.color = color;
        this.distance = distance;
    }

    public void updateLine(LineUpdateRequest lineUpdateRequest) {
        this.name = lineUpdateRequest.getName();
        this.color = lineUpdateRequest.getColor();
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

    public int getDistance() {
        return distance;
    }

    public Station getUpStation() {
        return upStation;
    }

    public Station getDownStation() {
        return downStation;
    }

    public boolean hasUpStation() {
        return upStation != null;
    }

    public boolean hasDownStation() {
        return downStation != null;
    }

    public void resetLines() {
        if (upStation != null) {
            upStation.setUpLine(null);
        }

        if (downStation != null) {
            downStation.setDownLine(null);
        }
    }
}
