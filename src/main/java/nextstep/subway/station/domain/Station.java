package nextstep.subway.station.domain;

import nextstep.subway.common.entity.BaseEntity;
import nextstep.subway.station.dto.StationResponse;

import javax.persistence.*;

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

    public StationResponse toDto() {
        return StationResponse.of(id, name, getCreatedDate(), getModifiedDate());
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
