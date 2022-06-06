package nextstep.subway.section;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.util.List;
import java.util.Map;

import static nextstep.subway.line.LineAcceptanceTest.*;
import static nextstep.subway.station.StationAcceptanceTest.역_객체로_변환;
import static nextstep.subway.station.StationAcceptanceTest.지하철역_생성;
import static nextstep.subway.utils.RequestUtil.요청_성공_실패_여부_확인;

public class SectionAcceptanceTest extends BasicAcceptance {

    private Station 강남역;
    private Station 양재역;

    LineResponse 생성된_신분당선;

    @Autowired
    LineStationRepository lineStationRepository;

    @Override
    protected void beforeEachInit() {
        강남역 = 역_객체로_변환(지하철역_생성("강남역"));
        양재역 = 역_객체로_변환(지하철역_생성(("양재역")));

        final LineRequest 신분당선 = new LineRequest("신분당선", "bg-red-600", 강남역.getId(), 양재역.getId(), 10L);

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
        Station 양재시민의숲 = 역_객체로_변환(지하철역_생성("양재시민의숲"));

        List<LineStation> lineStationsByLineId = lineStationRepository.findLineStationsByLineId(생성된_신분당선.getId());

        // When
        ExtractableResponse<Response> 구간_생성_요청_결과 = 구간_생성_요청(생성된_신분당선.getId(), 구간_요청_객체_생성(양재역.getId(), 양재시민의숲.getId(), 10L));

        //Then
        구간_생성됨(구간_생성_요청_결과);
    }

    public static ExtractableResponse<Response> 구간_생성됨(ExtractableResponse<Response> response) {
        요청_성공_실패_여부_확인(response, HttpStatus.CREATED);
        return response;
    }

    private ExtractableResponse<Response> 구간_생성_요청(final Long id, final SectionRequest SectionRequest) {
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
