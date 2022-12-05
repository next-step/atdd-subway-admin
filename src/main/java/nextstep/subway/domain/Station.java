package nextstep.subway.domain;

import javax.persistence.*;

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
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o){
        if (this == o) {
            return true;
        }
        if (!(o instanceof Station)) {
            return false;
        }
        return ((Station)o).getId() == id && ((Station)o).getName() == name;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
