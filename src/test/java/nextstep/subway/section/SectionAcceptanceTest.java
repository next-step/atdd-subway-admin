package nextstep.subway.section;

import com.fasterxml.jackson.databind.ObjectMapper;
import groovy.util.logging.Log;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.BasicAcceptance;
import nextstep.subway.domain.LineStation;
import nextstep.subway.domain.LineStationRepository;
import nextstep.subway.domain.Station;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.dto.SectionRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static nextstep.subway.line.LineAcceptanceTest.*;
import static nextstep.subway.station.StationAcceptanceTest.역_객체로_변환;
import static nextstep.subway.station.StationAcceptanceTest.지하철역_생성;
import static nextstep.subway.utils.RequestUtil.요청_성공_실패_여부_확인;

public class SectionAcceptanceTest extends BasicAcceptance {


    LineResponse 생성된_신분당선;

    private Map<String, Long> 저장된역정보테이블;

    @Override
    protected void beforeEachInit() {
        저장된역정보테이블 = new HashMap<String, Long>() {{
            put("강남역", 역_객체로_변환(지하철역_생성("강남역")).getId());
            put("양재역", 역_객체로_변환(지하철역_생성(("양재역"))).getId());
            put("수원역", 역_객체로_변환(지하철역_생성("수원역")).getId());
            put("세류역", 역_객체로_변환(지하철역_생성("세류역")).getId());
            put("병점역", 역_객체로_변환(지하철역_생성(("병점역"))).getId());
            put("양재시민의숲", 역_객체로_변환(지하철역_생성("양재시민의숲")).getId());
        }};

        final LineRequest 신분당선 = new LineRequest("신분당선", "bg-red-600", 저장된역정보테이블.get("강남역"), 저장된역정보테이블.get("양재역"), 10L);

        // then
        생성된_신분당선 = 지하철_노선_생성됨(지하철_노선_생성(신분당선)).as(LineResponse.class);
    }

    /**
     * Given 생성된 지하철 노선에
     * When 새로운 구간을 등록하면
     * Then 해당 지하철 노선에 구간이 등록된다.
     */
    @DisplayName("기존 노선에 구간을 등록한다.")
    @Test
    @Transactional
    void addSectionTest() {
        // Given
        지하철_노선_검색됨(지하철_노선_일부_검색(생성된_신분당선.getId()), "신분당선");

        // When
        ExtractableResponse<Response> 구간_생성_요청_결과 = 구간_생성_요청(생성된_신분당선.getId(), 구간_요청_객체_생성(저장된역정보테이블.get("양재역"), 저장된역정보테이블.get("양재시민의숲"), 10L));

        //Then
        구간_생성됨(구간_생성_요청_결과);
    }

    /**
     * Given 요청할려고 하는 구간이 등록된 지하철 노선에
     * When 다시 동일 구간을 요청하면
     * Then 등록되지 않는다.
     */
    @DisplayName("동일한 구간 저장시 저장되지 않는다.")
    @Test
    void requestSameSectionTest() {
        // Given
        구간_생성됨(구간_생성_요청(생성된_신분당선.getId(), 구간_요청_객체_생성(저장된역정보테이블.get("양재역"), 저장된역정보테이블.get("양재시민의숲"), 10L)));

        // When
        ExtractableResponse<Response> 같은_구간_요청_결과 = 구간_생성_요청(생성된_신분당선.getId(), 구간_요청_객체_생성(저장된역정보테이블.get("양재역"), 저장된역정보테이블.get("양재시민의숲"), 5L));

        // Then
        구간_생성_실패됨(같은_구간_요청_결과);
    }

    /**
     * Given 등록된 지하철 노선에
     * When 구간 사이에 Distance 가 기존보다 작은 새로운 구간을 요청하면
     * Then 등록된다.
     */
    @DisplayName("구간사이에 Distance 가 기존보다 작을 경우 사이에 등록된다.")
    @Test
    void insertToMiddleTest() {

        final LineResponse 생성된_일호선 = 초기_노선_생성("일호선", 저장된역정보테이블.get("수원역"), 저장된역정보테이블.get("병점역"), 10L);

        // When

        ExtractableResponse<Response> 구간_생성_요청_결과 = 구간_생성됨(구간_생성_요청(생성된_일호선.getId(), 구간_요청_객체_생성(저장된역정보테이블.get("수원역"), 저장된역정보테이블.get("세류역"), 5L)));

        // When
        구간_생성됨(구간_생성_요청_결과);
    }

    /**
     * Given 등록된 지하철 노선에
     * When 구간 사이에 Distance 가 기존보다 큰 경우 새로운 구간을 요청하면
     * Then 등록되지 않는다.
     */
    @DisplayName("구간사이에 Distance 가 기존보다 큰 경우 사이에 등록하면 등록이 되지 않는다.")
    @Test
    void invalidInsertToMiddleWhenDistanceIsBigTest() {
        final LineResponse 생성된_일호선 = 초기_노선_생성("일호선", 저장된역정보테이블.get("수원역"), 저장된역정보테이블.get("병점역"), 10L);

        // When

        ExtractableResponse<Response> 구간_생성_요청_결과 = 구간_생성_요청(생성된_일호선.getId(), 구간_요청_객체_생성(저장된역정보테이블.get("수원역"), 저장된역정보테이블.get("세류역"), 11L));

        // When
        구간_생성_실패됨(구간_생성_요청_결과);
    }

    /**
     * Given 등록된 지하철 노선에
     * When 구간 사이에 Distance 가 기존보다 큰 경우 새로운 구간을 요청하면
     * Then 등록되지 않는다.
     */
    @DisplayName("구간사이에 Distance 가 기존가 같아도 등록이 되지 않는다.")
    @Test
    void invalidInsertToMiddleWhenDistanceIsSameTest() {
        final LineResponse 생성된_일호선 = 초기_노선_생성("일호선", 저장된역정보테이블.get("수원역"), 저장된역정보테이블.get("병점역"), 10L);

        // When

        ExtractableResponse<Response> 구간_생성_요청_결과 = 구간_생성_요청(생성된_일호선.getId(), 구간_요청_객체_생성(저장된역정보테이블.get("수원역"), 저장된역정보테이블.get("세류역"), 10L));

        // Then
        구간_생성_실패됨(구간_생성_요청_결과);
    }

    /**
     * Given 구간이 저장된 역에
     * When 등록되지 않은 역을 삭제 요청 시
     * Then 삭제가 될수 없다.
     */
    @DisplayName("등록되지 않은 역을 요청시에 삭제가 될수 없다.")
    @Test
    void invalidRemoveTestWhenInputStationIdIsNotExist() {
        // given
        final LineResponse 생성된_일호선 = 초기_노선_생성("일호선", 저장된역정보테이블.get("수원역"), 저장된역정보테이블.get("병점역"), 10L);
        구간_생성됨(구간_생성_요청(생성된_일호선.getId(), 구간_요청_객체_생성(저장된역정보테이블.get("수원역"), 저장된역정보테이블.get("세류역"), 5L)));
        final Station 이상한역 = new Station(100L, "이상한역");
        // when
        ExtractableResponse<Response> 요청_결과 = 구간_삭제_요청(생성된_일호선.getId(), 이상한역.getId());

        // then
        구간_삭제_실패됨(요청_결과);
    }

    /**
     * Given 구간이 저장된 역에
     * When 노선에 등록되지 않은역을 삭제 요청 시
     * Then 삭제가 될수 없다.
     */
    @DisplayName("등록된 역이지만 노선에 등록되지 않은 역은 삭제 될수 없다.")
    @Test
    void invalidRemoveTestWhenInputStationIsNotRegister() {
        // given
        final LineResponse 생성된_일호선 = 초기_노선_생성("일호선", 저장된역정보테이블.get("수원역"), 저장된역정보테이블.get("병점역"), 10L);
        구간_생성됨(구간_생성_요청(생성된_일호선.getId(), 구간_요청_객체_생성(저장된역정보테이블.get("수원역"), 저장된역정보테이블.get("세류역"), 5L)));

        // when
        ExtractableResponse<Response> 요청_결과 = 구간_삭제_요청(생성된_일호선.getId(), 저장된역정보테이블.get("양재시민의숲"));

        // then
        구간_삭제_실패됨(요청_결과);
    }

    /**
     * Given 구간이 하나 등록된 노선에
     * When 노선에 등록된 역을 삭제 하면
     * Then 삭제가 될수 없다.
     */
    @DisplayName("노선에 구간이 1개만 등록된 경우 삭제 할수 없다.")
    @Test
    void invalidRemoveTestWhenLineHasOneSection() {
        // given
        final LineResponse 생성된_일호선 = 초기_노선_생성("일호선", 저장된역정보테이블.get("수원역"), 저장된역정보테이블.get("병점역"), 10L);

        // when
        ExtractableResponse<Response> 요청_결과 = 구간_삭제_요청(생성된_일호선.getId(), 저장된역정보테이블.get("병점역"));

        // then
        구간_삭제_실패됨(요청_결과);
    }
    private ExtractableResponse<Response> 구간_삭제_요청(final Long lineId, final Long stationId ) {
        return requestUtil.deleteSection(lineId, stationId);
    }

    private static LineResponse 초기_노선_생성 (final String name, final Long upStationId, final Long downStationId , final Long distance) {
        LineRequest lineRequest = new LineRequest(name, "bg-blue-600", upStationId, downStationId, distance);
        return 지하철_노선_생성됨(지하철_노선_생성(lineRequest)).as(LineResponse.class);
    }

    public static ExtractableResponse<Response>  구간_생성_실패됨(ExtractableResponse<Response> response) {
        요청_성공_실패_여부_확인(response, HttpStatus.BAD_REQUEST);
        return response;
    }

    public static ExtractableResponse<Response>  구간_삭제_실패됨(ExtractableResponse<Response> response) {
        요청_성공_실패_여부_확인(response, HttpStatus.BAD_REQUEST);
        return response;
    }

    public static ExtractableResponse<Response> 구간_생성됨(ExtractableResponse<Response> response) {
        요청_성공_실패_여부_확인(response, HttpStatus.CREATED);
        return response;
    }

    private static ExtractableResponse<Response> 구간_생성_요청(final Long id, final SectionRequest SectionRequest) {
        return requestUtil.createSection(id, convertMapBy(SectionRequest));
    }

    static SectionRequest 구간_요청_객체_생성(final Long upStationId, final Long downStationId, final Long distance) {
        return new SectionRequest(upStationId, downStationId, distance);
    }

    static Map<String, String> convertMapBy(final SectionRequest sectionRequest) {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.convertValue(sectionRequest, Map.class);
    }
}
