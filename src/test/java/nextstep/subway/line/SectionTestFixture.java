package nextstep.subway.line;

import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.Station;

public class SectionTestFixture {
    private SectionTestFixture() {
        throw new UnsupportedOperationException();
    }

    public static Line 이호선() {
        return Line.of(1L, "이호선", "bg-green-600");
    }

    public static Station 강남역() {
        return Station.of(1L, "강남역");
    }

    public static Station 역삼역() {
        return Station.of(2L, "역삼역");
    }
}
