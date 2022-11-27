package nextstep.subway.domain;

import nextstep.subway.constants.ErrorCode;

import javax.persistence.*;

@Entity
public class Station extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String name;

    public Station() {}

    public Station(String name) {
        validation(name);
        this.name = name;
    }

    private void validation(String name) {
        if (name == null || name.isEmpty()) {
            new IllegalArgumentException(ErrorCode.NO_EMPTY_STATION_NAME_EXCEPTION.getErrorMessage());
        }
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
