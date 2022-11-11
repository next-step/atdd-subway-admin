package nextstep.subway.domain;

import javax.persistence.*;
import nextstep.subway.constant.ErrorCode;

@Entity
public class Station extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true, nullable = false)
    private String name;

    protected Station() {
    }

    public Station(String name) {
        validateStationName(name);

        this.name = name;
    }

    private void validateStationName(String name) {
        if(name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException(ErrorCode.지하철역명은_비어있을_수_없음.getErrorMessage());
        }
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
