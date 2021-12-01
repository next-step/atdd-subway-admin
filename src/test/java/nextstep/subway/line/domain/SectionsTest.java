package nextstep.subway.line.domain;

import nextstep.subway.exception.BadRequestException;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
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

        Line 신분당선 = lineRepository.save(Line.of("신분당선", "red", 강남역, 양재역, 10));
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

        Line 신분당선 = lineRepository.save(Line.of("신분당선", "red", 강남역, 양재역, 10));

        // when
        ThrowableAssert.ThrowingCallable throwingCallable = () -> 신분당선.addSection(양재역, null, 8);

        // then
        Assertions.assertThatExceptionOfType(BadRequestException.class)
                .isThrownBy(throwingCallable);
    }

    @Test
    void 구간들을_정렬하여_조회한다() {
        // given
        Station 강남역 = stationRepository.save(Station.from("강남역"));
        Station 양재역 = stationRepository.save(Station.from("양재역"));
        Station 양재시민의숲 = stationRepository.save(Station.from("양재시민의숲"));
        Station 청계산입구 = stationRepository.save(Station.from("청계산입구"));

        Line 신분당선 = lineRepository.save(Line.of("신분당선", "red", 강남역, 양재역, 10));
        신분당선.addSection(양재역, 양재시민의숲, 8);
        신분당선.addSection(양재시민의숲, 청계산입구, 8);
        Sections sections = 신분당선.getSections();

        // when
        List<Station> actual = sections.getOrderedStations();

        //then
        Assertions.assertThat(actual).hasSize(4);
        Assertions.assertThat(actual).containsExactlyElementsOf(Arrays.asList(강남역, 양재역, 양재시민의숲, 청계산입구));
    }

    @Test
    @DisplayName("구간들 사이에 구간을 추가한다. 강남역 -(10m)- 양재시민의숲 => 강남역 -(8m)- 양재역 -(2m)- 양재시민의숲")
    void 구간들_사이에_새로운_구간을_추가한다() {
        // given
        Station 강남역 = stationRepository.save(Station.from("강남역"));
        Station 양재역 = stationRepository.save(Station.from("양재역"));
        Station 양재시민의숲 = stationRepository.save(Station.from("양재시민의숲"));

        Line 신분당선 = lineRepository.save(Line.of("신분당선", "red", 강남역, 양재시민의숲, 10));
        신분당선.addSection(강남역, 양재역, 8);
        Sections sections = 신분당선.getSections();

        // when
        List<Section> actual = sections.getOrderedSections();

        //then
        Assertions.assertThat(actual).hasSize(2);
        Assertions.assertThat(actual).contains(Section.of(신분당선, 강남역, 양재역, 8));
        Assertions.assertThat(actual).contains(Section.of(신분당선, 양재역, 양재시민의숲, 2));
    }

    @ParameterizedTest
    @ValueSource(ints = {10, 11})
    void 기존_구간의_사이_길이_보다_크거나_같으면_등록할_수_없다(int distance) {
        // given
        Station 강남역 = stationRepository.save(Station.from("강남역"));
        Station 양재역 = stationRepository.save(Station.from("양재역"));
        Station 양재시민의숲 = stationRepository.save(Station.from("양재시민의숲"));

        Line 신분당선 = lineRepository.save(Line.of("신분당선", "red", 강남역, 양재시민의숲, 10));

        // when
        ThrowableAssert.ThrowingCallable throwingCallable = () -> 신분당선.addSection(강남역, 양재역, distance);

        // then
        Assertions.assertThatExceptionOfType(BadRequestException.class)
                .isThrownBy(throwingCallable);
    }

    @Test
    void 상행역과_하행역이_이미_노선에_등록되어있으면_추가할_수_없다() {
        // given
        Station 강남역 = stationRepository.save(Station.from("강남역"));
        Station 양재역 = stationRepository.save(Station.from("양재역"));

        Line 신분당선 = lineRepository.save(Line.of("신분당선", "red", 강남역, 양재역, 10));

        // when
        ThrowableAssert.ThrowingCallable throwingCallable = () -> 신분당선.addSection(강남역, 양재역, 10);

        // then
        Assertions.assertThatExceptionOfType(BadRequestException.class)
                .isThrownBy(throwingCallable);
    }

    @Test
    void 상행역과_하행역_둘다_노선에_등록되어있지_않으면_추가할_수_없다() {
        // given
        Station 강남역 = stationRepository.save(Station.from("강남역"));
        Station 양재역 = stationRepository.save(Station.from("양재역"));
        Station 양재시민의숲 = stationRepository.save(Station.from("양재시민의숲"));
        Station 청계산입구 = stationRepository.save(Station.from("청계산입구"));

        Line 신분당선 = lineRepository.save(Line.of("신분당선", "red", 강남역, 양재역, 10));

        // when
        ThrowableAssert.ThrowingCallable throwingCallable = () -> 신분당선.addSection(양재시민의숲, 청계산입구, 10);

        // then
        Assertions.assertThatExceptionOfType(BadRequestException.class)
                .isThrownBy(throwingCallable);
    }

    @Test
    @DisplayName("새로운 구간을 상행 종점으로 추가한다. 양재역 -(10m)- 양재시민의숲 => 강남역 -(10m)- 양재역 -(10m)- 양재시민의숲")
    void 새로운_구간을_상행_종점으로_추가한다() {
        // given
        Station 강남역 = stationRepository.save(Station.from("강남역"));
        Station 양재역 = stationRepository.save(Station.from("양재역"));
        Station 양재시민의숲 = stationRepository.save(Station.from("양재시민의숲"));

        Line 신분당선 = lineRepository.save(Line.of("신분당선", "red", 양재역, 양재시민의숲, 10));
        신분당선.addSection(강남역, 양재역, 8);
        Sections sections = 신분당선.getSections();

        // when
        List<Section> actual = sections.getOrderedSections();

        //then
        Assertions.assertThat(actual).hasSize(2);
        Assertions.assertThat(actual).contains(Section.of(신분당선, 강남역, 양재역, 10));
        Assertions.assertThat(actual).contains(Section.of(신분당선, 양재역, 양재시민의숲, 10));
    }
}
