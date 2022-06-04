package nextstep.subway.domain;

import org.junit.jupiter.api.BeforeEach;

public abstract class SectionTestFixture {
    Station upStation;
    Station downStation;
    Section section;

    @BeforeEach
    void setUp() {
        upStation = Station.builder("양재")
                .id(1L)
                .build();
        downStation = Station.builder("판교")
                .id(2L)
                .build();
        section = Section.builder(upStation, downStation, Distance.valueOf(10))
                .build();
    }
}
