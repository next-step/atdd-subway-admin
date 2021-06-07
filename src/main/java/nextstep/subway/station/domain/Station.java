package nextstep.subway.station.domain;

import nextstep.subway.common.BaseEntity;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Station extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    public Station() {
    }

    private Station(Long id,
                    String name,
                    LocalDateTime createdDate,
                    LocalDateTime modifiedDate) {
        this.id = id;
        this.name = name;
        this.createdDate = createdDate;
        this.modifiedDate = modifiedDate;
    }

    private Station(String name) {
        this.name = name;
    }

    public static Station of(Long id,
                             String name,
                             LocalDateTime createdDate,
                             LocalDateTime modifiedDate) {
        return new Station(id, name, createdDate, modifiedDate);
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
