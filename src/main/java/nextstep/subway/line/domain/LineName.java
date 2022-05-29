package nextstep.subway.line.domain;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.util.Objects;

@Embeddable
public class LineName {
    private static final String LINE_NAME_NOT_NULL = "지하철 노선 이름은 빈값일 수 없습니다.";

    @Column(unique = true, nullable = false)
    private String name;

    protected LineName() {}

    private LineName(String name) {
        this.name = name;
    }

    public static LineName from(String name) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException(LINE_NAME_NOT_NULL);
        }
        return new LineName(name);
    }

    public String get() {
        return this.name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LineName lineName = (LineName) o;
        return Objects.equals(name, lineName.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
