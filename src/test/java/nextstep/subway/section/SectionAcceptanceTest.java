package nextstep.subway.section;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.BaseAcceptanceTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static nextstep.subway.line.LineTestUtils.*;
import static nextstep.subway.station.StationTestUtils.*;
import static nextstep.subway.section.SectionTestUtils.*;

@DisplayName("지하철 구간 관련 기능")
public class SectionAcceptanceTest extends BaseAcceptanceTest {
    private Long 낙성대역_ID;
    private Long 강남역_ID;
    private Long 삼성역_ID;
    private Long 종합운동장역_ID;
    private Long 잠실역_ID;
    private Long 건대입구역_ID;
    private Long 노선_ID;

    @BeforeEach
    public void init() {
        // given
        ExtractableResponse<Response> 낙성대역_생성_응답 = 지하철역_생성_요청("낙성대역");
        낙성대역_ID = 지하철역_ID_조회(낙성대역_생성_응답);
        ExtractableResponse<Response> 강남역_생성_응답 = 지하철역_생성_요청("강남역");
        강남역_ID = 지하철역_ID_조회(강남역_생성_응답);
        ExtractableResponse<Response> 삼성역_생성_응답 = 지하철역_생성_요청("삼성역");
        삼성역_ID = 지하철역_ID_조회(삼성역_생성_응답);
        ExtractableResponse<Response> 종합운동장역_생성_응답 = 지하철역_생성_요청("종합운동장역");
        종합운동장역_ID = 지하철역_ID_조회(종합운동장역_생성_응답);
        ExtractableResponse<Response> 잠실역_생성_응답 = 지하철역_생성_요청("잠실역");
        잠실역_ID = 지하철역_ID_조회(잠실역_생성_응답);
        ExtractableResponse<Response> 건대입구역_생성_응답 = 지하철역_생성_요청("건대입구");
        건대입구역_ID = 지하철역_ID_조회(건대입구역_생성_응답);

        ExtractableResponse<Response> 노선_생성_응답 = 지하철노선_생성_요청("2호선", "green", 강남역_ID, 잠실역_ID, 6);
        노선_ID = 지하철노선_ID_조회(노선_생성_응답);
    }

    /**
     * Given 지하철 노선이 생성되어 있다.
     * When 상행 또는 하행이 같고, 길이가 기존 역 사이 길이보다 짧은 구간을 추가하면
     * Then 노선 사이에 새로운 역이 추가된다
     */
    @DisplayName("지하철 노선에 구간을 추가한다. (역 사이에 새로운 역을 등록)")
    @Test
    public void addSection_between() {
        // when
        ExtractableResponse<Response> 구간_강남역_삼성역_추가_응답 = 구간_추가_요청(노선_ID, 강남역_ID, 삼성역_ID, 3);
        ExtractableResponse<Response> 구간_종합운동장역_잠실역_추가_응답 = 구간_추가_요청(노선_ID, 종합운동장역_ID, 잠실역_ID, 2);

        // then
        구간_추가_성공_확인(구간_강남역_삼성역_추가_응답);
        구간_추가_성공_확인(구간_종합운동장역_잠실역_추가_응답);

        // then
        지하철노선_역갯수_확인(노선_ID, 4);
    }

    /**
     * Given 지하철 노선이 생성되어 있다.
     * When 상행이 같은 구간을 추가하면
     * Then 새로운 역이 상행 종점으로 등록된다.
     */
    @DisplayName("지하철 노선에 구간을 추가한다. (새로운 역을 상행 종점으로 등록)")
    @Test
    public void addSection_up() {
        // when
        ExtractableResponse<Response> 구간_추가_응답 = 구간_추가_요청(노선_ID, 낙성대역_ID, 강남역_ID, 5);

        // then
        구간_추가_성공_확인(구간_추가_응답);

        // then
        지하철노선_역갯수_확인(노선_ID, 3);
    }

    /**
     * Given 지하철 노선이 생성되어 있다.
     * When 하행이 같은 구간을 추가하면
     * Then 새로운 역이 하행 종점으로 등록된다.
     */
    @DisplayName("지하철 노선에 구간을 추가한다. (새로운 역을 하행 종점으로 등록)")
    @Test
    public void addSection_down() {
        // when
        ExtractableResponse<Response> 구간_추가_응답 = 구간_추가_요청(노선_ID, 잠실역_ID, 건대입구역_ID, 4);

        // then
        구간_추가_성공_확인(구간_추가_응답);

        // then
        지하철노선_역갯수_확인(노선_ID, 3);
    }

    /**
     * Given 지하철 노선이 생성되어 있다.
     * When 하행이 같은 구간을 추가하면
     * Then 새로운 역이 하행 종점으로 등록된다.
     */
    @DisplayName("구간 추가 예외 - 역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없음")
    @Test
    public void addSection_fail_distance_size() {
        // when
        ExtractableResponse<Response> 구간_추가_응답 = 구간_추가_요청(노선_ID, 종합운동장역_ID, 잠실역_ID, 6);

        // then
        구간_추가_실패_확인(구간_추가_응답);
    }

    /**
     * Given 지하철 노선이 생성되어 있다.
     * When 하행이 같은 구간을 추가하면
     * Then 새로운 역이 하행 종점으로 등록된다.
     */
    @DisplayName("구간 추가 예외 - 상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없음")
    @Test
    public void addSection_fail_all_included() {
        // when
        ExtractableResponse<Response> 구간_추가_응답 = 구간_추가_요청(노선_ID, 잠실역_ID, 강남역_ID, 4);

        // then
        구간_추가_실패_확인(구간_추가_응답);
    }

    /**
     * Given 지하철 노선이 생성되어 있다.
     * When 하행이 같은 구간을 추가하면
     * Then 새로운 역이 하행 종점으로 등록된다.
     */
    @DisplayName("구간 추가 예외 - 상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없음")
    @Test
    public void addSection_fail_not_included() {
        // when
        ExtractableResponse<Response> 구간_추가_응답 = 구간_추가_요청(노선_ID, 종합운동장역_ID, 건대입구역_ID, 4);

        // then
        구간_추가_실패_확인(구간_추가_응답);
    }

    /**
     * Given 지하철 노선이 생성되어있으며, 2개의 구간이 등록되어 있다.
     * When 중간역을 제거하면
     * Then 구간이 재배치되며, 거리는 두 구간의 거리의 합으로 정해진다.
     */
    @DisplayName("지하철 노선의 중간역을 제거한다.")
    @Test
    public void deleteSection_center() {
        // given
        구간_추가_요청(노선_ID, 강남역_ID, 잠실역_ID, 6);
        구간_추가_요청(노선_ID, 종합운동장역_ID, 잠실역_ID, 2);

        // when
        ExtractableResponse<Response> 구간_삭제_응답 = 구간_삭제_요청(노선_ID, 종합운동장역_ID);

        // then
        구간_삭제_성공_확인(구간_삭제_응답);
        지하철노선_역갯수_확인(노선_ID, 2);
    }

    /**
     * Given 지하철 노선이 생성되어있으며, 2개의 구간이 등록되어 있다.
     * When 상행 종점역을 제거하면
     * Then 다음으로 오던 역이 상행 종점이 된다.
     */
    @DisplayName("지하철 노선의 상행 종점역을 제거한다.")
    @Test
    public void deleteSection_up() {
        // given
        구간_추가_요청(노선_ID, 강남역_ID, 잠실역_ID, 6);
        구간_추가_요청(노선_ID, 종합운동장역_ID, 잠실역_ID, 2);

        // when
        ExtractableResponse<Response> 구간_삭제_응답 = 구간_삭제_요청(노선_ID, 강남역_ID);

        // then
        구간_삭제_성공_확인(구간_삭제_응답);
        지하철노선_역갯수_확인(노선_ID, 2);
    }

    /**
     * Given 지하철 노선이 생성되어있으며, 2개의 구간이 등록되어 있다.
     * When 하행 종점역을 제거하면
     * Then 다음으로 오던 역이 하행 종점이 된다.
     */
    @DisplayName("지하철 노선의 하행 종점역을 제거한다.")
    @Test
    public void deleteSection_down() {
        // given
        구간_추가_요청(노선_ID, 강남역_ID, 잠실역_ID, 6);
        구간_추가_요청(노선_ID, 종합운동장역_ID, 잠실역_ID, 2);

        // when
        ExtractableResponse<Response> 구간_삭제_응답 = 구간_삭제_요청(노선_ID, 잠실역_ID);

        // then
        구간_삭제_성공_확인(구간_삭제_응답);
        지하철노선_역갯수_확인(노선_ID, 2);
    }

    /**
     * Given 지하철 노선이 생성되어있으며, 1개의 구간이 등록되어 있다.
     * When 등록되지 않은 역을 제거하면
     * Then 구간 제거에 실패한다.
     */
    @DisplayName("지하철 노선의 구간 제거 실패 - 등록되지 않은 역 제거")
    @Test
    public void deleteSection_fail_not_registered() {
        // given
        구간_추가_요청(노선_ID, 강남역_ID, 잠실역_ID, 6);

        // when
        ExtractableResponse<Response> 구간_삭제_응답 = 구간_삭제_요청(노선_ID, 삼성역_ID);

        // then
        구간_삭제_찾기_실패_확인(구간_삭제_응답);
    }

    /**
     * Given 지하철 노선이 생성되어있으며, 1개의 구간이 등록되어 있다.
     * When 노선의 마지막 구간을 제거하면
     * Then 구간 제거에 실패한다.
     */
    @DisplayName("지하철 노선의 구간 제거 실패 - 마지막구간 제거")
    @Test
    public void deleteSection_fail_last_station() {
        // given
        구간_추가_요청(노선_ID, 강남역_ID, 잠실역_ID, 6);

        // when
        ExtractableResponse<Response> 구간_삭제_응답 = 구간_삭제_요청(노선_ID, 강남역_ID);

        // then
        구간_삭제_실패_확인(구간_삭제_응답);
    }
}
