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

    public static Station of(nextstep.subway.domain.Station entity) {
        return new Station(entity.getId(),entity.getName());
    }

    public Long getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }
}
