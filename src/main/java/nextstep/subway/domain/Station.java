package nextstep.subway.domain;

import javax.persistence.*;
import org.apache.commons.lang3.StringUtils;

@Entity
public class Station extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String name;

    protected Station() {
    }

    private Station(String name) {
        validName(name);
        this.name = name;
    }

    public static Station from(String name) {
        return new Station(name);
    }

    private void validName(String name) {
        if (StringUtils.isBlank(name)) {
            throw new IllegalArgumentException("공백은 허용하지 않습니디.");
        }
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
