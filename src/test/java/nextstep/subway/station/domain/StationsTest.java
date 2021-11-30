package nextstep.subway.station.domain;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import nextstep.subway.common.exception.SubwayException;
import nextstep.subway.section.domain.Distance;
import nextstep.subway.section.domain.Section;

class StationsTest {
    private Station 강남역;
    private Station 양재역;
    private Station 판교역;
    private Station 삼성역;

    @BeforeEach
    void setUp() {
        강남역 = new Station("강남역");
        양재역 = new Station("양재역");
        판교역 = new Station("판교역");
        삼성역 = new Station("삼성역");
    }

    @DisplayName("상행역과 하행역이 모두 등록되어 있다면 추가 실패")
    @Test
    void validateSection_errorWhenStationsContainsAll() {
        Stations stations = new Stations(Collections.singletonMap(강남역, 양재역));

        assertAll(
            () -> assertThatExceptionOfType(SubwayException.class)
                .isThrownBy(() -> stations.validateSection(new Section(강남역, 양재역, new Distance(10))))
                .withMessage("이미 모두 구간에 포함되어 있습니다."),
            () -> assertThatExceptionOfType(SubwayException.class)
                .isThrownBy(() -> stations.validateSection(new Section(양재역, 강남역, new Distance(10))))
                .withMessage("이미 모두 구간에 포함되어 있습니다.")
        );
    }

    @DisplayName("상행역과 하행역이 모두 등록되어 있다면 추가 실패")
    @Test
    void validateSection_errorWhenStationsNotContainsAll() {
        Stations stations = new Stations(Collections.singletonMap(강남역, 양재역));

        assertThatExceptionOfType(SubwayException.class)
            .isThrownBy(() -> stations.validateSection(new Section(판교역, 삼성역, new Distance(10))))
            .withMessage("모두 구간에 포함되어 있지 않습니다.");
    }
}