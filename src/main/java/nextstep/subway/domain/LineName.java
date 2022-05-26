package nextstep.subway.domain;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class LineName {
    @Column(name = "name")
    private String name;

    protected LineName() {

    }

    public LineName(String name) {
        this.name = name;
    }

    public void modify(String name) {
        if (name != null) {
            this.name = name;
        }
    }

    public String getName() {
        return name;
    }
}
