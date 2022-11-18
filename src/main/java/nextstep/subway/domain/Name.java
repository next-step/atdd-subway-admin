package nextstep.subway.domain;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class Name {
    @Column(unique = true, nullable = false)
    private String name;

    protected Name() {
    }

    public Name(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
