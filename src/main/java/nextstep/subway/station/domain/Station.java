package nextstep.subway.station.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import nextstep.subway.common.BaseEntity;

import javax.persistence.*;

@Entity
@Getter @NoArgsConstructor
@Table(uniqueConstraints = @UniqueConstraint(name = "unique_station_name", columnNames={"name"}))
public class Station extends BaseEntity {

    private String name;

    public Station(final String name) {
        this.name = name;
    }
}
