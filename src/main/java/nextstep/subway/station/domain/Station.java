package nextstep.subway.station.domain;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.common.Name;

import javax.persistence.*;

@Entity
public class Station extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private Name name;

    public Station() {
    }

    public Station(String name) {
        this.name = new Name(name);
    }

    public Long getId() {
        return id;
    }

    public Name getName() {
        return name;
    }
}
