package nextstep.subway.domain.line;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.util.Objects;

@Embeddable
public class LineName {
    @Column(name = "name", unique = true)
    private String value;

    protected LineName() {
    }

    private LineName(String name) {
        this.value = name;
    }

    public static LineName of(String name) {
        if (Objects.isNull(name) || name.trim().isEmpty()) {
            throw new IllegalArgumentException("노선명을 지정해주세요.");
        }

        return new LineName(name);
    }

    public String getValue() {
        return value;
    }
}
