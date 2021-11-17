package nextstep.subway.station.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import nextstep.subway.common.BaseEntity;

@Entity
public class Station extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String name;

    public Station() {
    }

    private Station(String name) {
        this.name = name;
    }

    public static Station of(String name) {
        return new Station(name);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
