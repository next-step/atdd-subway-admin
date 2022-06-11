package nextstep.subway.domain;

import javax.persistence.*;

@Entity
public class Station extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Embedded()
    private StationName name;

    public Station() {
    }

    public Station(String name) {
        this.name = new StationName(name);
    }

    public Long getId() {
        return id;
    }

    public StationName getName() {
        return name;
    }
}
