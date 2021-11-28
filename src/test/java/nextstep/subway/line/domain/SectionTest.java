package nextstep.subway.line.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import nextstep.subway.common.exception.IllegalStationException;
import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SectionTest {

    private Section 강남_판교_구간1;
    private Section 강남_판교_구간2;
    private Section 강남_정자_구간;
    private Section 판교_정자_구간;
    private Section 정자_광교_구간;

    @BeforeEach
    void setUp() {
        Station 강남역 = new Station("강남역");
        Station 판교역 = new Station("판교역");
        Station 정자역 = new Station("정자역");
        Station 광교역 = new Station("광교역");

        강남_판교_구간1 = Section.of(강남역, 판교역, 5);
        강남_판교_구간2 = Section.of(강남역, 판교역, 7);
        강남_정자_구간 = Section.of(강남역, 정자역, 10);
        판교_정자_구간 = Section.of(판교역, 정자역, 10);
        정자_광교_구간 = Section.of(정자역, 광교역, 10);
    }

    @Test
    void test_지하철_역이_null_이면_예외_발생() {
        assertAll(
            () -> assertThatThrownBy(() -> Section.of(null, new Station("서울역"), 10))
                .isInstanceOf(IllegalStationException.class),
            () -> assertThatThrownBy(() -> Section.of(new Station("서울역"), null, 10))
                .isInstanceOf(IllegalStationException.class)
        );
    }

    @Test
    void test_거리와_상관없이_두_종점이_일치하면_동일한_section() {
        assertAll(
            () -> assertThat(강남_판교_구간1.isSame(강남_판교_구간2)).isTrue(),
            () -> assertThat(강남_판교_구간1.isSame(강남_정자_구간)).isFalse()
        );
    }

    @Test
    void test_상행_종점_일치() {
        assertAll(
            () -> assertThat(강남_판교_구간1.isSameUpStation(강남_정자_구간)).isTrue(),
            () -> assertThat(강남_판교_구간1.isSameUpStation(판교_정자_구간)).isFalse()
        );
    }

    @Test
    void test_하행_종점_일치() {
        assertAll(
            () -> assertThat(강남_정자_구간.isSameDownStation(판교_정자_구간)).isTrue(),
            () -> assertThat(강남_정자_구간.isSameDownStation(강남_판교_구간1)).isFalse()
        );
    }

    @Test
    void test_상행_하행_종점_사이에_포함되는_구간_확인() {
        assertAll(
            () -> assertThat(강남_정자_구간.isIncludeAbleSection(강남_판교_구간1)).isTrue(),
            () -> assertThat(강남_정자_구간.isIncludeAbleSection(판교_정자_구간)).isTrue(),
            () -> assertThat(강남_판교_구간1.isIncludeAbleSection(판교_정자_구간)).isFalse()
        );
    }

    @Test
    void test_상행_하행_종점_중_하나라도_일치하면_연결_가능() {
        assertAll(
            () -> assertThat(강남_정자_구간.isLinkable(강남_판교_구간1)).isTrue(),
            () -> assertThat(판교_정자_구간.isLinkable(강남_판교_구간1)).isTrue(),
            () -> assertThat(강남_정자_구간.isLinkable(정자_광교_구간)).isTrue(),
            () -> assertThat(강남_판교_구간1.isLinkable(정자_광교_구간)).isFalse()
        );
    }
}