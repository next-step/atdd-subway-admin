package nextstep.subway.domain;

import javax.persistence.Embeddable;

@Embeddable
public class Name {

    private String name;

    protected Name() {
    }

    public Name(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
