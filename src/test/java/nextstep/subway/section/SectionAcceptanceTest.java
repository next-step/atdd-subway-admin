package nextstep.subway.section;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.LineAcceptanceTest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.StationAcceptanceTest;
import nextstep.subway.station.dto.StationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 노선 구간 관련 기능")
public class SectionAcceptanceTest extends AcceptanceTest {

    private StationResponse 상행역;
    private StationResponse 강남역;
    private StationResponse 양재역;
    private StationResponse 정자역;
    private StationResponse 미금역;
    private StationResponse 광교역;
    private StationResponse 하행역;
    private LineResponse 신분당선;

    @BeforeEach
    public void setUp() {

        super.setUp();

        상행역 = StationAcceptanceTest.지하철역_등록되어_있음("상행역").as(StationResponse.class);
        강남역 = StationAcceptanceTest.지하철역_등록되어_있음("강남역").as(StationResponse.class);
        양재역 = StationAcceptanceTest.지하철역_등록되어_있음("양재역").as(StationResponse.class);
        정자역 = StationAcceptanceTest.지하철역_등록되어_있음("정자역").as(StationResponse.class);
        미금역 = StationAcceptanceTest.지하철역_등록되어_있음("미금역").as(StationResponse.class);
        광교역 = StationAcceptanceTest.지하철역_등록되어_있음("광교역").as(StationResponse.class);
        하행역 = StationAcceptanceTest.지하철역_등록되어_있음("하행역").as(StationResponse.class);

        Map<String, String> createParams = LineAcceptanceTest.지하철_노선_더미_데이터_신분상선(강남역.getId(), 광교역.getId(), 10);
        신분당선 = LineAcceptanceTest.지하철_노선_등록되어_있음(createParams).as(LineResponse.class);
    }

    @DisplayName("노선에 구간을 등록한다.")
    @Test
    void addSection() {

        // when
        // 지하철_노선에_지하철역_등록_요청
        ExtractableResponse<Response> registerSectionResponse1 = 지하철_노선에_지하철역_등록_요청(신분당선.getId(), 상행역.getId(), 강남역.getId(), 10);
        ExtractableResponse<Response> registerSectionResponse2 = 지하철_노선에_지하철역_등록_요청(신분당선.getId(), 강남역.getId(), 정자역.getId(), 7);
        ExtractableResponse<Response> registerSectionResponse3 = 지하철_노선에_지하철역_등록_요청(신분당선.getId(), 미금역.getId(), 광교역.getId(), 2);
        ExtractableResponse<Response> registerSectionResponse4 = 지하철_노선에_지하철역_등록_요청(신분당선.getId(), 강남역.getId(), 양재역.getId(), 2);

        // 지하철_노선에_조회
        ExtractableResponse<Response> lineResponse = LineAcceptanceTest.지하철_노선_조회(registerSectionResponse2.header("Location"));

        // then
        // 지하철_노선에_지하철역_등록됨
        Stream.of(registerSectionResponse1, registerSectionResponse2, registerSectionResponse3, registerSectionResponse4)
                .forEach(i -> 상태코드_확인(i, HttpStatus.CREATED.value()));

        assertThat(lineResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
        라인에_역들이_순서대로_나오는지_확인(lineResponse, 상행역.getName(), 강남역.getName(), 양재역.getName(), 정자역.getName(), 미금역.getName(), 광교역.getName());

    }

    @DisplayName("노선에 구간을 등록한다. - 새로운 역을 상행 종점으로 등록")
    @Test
    void addSection_새로운_역을_상행_종점으로_등록() {

        // when
        // 지하철_노선에_지하철역_등록_요청
        ExtractableResponse<Response> registerSectionResponse = 지하철_노선에_지하철역_등록_요청(신분당선.getId(), 상행역.getId(), 강남역.getId(), 10);

        // 지하철_노선에_조회
        ExtractableResponse<Response> lineResponse = LineAcceptanceTest.지하철_노선_조회(registerSectionResponse.header("Location"));

        // then
        // 지하철_노선에_지하철역_등록됨
        assertThat(registerSectionResponse.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(lineResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(lineResponse.jsonPath().getList("stations", StationResponse.class).get(0).getName()).isEqualTo(상행역.getName());
        assertThat(lineResponse.jsonPath().getList("stations", StationResponse.class).get(1).getName()).isEqualTo(강남역.getName());
        assertThat(lineResponse.jsonPath().getList("stations", StationResponse.class).get(2).getName()).isEqualTo(광교역.getName());
    }

    @DisplayName("노선에 구간을 등록한다. - 새로운 역을 하행 종점으로 등록할 경우")
    @Test
    void addSection_새로운_역을_하행_종점으로_등록() {

        // when
        // 지하철_노선에_지하철역_등록_요청
        ExtractableResponse<Response> registerSectionResponse = 지하철_노선에_지하철역_등록_요청(신분당선.getId(), 광교역.getId(), 하행역.getId(), 10);

        // 지하철_노선에_조회
        ExtractableResponse<Response> lineResponse = LineAcceptanceTest.지하철_노선_조회(registerSectionResponse.header("Location"));

        // then
        // 지하철_노선에_지하철역_등록됨
        assertThat(registerSectionResponse.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(lineResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(lineResponse.jsonPath().getList("stations", StationResponse.class).get(0).getName()).isEqualTo(강남역.getName());
        assertThat(lineResponse.jsonPath().getList("stations", StationResponse.class).get(1).getName()).isEqualTo(광교역.getName());
        assertThat(lineResponse.jsonPath().getList("stations", StationResponse.class).get(2).getName()).isEqualTo(하행역.getName());
    }

    @DisplayName("노선에 구간을 등록한다. - 역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없음")
    @Test
    void addSectionException_역_사이_길이보다_크거나_같은_경우() {

        // when
        // 지하철_노선에_지하철역_등록_요청
        ExtractableResponse<Response> response = 지하철_노선에_지하철역_등록_요청(신분당선.getId(), 강남역.getId(), 정자역.getId(), 10);

        // then
        // 지하철_노선에_지하철역_등록됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("노선에 구간을 등록한다. - 상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없음")
    @Test
    void addSectionException_이미_노선에_모두_등록되어_있는_경우() {

        // when
        // 지하철_노선에_지하철역_등록_요청
        ExtractableResponse<Response> response = 지하철_노선에_지하철역_등록_요청(신분당선.getId(), 강남역.getId(), 광교역.getId(), 10);
        // then
        // 지하철_노선에_지하철역_등록됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("노선에 구간을 등록한다. - 상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없음")
    @Test
    void addSectionException_상행역과_하행역_둘_중_하나도_포함되어있지_않은_경우() {

        // when
        // 지하철_노선에_지하철역_등록_요청
        ExtractableResponse<Response> response = 지하철_노선에_지하철역_등록_요청(신분당선.getId(), 정자역.getId(), 미금역.getId(), 10);

        // then
        // 지하철_노선에_지하철역_등록됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("노선에 구간을 삭제한다 - 구간이 하나인 노선에서 마지막 구간을 제거할 때")
    @Test
    void deleteSectionException_구간이_하나인_노선에서_마지막_구간을_제거할_때() {

        // 지하철 구간 삭제
        ExtractableResponse<Response> lineResponse = 지하철역_구간_삭제("lines/" + 신분당선.getId() + "/sections?stationId=" + 강남역.getId());

        // then
        // 지하철 삭제할 수 없음
        assertThat(lineResponse.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("노선에 구간을 삭제한다 - 종점이 제거될 경우 다음으로 오던 역이 종점이 됨")
    @Test
    void deleteSection_종점이_제거될_경우_다음으로_오던_역이_종점이_됨() {
        // when
        // 지하철_노선에_지하철역_등록_요청
        지하철_노선에_지하철역_등록_요청(신분당선.getId(), 상행역.getId(), 강남역.getId(), 10);

        // 지하철 구간 삭제
        ExtractableResponse<Response> deleteSection = 지하철역_구간_삭제("lines/" + 신분당선.getId() + "/sections?stationId=" + 상행역.getId());

        // 지하철 구간 삭제 완료
        assertThat(deleteSection.statusCode()).isEqualTo(HttpStatus.OK.value());

        // 지하철 노선 조회
        ExtractableResponse<Response> lineResponse = LineAcceptanceTest.지하철_노선_조회("lines/" + 신분당선.getId());

        // then
        assertThat(lineResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(lineResponse.jsonPath().getList("stations", StationResponse.class).get(0).getName()).isEqualTo(강남역.getName());
    }

    @DisplayName("노선에 구간을 삭제한다.")
    @Test
    void deleteSection() {

        // when
        // 지하철_노선에_지하철역_등록_요청
        ExtractableResponse<Response> registerSectionResponse1 = 지하철_노선에_지하철역_등록_요청(신분당선.getId(), 상행역.getId(), 강남역.getId(), 10);
        ExtractableResponse<Response> registerSectionResponse2 = 지하철_노선에_지하철역_등록_요청(신분당선.getId(), 강남역.getId(), 정자역.getId(), 7);
        ExtractableResponse<Response> registerSectionResponse3 = 지하철_노선에_지하철역_등록_요청(신분당선.getId(), 미금역.getId(), 광교역.getId(), 2);
        ExtractableResponse<Response> registerSectionResponse4 = 지하철_노선에_지하철역_등록_요청(신분당선.getId(), 강남역.getId(), 양재역.getId(), 2);

        // then
        // 지하철_노선에_지하철역_등록됨
        Stream.of(registerSectionResponse1, registerSectionResponse2, registerSectionResponse3, registerSectionResponse4)
                .forEach(i -> 상태코드_확인(i, HttpStatus.CREATED.value()));

        ExtractableResponse<Response> lineResponse = LineAcceptanceTest.지하철_노선_조회("lines/" + 신분당선.getId());
        라인에_역들이_순서대로_나오는지_확인(lineResponse, 상행역.getName(), 강남역.getName(), 양재역.getName(), 정자역.getName(), 미금역.getName(), 광교역.getName());

        // 지하철 구간 삭제
        ExtractableResponse<Response> deleteSection = 지하철역_구간_삭제("lines/" + 신분당선.getId() + "/sections?stationId=" + 강남역.getId());

        // 지하철 구간 삭제 완료
        assertThat(deleteSection.statusCode()).isEqualTo(HttpStatus.OK.value());

        // 삭제된 지하철 노선 조회
        ExtractableResponse<Response> deletedLineResponse = LineAcceptanceTest.지하철_노선_조회("lines/" + 신분당선.getId());

        // then
        assertThat(deletedLineResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
        라인에_역들이_순서대로_나오는지_확인(deletedLineResponse, 상행역.getName(), 양재역.getName(), 정자역.getName(), 미금역.getName(), 광교역.getName());
    }

    public static ExtractableResponse<Response> 지하철역_구간_삭제(String uri) {
        return RestAssured.given().log().all()
                .when()
                .delete(uri)
                .then().log().all()
                .extract();
    }


    public static ExtractableResponse<Response> 지하철_노선에_지하철역_등록_요청(Long lineId, Long upStationId, Long downStationId, Integer distance) {

        Map<String, String> params = new HashMap<>();
        params.put("upStationId", upStationId + "");
        params.put("downStationId", downStationId + "");
        params.put("distance", distance + "");

        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines/" + lineId + "/sections")
                .then().log().all()
                .extract();
    }

    private void 라인에_역들이_순서대로_나오는지_확인(ExtractableResponse<Response> lineResponse, String... 역이름) {
        for (int i = 0; i < 역이름.length; i++) {
            assertThat(lineResponse.jsonPath().getList("stations", StationResponse.class).get(i).getName()).isEqualTo(역이름[i]);
        }
    }

    private void 상태코드_확인(ExtractableResponse<Response> response, int httpStatusValue) {
        assertThat(response.statusCode()).isEqualTo(httpStatusValue);
    }
}
