package nextstep.subway.domain;

import nextstep.subway.exception.StationException;

import javax.persistence.*;

import static nextstep.subway.exception.StationExceptionMessage.EMPTY_STATION_NAME;

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
        validateStation(name);
        this.name = name;
    }

    private void validateStation(String name) {
        if (name == null || name.isEmpty()) {
            throw new StationException(EMPTY_STATION_NAME.getMessage());
        }
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
