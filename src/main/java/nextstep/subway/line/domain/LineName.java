package nextstep.subway.line.domain;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import org.springframework.util.StringUtils;

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
        if (!StringUtils.hasLength(name)) {
            throw new IllegalArgumentException(String.format(EMPTY_LINE_NAME_ERROR_MESSAGE, name));
        }
    }
}
