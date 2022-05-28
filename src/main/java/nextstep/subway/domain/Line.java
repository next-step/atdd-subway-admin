package nextstep.subway.domain;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import nextstep.subway.domain.collection.Stations;
import org.apache.commons.lang3.StringUtils;

@Entity
public class Line extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String name;
    private String color;
    private boolean deleted = false;

    @Embedded
    private Stations stations = new Stations();

    protected Line() {
    }

    public Line(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public void update(String name, String color) {
        if (StringUtils.isNoneEmpty(name)) {
            this.name = name;
        }
        if (StringUtils.isNoneEmpty(color)) {
            this.color = color;
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

    public void delete() {
        this.deleted = true;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public Stations getStations() {
        return stations;
    }

    public void addStation(Station station) {
        stations.addStaion(station);
    }
}
