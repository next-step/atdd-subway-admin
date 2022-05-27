package nextstep.subway.line.domain;

import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class LineName {
    private static final String NO_LINE_NAME_ERROR = "지하철 노선 이름이 필요 합니다.";

    @Column
    private String name;

    protected LineName() {
    }

    private LineName(String name) {
        validateLineName(name);
        this.name = name;
    }

    private void validateLineName(String name) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException(NO_LINE_NAME_ERROR);
        }
    }

    public static LineName from(String name) {
        return new LineName(name);
    }

    public String getValue() {
        return this.name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        LineName lineName = (LineName) o;
        return Objects.equals(name, lineName.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
