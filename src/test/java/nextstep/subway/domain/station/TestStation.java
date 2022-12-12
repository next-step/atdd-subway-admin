package nextstep.subway.domain.station;

public class TestStation extends Station {
    private Long id;

    public TestStation(Long id, String name) {
        super(name);

        this.id = id;
    }

    @Override
    public Long getId() {
        return this.id;
    }
}