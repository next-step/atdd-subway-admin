package nextstep.subway.station.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import nextstep.subway.common.BaseEntity;

import javax.persistence.*;

@Entity
@ToString(callSuper = true)
@Getter @NoArgsConstructor
@Table(uniqueConstraints = @UniqueConstraint(name = "unique_station_name", columnNames={"name"}))
public class Station extends BaseEntity {

    private String name;

    public Station(final String name) {
        this(null, name);
    }

    public Station(final Long id, final String name) {
        this.id = id;
        this.name = name;
    }
}
