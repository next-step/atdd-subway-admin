package nextstep.subway.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;

@Entity
@JsonIgnoreProperties(value={"hibernateLazyInitializer"}, ignoreUnknown= true)
public class Line extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true,nullable = false)
    private String name;
    @Column(unique = true,nullable = false)
    private String color;

    @Column(nullable = false)
    private Long distance;

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinTable(name = "line_station"
        ,joinColumns = {@JoinColumn(name = "line_id",referencedColumnName = "id")}
        , inverseJoinColumns = {@JoinColumn(name = "station_id",referencedColumnName = "id")}
    )
    private List<Station> stations = new ArrayList<>();

    protected Line() {
    }

    public Line(String name, String color, Long distance, List<Station> stations) {
        this.name = name;
        this.color = color;
        this.distance = distance;
        this.stations.addAll(stations);
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

    public Long getDistance() {
        return distance;
    }

    public List<Station> getStations() {
        return stations;
    }

    public void updateLine(String name, String color) {
        this.name = name;
        this.color = color;
    }

    @Override
    public String toString() {
        return "Line{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", color='" + color + '\'' +
                ", distance=" + distance +
                ", stations=" + stations +
                '}';
    }
}
