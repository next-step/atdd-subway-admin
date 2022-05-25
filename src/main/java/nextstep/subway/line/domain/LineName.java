package nextstep.subway.line.domain;

import static nextstep.subway.line.domain.exception.LineExceptionMessage.LINE_NAME_IS_NOT_NULL;

import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import nextstep.subway.utils.StringUtils;

@Embeddable
public class LineName {

    @Column
    private String name;

    protected LineName() {}

    private LineName(String name) {
        this.name = name;
    }

    public static LineName from(String name) {
        validateLineName(name);
        return new LineName(name);
    }

    private static void validateLineName(String name) {
        if (StringUtils.isEmpty(name)) {
            throw new IllegalArgumentException(LINE_NAME_IS_NOT_NULL.getMessage());
        }
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
