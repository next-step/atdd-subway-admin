package nextstep.subway.domain;

import javax.persistence.*;

@Entity
public class Station extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Embedded
    @AttributeOverride(name = "name", column = @Column(name = "name", unique = true, nullable = false))
    private Name name;

//    @Embedded
//    private Sections sections;

    public Station() {
    }

    public Station(Name name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public Name getName() {
        return name;
    }
}
