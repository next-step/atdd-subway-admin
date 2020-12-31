package nextstep.subway.station.domain;

import nextstep.subway.common.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
public class Station extends BaseEntity {

    @Column(unique = true)
    private String name;

    public Station() {
    }

    public Station(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
