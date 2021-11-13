package nextstep.subway.line.domain;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import nextstep.subway.utils.StringUtils;

@Embeddable
public class LineName {
    private static final String EMPTY_LINE_NAME_ERROR_MESSAGE = "노선이름이 비어있습니다. name=%s";

    @Column(name = "name", unique = true, nullable = false)
    private String name;

    protected LineName() {}

    private LineName(String name) {
        this.name = name;
    }

    public static LineName from(String name) {
        validateLineName(name);
        return new LineName(name);
    }

    public String getValue() {
        return name;
    }

    private static void validateLineName(String name) {
        if (StringUtils.isEmpty(name)) {
            throw new IllegalArgumentException(String.format(EMPTY_LINE_NAME_ERROR_MESSAGE, name));
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        LineName lineName = (LineName)o;
        return Objects.equals(name, lineName.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
