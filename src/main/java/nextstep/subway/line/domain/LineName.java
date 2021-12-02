package nextstep.subway.line.domain;

import nextstep.subway.common.exception.InvalidEntityRequiredException;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.util.Objects;

@Embeddable
public class LineName {
    @Column(unique = true)
    private String name;

    protected LineName() {
    }

    public LineName(String name) {
        validateEmptyName(name);
        this.name = name;
    }

    private void validateEmptyName(String name) {
        if (name.isEmpty()) {
            throw new InvalidEntityRequiredException(name);
        }
    }

    public String getName() {
        return name;
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
