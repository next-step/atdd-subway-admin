package nextstep.subway.dto;

public class Station {
    private Long id;
    private String name;

    public Station() {
    }

    public Station(Long id, String name) {

        this.id = id;
        this.name = name;
    }

    protected Long getId() {
        return this.id;
    }

    protected String getName() {
        return this.name;
    }
}
