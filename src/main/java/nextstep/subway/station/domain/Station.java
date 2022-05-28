package nextstep.subway.station.domain;

import nextstep.subway.common.domain.BaseEntity;

import javax.persistence.*;

@Entity
public class Station extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Embedded
    private StationName name;

    protected Station() {}

    public Station(StationName name) {
        this.name = name;
    }

    public Station(String name) {
        this(StationName.from(name));
    }

    public Long getId() {
        return this.id;
    }

    public String getName() {
        return this.name.get();
    }
}
