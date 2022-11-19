package nextstep.subway.domain.raw;

import nextstep.subway.dto.LineRequest;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import static nextstep.subway.constant.Message.NOT_VALID_EMPTY;

@Embeddable
public class Name {
    @Column(nullable = false)
    private String name;

    protected Name() {
    }

    public Name(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Name from(LineRequest updateRequest) {
        if (updateRequest.getName().isEmpty() || updateRequest.getName() == "") {
            throw new IllegalArgumentException(NOT_VALID_EMPTY);
        }
        return new Name(updateRequest.getName());
    }

}
