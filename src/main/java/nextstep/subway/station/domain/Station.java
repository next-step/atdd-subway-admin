package nextstep.subway.station.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import nextstep.subway.common.BaseEntity;

import javax.persistence.*;

@Entity
@Getter @NoArgsConstructor
public class Station extends BaseEntity {
    @Id  @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String name;

    public Station(String name) {
        this.name = name;
    }
}
