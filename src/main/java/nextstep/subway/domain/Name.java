package nextstep.subway.domain;

import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import nextstep.subway.constant.ErrorCode;
import nextstep.subway.utils.StringUtils;

@Embeddable
public class Name {

    @Column(nullable = false, unique = true)
    private String name;

    protected Name() {
    }

    private Name(String name) {
        validateNameNullOrEmpty(name);
        this.name = name;
    }

    public static Name from(String name) {
        return new Name(name);
    }

    private void validateNameNullOrEmpty(String name) {
        if(StringUtils.isNullOrEmpty(name)) {
            throw new IllegalArgumentException(ErrorCode.노선_또는_지하철명은_비어있을_수_없음.getErrorMessage());
        }
    }

    public String value() {
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
        Name name1 = (Name) o;
        return Objects.equals(name, name1.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
