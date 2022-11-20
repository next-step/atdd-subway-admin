package nextstep.subway.domain;

import java.util.Objects;
import javax.persistence.*;

@Entity
public class Station extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String name;

    public Station() {}

    public Station(Long id) {
        this.id = id;
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
    public boolean equals(Object obj) {
        if (!(obj instanceof Station)) {
            return false;
        }
        return id != null
                && id.equals(((Station) obj).getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }
}
