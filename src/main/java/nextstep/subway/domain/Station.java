package nextstep.subway.domain;

import javax.persistence.*;

@Entity
public class Station extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String name;

    protected Station() {}

    public Station(String name) {
        this.name = name;
    }

    public Section toLineUpStation() {
        return new Section(this);
    }

    public Section toLineStation(Station preStation, int distance) {
        return new Section(preStation, this, distance);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

}
