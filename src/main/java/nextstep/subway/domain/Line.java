package nextstep.subway.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Entity
public class Line extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String color;
    @ManyToOne
    private Station upStation;
    @ManyToOne
    private Station downStation;
    @Column(nullable = false)
    private int distance;

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

    public Station getUpStation() {
        return upStation;
    }

    public Station getDownStation() {
        return downStation;
    }

    public List<Station> getStations() {
        return new ArrayList(Arrays.asList(upStation, downStation));
    }

    public int getDistance() {
        return distance;
    }

    public void updateNameAndColor(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public static class Builder {
        private String name;
        private String color;
        private Station upStation;
        private Station downStation;
        private int distance;

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder color(String color) {
            this.color = color;
            return this;
        }

        public Builder upStation(Station upStation) {
            this.upStation = upStation;
            return this;
        }

        public Builder downStation(Station downStation) {
            this.downStation = downStation;
            return this;
        }

        public Builder distance(int distance) {
            this.distance = distance;
            return this;
        }

        public Line build() {
            return new Line(this);
        }
    }

    private Line(Builder builder) {
        this.name = builder.name;
        this.color = builder.color;
        this.upStation = builder.upStation;
        this.downStation = builder.downStation;
        this.distance = builder.distance;
    }
}
