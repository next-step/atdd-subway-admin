package nextstep.subway.line.domain;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class LineName {
    @Column(unique = true)
    private String name;

    protected LineName() {
    }

    public LineName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
