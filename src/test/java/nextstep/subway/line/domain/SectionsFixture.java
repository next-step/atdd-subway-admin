package nextstep.subway.line.domain;

import nextstep.subway.station.domain.Station;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class SectionsFixture {
    private SectionsFixture() {
    }

    public static List<Station> listOf(String... stationNames) {
        return Arrays.stream(stationNames)
                .map(Station::new)
                .collect(Collectors.toList());
    }
}
