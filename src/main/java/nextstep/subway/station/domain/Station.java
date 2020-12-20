package nextstep.subway.station.domain;

import nextstep.subway.common.BaseEntity;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
public class Station extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String name;

    public Station() {
    }

    Station(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Station(String name) {
        this(null, name);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    void updateCreatedDate(final LocalDateTime localDateTime) {
        super.setCreatedDate(localDateTime);
    }
}
