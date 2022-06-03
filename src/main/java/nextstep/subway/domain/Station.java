package nextstep.subway.domain;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Station extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String name;

    @OneToMany(mappedBy = "upStation", fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    private List<LineStation> upLineStations = new ArrayList<>();

    @OneToMany(mappedBy = "downStation", fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    private List<LineStation> downLineStations = new ArrayList<>();

    protected Station() {
    }

    public Station(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<LineStation> getUpLineStations() {
        return upLineStations;
    }

    public List<LineStation> getDownLineStations() {
        return downLineStations;
    }
}
