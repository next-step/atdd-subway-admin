package nextstep.subway.station.domain;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class StationName {

    @Column(name = "name", unique = true, nullable = false)
    private String name;

    protected StationName() {}

    private StationName(String name) {
        this.name = name;
    }

    public static StationName from(String name) {
        return new StationName(name);
    }

    public String getValue() {
        return name;
    }
}
