package nextstep.subway.common;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class Name {

    @Column(unique = true)
    private String name;

    protected Name() {
    }

    public Name(String name) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException();
        }

        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
