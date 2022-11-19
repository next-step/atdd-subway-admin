package nextstep.subway.domain;

import nextstep.subway.domain.raw.Name;

import javax.persistence.*;

@Entity
public class Station extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private Name name;

    public Station() {
    }

    public Station(String name) {
        this.name = new Name(name);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name.getName();
    }
}
