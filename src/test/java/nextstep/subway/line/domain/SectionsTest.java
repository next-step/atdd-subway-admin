package nextstep.subway.line.domain;

import nextstep.subway.exception.CannotAddSectionException;
import nextstep.subway.exception.CannotDeleteSectionException;
import nextstep.subway.station.domain.Station;
import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Arrays;
import java.util.List;

import static nextstep.subway.line.domain.SectionTestFixture.강남역;
import static nextstep.subway.line.domain.SectionTestFixture.교대역;
import static nextstep.subway.line.domain.SectionTestFixture.서초역;
import static nextstep.subway.line.domain.SectionTestFixture.역삼역;
import static nextstep.subway.line.domain.SectionTestFixture.강남역_역삼역_구간;
import static nextstep.subway.line.domain.SectionTestFixture.역삼역_교대역_구간;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("구간 모음 테스트")
class SectionsTest {

    @DisplayName("생성 확인")
    @Test
    void 생성_확인() {
        // given
        Section 강남역_역삼역_구간 = 강남역_역삼역_구간();
        Section 역삼역_교대역_구간 = 역삼역_교대역_구간();

        // when
        Sections sections = Sections.of(Arrays.asList(강남역_역삼역_구간, 역삼역_교대역_구간));

        // then
        assertThat(sections)
                .isNotNull();
    }

    @DisplayName("역 목록 조회 확인")
    @Test
    void 역_목록_조회_확인() {
        // given
        Section 강남역_역삼역_구간 = 강남역_역삼역_구간();
        Section 역삼역_교대역_구간 = 역삼역_교대역_구간();
        Sections sections = Sections.of(Arrays.asList(강남역_역삼역_구간, 역삼역_교대역_구간));

        // when
        List<Station> stations = sections.getStations();

        // then
        assertAll(
                () -> assertThat(stations.get(0))
                        .isEqualTo(강남역()),
                () -> assertThat(stations.get(1))
                        .isEqualTo(역삼역()),
                () -> assertThat(stations.get(2))
                        .isEqualTo(교대역())
        );
    }

    @DisplayName("구간 등록 테스트")
    @Nested
    class 구간_등록_테스트 {
        @DisplayName("성공 테스트")
        @Nested
        class 성공_테스트 {

            @DisplayName("역_사이에_새로운_역을_등록")
            @Test
            void 역_사이에_새로운_역을_등록() {
                // given
                Sections 이호선 = new Sections();
                Section 강남역_교대역_구간 = Section.of(강남역(), 교대역(), 10);
                Section 강남역_역삼역_구간 = Section.of(강남역(), 역삼역(), 5);

                // when
                이호선.addSection(강남역_교대역_구간);
                이호선.addSection(강남역_역삼역_구간);

                // then
                assertAll(
                        () -> 등록된_구간_순서_확인(이호선, 강남역(), 역삼역(), 교대역())
                );
            }

            @DisplayName("새로운 역을 상행 종점으로 등록")
            @Test
            void 새로운_역을_상행_종점으로_등록() {
                // given
                Sections 이호선 = new Sections();
                Section 역삼역_교대역_구간 = Section.of(역삼역(), 교대역(), 5);
                Section 강남역_역삼역_구간 = Section.of(강남역(), 역삼역(), 5);

                // when
                이호선.addSection(역삼역_교대역_구간);
                이호선.addSection(강남역_역삼역_구간);

                // then
                assertAll(
                        () -> 등록된_구간_순서_확인(이호선, 강남역(), 역삼역(), 교대역())
                );
            }

            @DisplayName("새로운 역을 하행 종점으로 등록")
            @Test
            void 새로운_역을_하행_종점으로_등록() {
                // given
                Sections 이호선 = new Sections();
                Section 강남역_역삼역_구간 = Section.of(강남역(), 역삼역(), 5);
                Section 역삼역_교대역_구간 = Section.of(역삼역(), 교대역(), 5);

                // when
                이호선.addSection(강남역_역삼역_구간);
                이호선.addSection(역삼역_교대역_구간);

                // then
                assertAll(
                        () -> 등록된_구간_순서_확인(이호선, 강남역(), 역삼역(), 교대역())
                );
            }
        }

        @DisplayName("실패 테스트")
        @Nested
        class 실패_테스트 {

            @DisplayName("기존 구간의 길이보다 크거나 같음_상행역_기준")
            @ParameterizedTest(name = "{displayName} ({index}) -> param = [{arguments}]")
            @CsvSource(value = {"10:10", "10:11"}, delimiter = ':')
            void 기존_구간의_길이보다_크거나_같음_상행역_기준(int 기존_구간_길이, int 신규_구간_길이) {
                // given
                Sections 이호선 = new Sections();
                Section 강남역_교대역_구간 = Section.of(강남역(), 교대역(), 기존_구간_길이);
                Section 강남역_역삼역_구간 = Section.of(강남역(), 역삼역(), 신규_구간_길이);

                이호선.addSection(강남역_교대역_구간);

                // when
                ThrowableAssert.ThrowingCallable throwingCallable = () -> 이호선.addSection(강남역_역삼역_구간);

                // then
                assertThatThrownBy(throwingCallable)
                        .isInstanceOf(CannotAddSectionException.class);
            }

            @DisplayName("기존 구간의 길이보다 크거나 같음_하행역_기준")
            @ParameterizedTest(name = "{displayName} ({index}) -> param = [{arguments}]")
            @CsvSource(value = {"10:10", "10:11"}, delimiter = ':')
            void 기존_구간의_길이보다_크거나_같음_하행역_기준(int 기존_구간_길이, int 신규_구간_길이) {
                // given
                Sections 이호선 = new Sections();
                Section 강남역_교대역_구간 = Section.of(강남역(), 교대역(), 기존_구간_길이);
                Section 역삼역_교대역_구간 = Section.of(역삼역(), 교대역(), 신규_구간_길이);

                이호선.addSection(강남역_교대역_구간);

                // when
                ThrowableAssert.ThrowingCallable throwingCallable = () -> 이호선.addSection(역삼역_교대역_구간);

                // then
                assertThatThrownBy(throwingCallable)
                        .isInstanceOf(CannotAddSectionException.class);
            }

            @DisplayName("상행역과 하행역이 모두 등록되어 있음")
            @Test
            void 상행역과_하행역이_모두_등록되어_있음() {
                // given
                Sections 이호선 = new Sections();
                Section 강남역_역삼역_구간 = Section.of(강남역(), 역삼역(), 10);

                이호선.addSection(강남역_역삼역_구간);

                // when
                ThrowableAssert.ThrowingCallable throwingCallable = () -> 이호선.addSection(강남역_역삼역_구간);

                // then
                assertThatThrownBy(throwingCallable)
                        .isInstanceOf(CannotAddSectionException.class);
            }

            @DisplayName("상행역과 하행역 둘 중 하나도 포함되어 있지 않음")
            @Test
            void 상행역과_하행역_둘_중_하나도_포함되어_있지_않음() {
                // given
                Sections 이호선 = new Sections();
                Section 강남역_역삼역_구간 = Section.of(강남역(), 역삼역(), 10);
                Section 교대역_서초역_구간 = Section.of(교대역(), 서초역(), 10);

                이호선.addSection(강남역_역삼역_구간);

                // when
                ThrowableAssert.ThrowingCallable throwingCallable = () -> 이호선.addSection(교대역_서초역_구간);

                // then
                assertThatThrownBy(throwingCallable)
                        .isInstanceOf(CannotAddSectionException.class);
            }
        }
    }

    @DisplayName("구간 삭제 테스트")
    @Nested
    class 구간_삭제_테스트 {
        @DisplayName("성공 테스트")
        @Nested
        class 성공_테스트 {

            @DisplayName("상행 종점 제거")
            @Test
            void 상행_종점_제거() {
                // given
                Sections 이호선 = new Sections();
                Section 강남역_역삼역_구간 = Section.of(강남역(), 역삼역(), 5);
                Section 역삼역_교대역_구간 = Section.of(역삼역(), 교대역(), 5);

                이호선.addSection(강남역_역삼역_구간);
                이호선.addSection(역삼역_교대역_구간);

                // when
                이호선.deleteSectionBy(강남역());

                // then
                assertAll(
                        () -> 등록된_구간_순서_확인(이호선, 역삼역(), 교대역())
                );
            }

            @DisplayName("하행 종점 제거")
            @Test
            void 하행_종점_제거() {
                // given
                Sections 이호선 = new Sections();
                Section 강남역_역삼역_구간 = Section.of(강남역(), 역삼역(), 5);
                Section 역삼역_교대역_구간 = Section.of(역삼역(), 교대역(), 5);

                이호선.addSection(강남역_역삼역_구간);
                이호선.addSection(역삼역_교대역_구간);

                // when
                이호선.deleteSectionBy(교대역());

                // then
                assertAll(
                        () -> 등록된_구간_순서_확인(이호선, 강남역(), 역삼역())
                );
            }

            @DisplayName("중간역 제거")
            @Test
            void 중간역_제거() {
                // given
                Sections 이호선 = new Sections();
                Section 강남역_역삼역_구간 = Section.of(강남역(), 역삼역(), 5);
                Section 역삼역_교대역_구간 = Section.of(역삼역(), 교대역(), 5);

                이호선.addSection(강남역_역삼역_구간);
                이호선.addSection(역삼역_교대역_구간);

                // when
                이호선.deleteSectionBy(역삼역());

                // then
                assertAll(
                        () -> 등록된_구간_순서_확인(이호선, 강남역(), 교대역())
                );
            }
        }

        @DisplayName("실패 테스트")
        @Nested
        class 실패_테스트 {

            @DisplayName("노선에 존재하지 않는 역 제거")
            @Test
            void 노선에_존재하지_않는_역_제거() {
                // given
                Sections 이호선 = new Sections();
                Section 강남역_역삼역_구간 = Section.of(강남역(), 역삼역(), 5);
                Section 역삼역_교대역_구간 = Section.of(역삼역(), 교대역(), 5);

                이호선.addSection(강남역_역삼역_구간);
                이호선.addSection(역삼역_교대역_구간);

                // when
                ThrowableAssert.ThrowingCallable throwingCallable = () -> 이호선.deleteSectionBy(서초역());

                // then
                assertThatThrownBy(throwingCallable)
                        .isInstanceOf(CannotDeleteSectionException.class);
            }

            @DisplayName("노선의 마지막 구간을 제거 시도")
            @Test
            void 노선의_마지막_구간을_제거_시도() {
                // given
                Sections 이호선 = new Sections();
                Section 강남역_역삼역_구간 = Section.of(강남역(), 역삼역(), 5);

                이호선.addSection(강남역_역삼역_구간);

                // when
                ThrowableAssert.ThrowingCallable throwingCallable = () -> 이호선.deleteSectionBy(역삼역());

                // then
                assertThatThrownBy(throwingCallable)
                        .isInstanceOf(CannotDeleteSectionException.class);
            }
        }
    }

    private void 등록된_구간_순서_확인(Sections sections, Station... expectedStations) {
        List<Station> stations = sections.getStations();

        for (int i = 0; i < expectedStations.length; i++) {
            assertThat(stations.get(i))
                    .isEqualTo(expectedStations[i]);
        }
    }
}
