package nextstep.subway.line;

import static nextstep.subway.fixtrue.Param.*;
import static nextstep.subway.fixtrue.TestFactory.*;
import static nextstep.subway.station.StationAcceptanceTest.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.HttpStatus.*;

import java.util.ArrayList;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import io.restassured.path.json.JsonPath;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.fixtrue.Param;
import nextstep.subway.fixtrue.TestFactory;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.SectionRequest;
import nextstep.subway.station.dto.StationRequest;
import nextstep.subway.station.dto.StationResponse;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {

    public static final String LINE_ONE = "1호선";
    public static final String LINE_ONE_COLOR_RED = "bg-red-600";
    public static final String LINE_TWO = "2호선";
    public static final String LINE_TWO_COLOR_GREEN = "bg-green-600";
    public static final String LINE_THREE = "3호선";
    public static final String LINE_THREE_YELLOW = "bg-yellow-600";


    @Test
    void 지하철_노선을_생성한다() {

        StationResponse station1 = 지하철역_등록되어_있음(신촌역);
        StationResponse station2 = 지하철역_등록되어_있음(강남역);
        LineRequest lineRequest = new LineRequest(LINE_ONE, LINE_ONE_COLOR_RED, station1.getId(), station2.getId(), 10);

        // when
        // 지하철_노선_생성_요청
        ExtractableResponse<Response> post = 지하철_노선_생성_요청(lineRequest);

        // then
        // 지하철_노선_생성됨
        assertThat(post.statusCode()).isEqualTo(CREATED.value());
    }

    @Test
    void 기존에_존재하는_지하철_노선_이름으로_지하철_노선을_생성한다() {
        // given
        // 지하철_노선_등록되어_있음
        StationResponse station1 = 지하철역_등록되어_있음(신촌역);
        StationResponse station2 = 지하철역_등록되어_있음(강남역);
        LineRequest createLine = new LineRequest(LINE_ONE, LINE_ONE_COLOR_RED, station1.getId(), station2.getId(), 10);
        지하철_노선_등록되어_있음(createLine);

        // when
        // 존재하는 지하철 노선 생성 요청
        ExtractableResponse<Response> post = 지하철_노선_생성_요청(createLine);

        // then
        // 지하철_노선_생성_실패됨
        assertThat(post.statusCode()).isEqualTo(BAD_REQUEST.value());
    }

    @Test
    void 지하철_노선_목록을_조회한다() {

        // given
        StationResponse station1 = 지하철역_등록되어_있음(신촌역);
        StationResponse station2 = 지하철역_등록되어_있음(강남역);
        StationResponse station3 = 지하철역_등록되어_있음(서울역);
        StationResponse station4 = 지하철역_등록되어_있음(용산역);
        지하철_노선_등록되어_있음(new LineRequest(LINE_ONE, LINE_ONE_COLOR_RED, station1.getId(), station2.getId(), 10));
        지하철_노선_등록되어_있음(new LineRequest(LINE_TWO, LINE_TWO_COLOR_GREEN, station3.getId(), station4.getId(), 10));

        // when
        ExtractableResponse<Response> response = 지하철_노선_조회_요청();

        // then
        assertThat(response.statusCode()).isEqualTo(OK.value());
        지하철_응답_내용_확인(response);
    }

    private void 지하철_응답_내용_확인(ExtractableResponse<Response> response) {
        JsonPath jsonPath = response.jsonPath();
        String lineName1 = jsonPath.getString("[0].name");
        String subwayName1 = jsonPath.getString("[0].stations[0].name");
        String lineName2 = jsonPath.getString("[1].name");
        String subwayName2 = jsonPath.getString("[1].stations[0].name");
        assertThat(lineName1).isEqualTo(LINE_ONE);
        assertThat(subwayName1).isEqualTo(신촌역);
        assertThat(lineName2).isEqualTo(LINE_TWO);
        assertThat(subwayName2).isEqualTo(서울역);
    }

    @Test
    void 지하철_노선을_조회한다() {

        // given
        StationResponse station1 = 지하철역_등록되어_있음(신촌역);
        StationResponse station2 = 지하철역_등록되어_있음(강남역);
        LineResponse lineResponse = 지하철_노선_등록되어_있음(
            new LineRequest(LINE_ONE, LINE_ONE_COLOR_RED, station1.getId(), station2.getId(), 10));

        // when
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(lineResponse.getId());

        // then
        // 지하철_노선_응답됨
        assertThat(response.statusCode()).isEqualTo(OK.value());
        지하철_노선_데이터_확인(response);
    }

    private void 지하철_노선_데이터_확인(ExtractableResponse<Response> response) {
        JsonPath jsonPath = response.jsonPath();
        String lineName = jsonPath.getString("name");
        List stations = jsonPath.getObject("stations.name", List.class);
        assertThat(lineName).isEqualTo(LINE_ONE);
        assertThat(stations).containsExactly(신촌역, 강남역);
    }


    @Test
    void 지하철_노선을_수정한다() {

        // given
        StationResponse station1 = 지하철역_등록되어_있음(강남역);
        StationResponse station2 = 지하철역_등록되어_있음(신촌역);
        LineResponse lineResponse = 지하철_노선_등록되어_있음(
            new LineRequest(LINE_ONE, LINE_ONE_COLOR_RED, station1.getId(), station2.getId(), 10));

        // when
        LineRequest updateRequest = new LineRequest(LINE_THREE, LINE_THREE_YELLOW, station1.getId(), station2.getId(), 10);
        ExtractableResponse<Response> response = 지하철_노선_수정_요청(lineResponse.getId(), updateRequest);

        // then
        LineResponse expectedResponse = toResponseData(response, LineResponse.class);
        assertAll(
            () -> assertThat(expectedResponse.getName()).isEqualTo(LINE_THREE),
            () -> assertThat(expectedResponse.getColor()).isEqualTo(LINE_THREE_YELLOW)
        );
    }

    @Test
    void 지하철_노선을_제거한다() {

        // given
        StationResponse station1 = 지하철역_등록되어_있음(강남역);
        StationResponse station2 = 지하철역_등록되어_있음(신촌역);
        LineResponse lineResponse = 지하철_노선_등록되어_있음(
            new LineRequest(LINE_ONE, LINE_ONE_COLOR_RED, station1.getId(), station2.getId(), 10));

        // when
        ExtractableResponse<Response> response = 지하철_노선_제거_요청(lineResponse.getId());

        // then
        // 지하철_노선_삭제됨
        assertThat(response.statusCode()).isEqualTo(NO_CONTENT.value());
    }

    /**
     * 용산역 -> 강남역 -> 신촌역
     */
    @Test
    void 노선에_새로운_상행_구간을_등록한다() {
        // given
        StationResponse stationGangnam = 지하철역_등록되어_있음(강남역);
        StationResponse stationSinchon = 지하철역_등록되어_있음(신촌역);
        LineResponse lineResponse = 지하철_노선_등록되어_있음(
            new LineRequest(LINE_ONE, LINE_ONE_COLOR_RED, stationGangnam.getId(), stationSinchon.getId(), 10));

        StationResponse stationYoungSan = 지하철역_등록되어_있음(용산역);

        // when
        지하철_노선에_새로운_구간_등록_요청(lineResponse, stationYoungSan, stationGangnam, 5);

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(lineResponse.getId());
        지하철_노선에_해당하는_역_확인(response);
    }

    private void 지하철_노선에_해당하는_역_확인(ExtractableResponse<Response> response) {
        JsonPath jsonPath = response.jsonPath();
        String lineName = jsonPath.getString("name");
        List stations = jsonPath.getObject("stations.name", List.class);
        assertThat(lineName).isEqualTo(LINE_ONE);
        assertThat(stations).containsExactly(용산역, 강남역, 신촌역);
    }


    @Test
    void 노선에_새로운_중간_구간을_등록한다() {
        // given
        StationResponse stationGangnam = 지하철역_등록되어_있음(강남역);
        StationResponse stationSinchon = 지하철역_등록되어_있음(신촌역);
        LineResponse lineResponse = 지하철_노선_등록되어_있음(
            new LineRequest(LINE_ONE, LINE_ONE_COLOR_RED, stationGangnam.getId(), stationSinchon.getId(), 10));

        StationResponse stationYoungSan = 지하철역_등록되어_있음(용산역);
        StationResponse stationSeoul = 지하철역_등록되어_있음(서울역);
        StationResponse stationYeokSam = 지하철역_등록되어_있음(역삼역);
        // when
        지하철_노선에_새로운_구간_등록_요청(lineResponse, stationYoungSan, stationGangnam, 5);
        지하철_노선에_새로운_구간_등록_요청(lineResponse, stationSeoul, stationSinchon, 3);
        지하철_노선에_새로운_구간_등록_요청(lineResponse, stationYeokSam, stationGangnam, 3);

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(lineResponse.getId());
        지하철_중간_구간_추가_확인(response);
    }

    private void 지하철_중간_구간_추가_확인(ExtractableResponse<Response> response) {
        JsonPath jsonPath = response.jsonPath();
        String lineName = jsonPath.getString("name");
        List stations = jsonPath.getObject("stations.name", List.class);
        assertThat(lineName).isEqualTo(LINE_ONE);
        assertThat(stations).containsExactly(용산역, 역삼역, 강남역, 서울역, 신촌역);
    }

    /**
     * 강남역 -> 신촌역 -> 용산역
     */
    @Test
    void 노선에_새로운_하행_구간을_등록한다() {
        // given
        StationResponse stationGangnam = 지하철역_등록되어_있음(강남역);
        StationResponse stationSinchon = 지하철역_등록되어_있음(신촌역);
        LineResponse lineResponse = 지하철_노선_등록되어_있음(
            new LineRequest(LINE_ONE, LINE_ONE_COLOR_RED, stationGangnam.getId(), stationSinchon.getId(), 10));

        StationResponse stationYounSan = 지하철역_등록되어_있음(용산역);

        // when
        ExtractableResponse<Response> response = 지하철_노선에_새로운_구간_등록_요청(lineResponse, stationSinchon, stationYounSan, 5);

        // then
        ExtractableResponse<Response> dataResponse = 지하철_노선_조회_요청(lineResponse.getId());
        지하철_노선에_지하철역_등록됨(response);
        하행_구간추가_데이터_확인(dataResponse);
    }

    private void 하행_구간추가_데이터_확인(ExtractableResponse<Response> response) {
        JsonPath jsonPath = response.jsonPath();
        String lineName = jsonPath.getString("name");
        List stations = jsonPath.getObject("stations.name", List.class);
        assertThat(lineName).isEqualTo(LINE_ONE);
        assertThat(stations).containsExactly(강남역, 신촌역, 용산역);
    }


    @Test
    void 노선에_새로운_구간_등록시_상행역_하행역_둘중_하나라도_존재하지_않는경우_예외() {
        StationResponse stationGangnam = 지하철역_등록되어_있음(강남역);
        StationResponse stationSinchon = 지하철역_등록되어_있음(신촌역);
        LineResponse lineResponse = 지하철_노선_등록되어_있음(
            new LineRequest(LINE_ONE, LINE_ONE_COLOR_RED, stationGangnam.getId(), stationSinchon.getId(), 10));

        StationResponse stationSeoul = 지하철역_등록되어_있음(서울역);
        StationResponse stationYounSan = 지하철역_등록되어_있음(용산역);

        // when
        ExtractableResponse<Response> response = 지하철_노선에_새로운_구간_등록_요청(lineResponse, stationSeoul, stationYounSan, 5);

        // then
        지하철_노선에_새로운구간_등록_실패(response);
    }

    @Test
    void 노선에_새로운_구간_등록시_기존_거리보다_큰_경우_예외() {
        StationResponse stationGangnam = 지하철역_등록되어_있음(강남역);
        StationResponse stationSinchon = 지하철역_등록되어_있음(신촌역);
        LineResponse lineResponse = 지하철_노선_등록되어_있음(
            new LineRequest(LINE_ONE, LINE_ONE_COLOR_RED, stationGangnam.getId(), stationSinchon.getId(), 10));

        StationResponse stationSeoul = 지하철역_등록되어_있음(강남역);
        StationResponse stationYounSan = 지하철역_등록되어_있음(용산역);

        // when
        ExtractableResponse<Response> response = 지하철_노선에_새로운_구간_등록_요청(lineResponse, stationSeoul, stationYounSan, 11);

        // then
        지하철_노선에_새로운구간_등록_실패(response);
    }

    @Test
    void 노선에_새로운_구간_등록시_기존에_존재하는_구간이면_예외() {
        StationResponse stationGangnam = 지하철역_등록되어_있음(강남역);
        StationResponse stationSinchon = 지하철역_등록되어_있음(신촌역);
        LineResponse lineResponse = 지하철_노선_등록되어_있음(
            new LineRequest(LINE_ONE, LINE_ONE_COLOR_RED, stationGangnam.getId(), stationSinchon.getId(), 10));
        // when
        ExtractableResponse<Response> response = 지하철_노선에_새로운_구간_등록_요청(lineResponse, stationGangnam, stationSinchon, 5);

        // then
        지하철_노선에_새로운구간_등록_실패(response);
    }

    @Test
    void 지하철_구간을_삭제_한다() {
        // given
        StationResponse stationGangnam = 지하철역_등록되어_있음(강남역);
        StationResponse stationSinchon = 지하철역_등록되어_있음(신촌역);
        StationResponse stationSeoul = 지하철역_등록되어_있음(서울역);
        StationResponse stationYounSan = 지하철역_등록되어_있음(용산역);

        LineResponse lineResponse = 지하철_노선_등록되어_있음(
            new LineRequest(LINE_ONE, LINE_ONE_COLOR_RED, stationGangnam.getId(), stationSinchon.getId(), 10));
        지하철_노선에_새로운_구간_등록_요청(lineResponse, stationSeoul, stationYounSan, 5);

        // when
        ExtractableResponse<Response> response = 지하철_구간_삭제요청(lineResponse.getId(), stationGangnam.getId());

        // then
        지하철_삭제됨(response);
    }

    @Test
    void 구간이_하나인_노선이면_삭제_예외() {
        // given
        StationResponse stationGangnam = 지하철역_등록되어_있음(강남역);
        StationResponse stationSinchon = 지하철역_등록되어_있음(신촌역);
        StationResponse stationSeoul = 지하철역_등록되어_있음(서울역);
        StationResponse stationYounSan = 지하철역_등록되어_있음(용산역);

        LineResponse lineResponse = 지하철_노선_등록되어_있음(
            new LineRequest(LINE_ONE, LINE_ONE_COLOR_RED, stationGangnam.getId(), stationSinchon.getId(), 10));
        지하철_노선에_새로운_구간_등록_요청(lineResponse, stationSeoul, stationYounSan, 5);

        // when
        ExtractableResponse<Response> response = 지하철_구간_삭제요청(lineResponse.getId(), stationGangnam.getId());

        // then
        지하철_삭제_실패(response);
    }

    public static ExtractableResponse<Response> 지하철_노선_생성_요청(Object obj) {
        return TestFactory.post("/lines", obj);
    }

    public static LineResponse 지하철_노선_등록되어_있음(Object obj) {
        ExtractableResponse<Response> response = 지하철_노선_생성_요청(obj);
        return response.jsonPath().getObject(".", LineResponse.class);
    }

    public static ExtractableResponse<Response> 지하철_노선_조회_요청(Long id) {
        return TestFactory.get("lines/{id}", id);
    }

    public static ExtractableResponse<Response> 지하철_노선_조회_요청() {
        return TestFactory.get("lines");
    }

    public static ExtractableResponse<Response> 지하철_노선_수정_요청(Long id, Object obj) {
        return TestFactory.update("lines/{id}", id, obj);
    }

    public static ExtractableResponse<Response> 지하철_노선_제거_요청(Long id) {
        return TestFactory.delete("lines/{id}", id);
    }

    public void 지하철_노선에_지하철역_등록됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    public void 지하철_노선에_새로운구간_등록_실패(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    public ExtractableResponse<Response> 지하철_노선에_새로운_구간_등록_요청(
        LineResponse lineResponse, StationResponse stationGangnam,
        StationResponse stationSinchon, int distance) {
        Param param = createParam()
            .addParam("lineId", lineResponse.getId());
        return post("/lines/{lineId}/sections", param,
                    new SectionRequest(stationGangnam.getId(), stationSinchon.getId(), distance)
        );
    }

    private ExtractableResponse<Response> 지하철_구간_삭제요청(Long lineId, Long stationId) {
        Param param = Param.createParam()
                           .addParam("stationId", stationId);
        return delete("/lines/{lineId}/sections", lineId, param);
    }

    public static void 지하철_삭제_실패(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    private void 지하철_삭제됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(OK.value());
    }

}
