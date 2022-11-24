package nextstep.subway.domain.line;

import nextstep.subway.message.LineMessage;
import org.springframework.util.StringUtils;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.util.Objects;

@Embeddable
public class LineName {

    @Column(length = 6, nullable = false, unique = true)
    private final String name;

    protected LineName() {
        this.name = null;
    }

    public LineName(String name) {
        validateLineName(name);
        this.name = name;
    }

    private void validateLineName(String lineName) {
        if(!StringUtils.hasText(lineName)) {
            throw new IllegalArgumentException(LineMessage.ERROR_LINE_NAME_SHOULD_BE_NOT_NULL.message());
        }
    }

    public String value() {
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
        return name != null ? name.hashCode() : 0;
    }
}
