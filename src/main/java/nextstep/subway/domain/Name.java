package nextstep.subway.domain;

import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class Name {

    private static final String ROUTE_NAME_ERROR = "노선명을 지정해주세요.";

    @Column(unique = true)
    private String name;

    protected Name() {
    }

    private Name(String name) {
        this.name = name;
    }

    public static Name of(String name) {
        if (Objects.isNull(name) || name.trim().isEmpty()) {
            throw new IllegalArgumentException(ROUTE_NAME_ERROR);
        }

        return new Name(name);
    }

    public String value() {
        return name;
    }
}
