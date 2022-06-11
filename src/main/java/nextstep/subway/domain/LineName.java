package nextstep.subway.domain;

import org.apache.commons.lang3.StringUtils;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class LineName {
    private static final String ERROR_MESSAGE_EMPTY_NAME = "지하철노선 이름은 비어있을 수 없습니다.";

    @Column(unique = true)
    private String name;

    public LineName() {
    }

    public LineName(String name) {
        validate(name);
        this.name = name;
    }

    private void validate(String name) {
        if (StringUtils.isEmpty(name)) {
            throw new IllegalArgumentException(ERROR_MESSAGE_EMPTY_NAME);
        }
    }

    public String getName() {
        return name;
    }
}
