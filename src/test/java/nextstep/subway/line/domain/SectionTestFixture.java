package nextstep.subway.line.domain;

import nextstep.subway.station.domain.Station;

public class SectionTestFixture {
    private SectionTestFixture() {
        throw new UnsupportedOperationException();
    }

    public static Station 강남역() {
        return Station.of(1L, "강남역");
    }

    public static Station 역삼역() {
        return Station.of(2L, "역삼역");
    }

    public static Station 교대역() {
        return Station.of(3L, "교대역");
    }

    public static Station 서초역() {
        return Station.of(4L, "서초역");
    }

    public static Section 강남역_역삼역_구간() {
        return Section.of(1L, 강남역(), 역삼역(), 10);
    }

    public static Section 역삼역_교대역_구간() {
        return Section.of(2L, 역삼역(), 교대역(), 10);
    }
}
