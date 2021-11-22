package nextstep.subway.line.domain;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import org.springframework.util.StringUtils;

@Embeddable
public class LineName {
    @Column(name = "name", unique = true)
    private String value;

    protected LineName() {
    }

    private LineName(String value) {
        validate(value);
        this.value = value;
    }

    public static LineName from(String value) {
        return new LineName(value);
    }

    private void validate(String value) {
        if (StringUtils.hasText(value)) {
            return;
        }
        throw new IllegalArgumentException("라인 이름은 빈 값일 수 없습니다.");
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        LineName lineName = (LineName)o;
        return Objects.equals(value, lineName.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
