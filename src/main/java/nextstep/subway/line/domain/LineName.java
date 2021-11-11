package nextstep.subway.line.domain;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class LineName {

    @Column(name = "name", unique = true, nullable = false)
    private String name;

    protected LineName() {}

    private LineName(String name) {
        this.name = name;
    }

    public static LineName from(String name) {
        return new LineName(name);
    }

    public String getValue() {
        return name;
    }
}
