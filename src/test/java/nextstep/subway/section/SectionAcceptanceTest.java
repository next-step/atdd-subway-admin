package nextstep.subway.section;

import static nextstep.subway.line.LineAcceptanceTest.지하철역과_노선_동시_생성;
import static nextstep.subway.station.StationAcceptanceTest.지하철역_생성;

import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

@DisplayName("지하철 구간 관련 기능")
public class SectionAcceptanceTest {

    /*
        유효성 검사 시나리오
            WHEN 새로운 노선과 지하철 역을 생성한다.
            THEN 지하철 역 조회 시 생성한 지하철 역을 찾을 수 있다.

            WHEN 역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같게 등록한다.
            THEN 구간 추가에 실패한다.

            WHEN 상행역과 하행역이 노선에 등록되어있는 동일한 역으로 등록한다.
            THEN 구간 추가에 실패한다.

            WHEN 상행역과 하행역이 없는 상태로 등록한다.
            THEN 구간 추가에 실패한다.
     */
    @TestFactory
    Stream<DynamicTest> createSection_fail() {
        return Stream.of(
            DynamicTest.dynamicTest("새로운 노선과 지하철 역을 생성한다.", () -> {
                지하철역과_노선_동시_생성("신림역", "서울대입구역", "2호선", "bg-green-600", 10L);
                지하철역_생성("봉천역");
            })
        );
    }

}
