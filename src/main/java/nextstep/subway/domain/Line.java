package nextstep.subway.domain;

import javax.persistence.*;
import java.util.*;

@Entity
public class Line extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String color;

    @Column(nullable = false)
    private int distance;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "up_station_id", foreignKey = @ForeignKey(name="fk_line_up_station_to_station"))
    private Station upStation;

    @JoinColumn(name = "down_station_id", foreignKey = @ForeignKey(name="fk_line_down_station_to_station"))
    @ManyToOne(fetch = FetchType.LAZY)
    private Station downStation;

    public Line (Builder builder){
        this.id = builder.id;
        this.name = builder.name;
        this.color = builder.color;
        this.distance = builder.distance;
        this.upStation = builder.upStation;
        this.downStation = builder.downStation;
    }

    public Line setUpStation(Station upStation) {
        this.upStation = upStation;
        return this;
    }

    public Line setDownStation(Station downStation) {
        this.downStation = downStation;
        return this;
    }

    public Line update(String name, String color){
        if(!name.isEmpty() && !this.name.equals(name)){
            this.name = name;
        }
        if(!color.isEmpty() && !this.name.equals(color)){
            this.color = color;
        }
        return this;
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

    public List<Station> getStations() {
        return new ArrayList(Arrays.asList(upStation, downStation));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Line line = (Line) o;
        return Objects.equals(id, line.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public static class Builder {

        private Long id;
        private String name;
        private String color;
        private int distance;
        private Station upStation;
        private Station downStation;

        public Builder setId(Long id) {
            this.id = id;
            return this;
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setColor(String color) {
            this.color = color;
            return this;
        }

        public Builder setDistance(int distance) {
            this.distance = distance;
            return this;
        }

        public Builder setUpStation(Station upStation) {
            this.upStation = upStation;
            return this;
        }

        public Builder setDownStation(Station downStation) {
            this.downStation = downStation;
            return this;
        }

        public Line build() {
            return new Line(this);
        }
    }
}
