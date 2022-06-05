package nextstep.subway.dto.line;

import nextstep.subway.domain.Station;

class FinalStation {
    private final Long id;
    private final String name;

    private FinalStation(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public static FinalStation from(Station station) {
        return new FinalStation(station.getId(), station.getName());
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
