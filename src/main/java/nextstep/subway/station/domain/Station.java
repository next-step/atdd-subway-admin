package nextstep.subway.station.domain;

import lombok.EqualsAndHashCode;
import nextstep.subway.common.BaseEntity;

import javax.persistence.*;

@EqualsAndHashCode(of = "name")
@Entity
public class Station extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String name;

    public Station() {
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
}
