package nextstep.subway.line.domain;

import nextstep.subway.common.util.SubwayValidator;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.util.Objects;

@Embeddable
public class LineName {
    @Column(unique = true, nullable = false)
    private String name;

    protected LineName() {
    }

    private LineName(String name) {
        this.name = name;
    }

    public static LineName from(String name) {
        SubwayValidator.validateNotNullAndNotEmpty(name);
        return new LineName(name);
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LineName lineName = (LineName) o;
        return name.equals(lineName.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
