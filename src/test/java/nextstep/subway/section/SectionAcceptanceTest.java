package nextstep.subway.section;

import nextstep.subway.common.AcceptanceTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


@DisplayName("구간 인수 테스트")
class SectionAcceptanceTest extends AcceptanceTest {

    /**
     * Given 이미 생성된 구간과 구간에 등록되지 않은 역이 존재하고
     * When 구간 사이에 등록되지 않은 지하철 역을 역을 등록하면
     * Then 지하철역이 등록된다.
     */
    @DisplayName("역 사이에 새로운 역 등록")
    @Test
    void registerStation() { }

    @DisplayName("새로운 역을 상행 종점으로 등록")
    @Test
    void registerUpTerminalStation() { }

    @DisplayName("새로운 역을 하행 종점으로 등록")
    @Test
    void registerDownTerminalStation() { }

    @DisplayName("역 사이에 새로운 역을 등록하는 경우 기존 역 사이 길이보다 크거나 같으면 등록 불가")
    @Test
    void registerOverSectionLengthStationError() { }

    @DisplayName("이미 노선에 모두 등록된 상행 종점, 하행 종점역 등록")
    @Test
    void registerUpAndDownStationError() { }

    @DisplayName("등록하려는 두 역이 모두 노선에 포함되지 않은 경우")
    @Test
    void registerBothNoneStation() { }
}
