package nextstep.subway.domain;

import javax.persistence.*;

@Entity
public class Station extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String name;

    private Long lineId;

    public Station() {
    }

    public Station(String name) {
        this.name = name;
    }

    public Station(String name, Long lineId) {
        this.name = name;
        this.lineId = lineId;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Long getLineId() {
        return lineId;
    }
}
