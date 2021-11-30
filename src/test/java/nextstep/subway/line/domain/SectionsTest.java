package nextstep.subway.line.domain;

import nextstep.subway.exception.BadRequestException;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;

import java.util.Arrays;
import java.util.List;

@DisplayName("구간들 관련 기능")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@DataJpaTest
class SectionsTest {

    @Autowired
    StationRepository stationRepository;

    @Autowired
    LineRepository lineRepository;

    @Test
    void 구간들_생성() {
        // given
        Station 강남역 = stationRepository.save(Station.from("강남역"));
        Station 양재역 = stationRepository.save(Station.from("양재역"));
        Station 양재시민의숲 = stationRepository.save(Station.from("양재시민의숲"));
        Station 청계산입구 = stationRepository.save(Station.from("청계산입구"));

        Line 신분당선 = lineRepository.save(Line.of("신분당선", "red"));

        신분당선.addSection(강남역, 양재역, 10);
        신분당선.addSection(양재역, 양재시민의숲, 8);
        신분당선.addSection(양재시민의숲, 청계산입구, 8);

        // when
        Sections actual = 신분당선.getSections();

        // then
        Assertions.assertThat(actual).isNotNull();
    }

    @Test
    void 구간들_생성_시_종점역_정보가_없을_경우_생성_실패() {
        // given
        Station 강남역 = stationRepository.save(Station.from("강남역"));
        Station 양재역 = stationRepository.save(Station.from("양재역"));

        Line 신분당선 = lineRepository.save(Line.of("신분당선", "red"));

        신분당선.addSection(강남역, 양재역, 10);

        // when
        ThrowableAssert.ThrowingCallable throwingCallable = () -> 신분당선.addSection(양재역, null, 8);

        // then
        Assertions.assertThatExceptionOfType(BadRequestException.class)
                .isThrownBy(throwingCallable);
    }

    @Test
    void 구간들을_정렬하여_조회() {
        // given
        Station 강남역 = stationRepository.save(Station.from("강남역"));
        Station 양재역 = stationRepository.save(Station.from("양재역"));
        Station 양재시민의숲 = stationRepository.save(Station.from("양재시민의숲"));
        Station 청계산입구 = stationRepository.save(Station.from("청계산입구"));

        Line 신분당선 = lineRepository.save(Line.of("신분당선", "red"));

        신분당선.addSection(강남역, 양재역, 10);
        신분당선.addSection(양재역, 양재시민의숲, 8);
        신분당선.addSection(양재시민의숲, 청계산입구, 8);
        Sections sections = 신분당선.getSections();

        // when
        List<Station> actual = sections.getOrderedStations();

        //then
        Assertions.assertThat(actual).hasSize(4);
        Assertions.assertThat(actual).containsExactlyElementsOf(Arrays.asList(강남역, 양재역, 양재시민의숲, 청계산입구));
    }
}
