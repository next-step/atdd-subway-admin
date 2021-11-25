package nextstep.subway.line;

import nextstep.subway.AcceptanceTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("지하철 구간 인수 테스트")
class SectionAcceptanceTest extends AcceptanceTest {

    @DisplayName("지하철 구간의 상행 종점으로 지하철 구간을 생성한다.")
    @Test
    void createSectionUpStation() {
        // given

        // when

        // then
    }

    @DisplayName("지하철 구간의 하행 종점으로 지하철 구간을 생성한다.")
    @Test
    void createSectionDownStation() {
        // given

        // when

        // then
    }

    @DisplayName("지하철 역 사이에 지하철 구간을 생성한다.")
    @Test
    void createSectionBetweenStations() {
        // given

        // when

        // then
    }

    @DisplayName("지하철 역이 등록되어 있지 않은 경우 구간 생성에 실패한다.")
    @Test
    void createSectionWithoutTerminus() {
        // given

        // when

        // then
    }

    @DisplayName("같은 지하철 구간이 이미 등록되어 있는 경우 구간 생성에 실패한다.")
    @Test
    void createSectionWithSameSection() {
        // given

        // when

        // then
    }

    @DisplayName("지하철 구간의 거리가 최소 거리 이하인 경우 구간 생성에 실패한다.")
    @Test
    void createSectionLessThenMinDistance() {
        // given

        // when

        // then
    }

    @DisplayName("기존 역 사이의 길이보다 크거나 같은 경우 구간 생성에 실패한다.")
    @Test
    void createSectionLessThenDistanceBetweenStations() {
        // given

        // when

        // then
    }

    @DisplayName("지하철 구간 상행역 부터 하행역 순으로 정렬되어 조회된다.")
    @Test
    void findSections() {
        // given

        // when

        // then
    }

}
