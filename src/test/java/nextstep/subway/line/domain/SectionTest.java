package nextstep.subway.line.domain;

import nextstep.subway.exception.BadRequestException;
import nextstep.subway.station.domain.Station;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("구간 관련 기능")
class SectionTest {

    @Test
    void 구간_생성() {
        Station upStation = Station.from("강남역");
        Station downStation = Station.from("양재역");
        Section section = Section.of(upStation, downStation, 10);

        Assertions.assertThat(section).isNotNull();
    }

    @Test
    void 상행역과_하행역이_같은_경우_구간_생성_불가() {
        Station upStation = Station.from("강남역");
        Station downStation = Station.from("강남역");

        ThrowableAssert.ThrowingCallable throwingCallable = () -> Section.of(upStation, downStation, 10);

        Assertions.assertThatExceptionOfType(BadRequestException.class)
                .isThrownBy(throwingCallable);
    }
}
