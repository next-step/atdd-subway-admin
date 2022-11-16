package nextstep.subway.common.vo;

import javax.persistence.Embeddable;
import java.util.Objects;

@Embeddable
public class Name {

    public static final String NULL_AND_EMPTY_EXCEPTION_MESSAGE = "이름은 null 이거나 empty일 수 없습니다.";
    private String name;

    protected Name() {
    }

    public Name(String name) {
        validate(name);
        this.name = name;
    }

    private void validate(String name) {
        if (Objects.isNull(name) || name.isEmpty()) {
            throw new IllegalArgumentException(NULL_AND_EMPTY_EXCEPTION_MESSAGE);
        }
    }
}
