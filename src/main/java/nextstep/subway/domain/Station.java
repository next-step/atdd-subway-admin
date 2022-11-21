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

    public boolean equalsById(Station station) {
        if (id == null) {
            return false;
        }
        return id.equals(station.getId());
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
