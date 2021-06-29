package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineCreateRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.LineStationCreateRequest;
import nextstep.subway.line.dto.LineStationResponse;
import nextstep.subway.station.dto.StationResponse;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import static nextstep.subway.line.LineAcceptanceTest.지하철_노선_등록되어_있음;
import static nextstep.subway.line.LineAcceptanceTest.지하철_노선_조회_요청;
import static nextstep.subway.line.LineStationAddAcceptanceTest.지하철_노선에_지하철역_등록_요청;
import static nextstep.subway.station.StationAcceptanceTest.지하철역_등록;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 구간 관련 기능")
public class LineStationDeleteAcceptanceTest extends AcceptanceTest {
    private StationResponse 강남역;
    private StationResponse 당산역;
    private StationResponse 서울대입구역;
    private StationResponse 역삼역;
    private StationResponse 신촌역;
    private LineResponse 이호선;
    private LineCreateRequest 노선첫구간요청;
    private LineStationCreateRequest 중간노선추가요청;

    @BeforeEach
    public void setUp() {
        super.setUp();
        강남역 = 지하철역_등록("강남역").as(StationResponse.class);
        역삼역 = 지하철역_등록("역삼역").as(StationResponse.class);
        당산역 = 지하철역_등록("당산역").as(StationResponse.class);
        서울대입구역 = 지하철역_등록("서울대입구역").as(StationResponse.class);
        신촌역 = 지하철역_등록("신촌역").as(StationResponse.class);


        노선첫구간요청 = new LineCreateRequest("2호선", "bg-green-600", 당산역.getId(), 강남역.getId(), 10);
        이호선 = 지하철_노선_등록되어_있음(노선첫구간요청).as(LineResponse.class);
        중간노선추가요청 = new LineStationCreateRequest(서울대입구역.getId(), 강남역.getId(), 5);
        지하철_노선에_지하철역_등록_요청(이호선.getId(), 중간노선추가요청);
    }

    @DisplayName("노선의 구간을 제거한다 - 상행 종점이 제거되면 다음 역이 종점이 된다")
    @Test
    public void deleteSectionFromLine_firstStation__success() {
        //when
        ExtractableResponse<Response> response = 지하철_노선에_지하철역_삭제_요청(이호선.getId(), 당산역.getId());

        //then
        지하철_노선에_지하철역_삭제됨(response);
        ExtractableResponse<Response> lineResponse = 지하철_노선_조회_요청(이호선.getId());
        int expectedTotalDistance = 중간노선추가요청.getDistance();
        종점이제거된경우_노선의총거리는_삭제된구간을삭제한것과같다(expectedTotalDistance, lineResponse);
    }

    @DisplayName("노선의 구간을 제거한다 - 하행 종점이 제거되면 다음 역이 종점이 된다")
    @Test
    public void deleteSectionFromLine_lastStation__success() {
        //when
        ExtractableResponse<Response> response = 지하철_노선에_지하철역_삭제_요청(이호선.getId(), 강남역.getId());

        //then
        지하철_노선에_지하철역_삭제됨(response);
        ExtractableResponse<Response> lineResponse = 지하철_노선_조회_요청(이호선.getId());
        int expectedTotalDistance = 노선첫구간요청.getDistance() - (노선첫구간요청.getDistance() - 중간노선추가요청.getDistance());
        종점이제거된경우_노선의총거리는_삭제된구간을삭제한것과같다(expectedTotalDistance, lineResponse);
    }


    @DisplayName("노선의 구간을 제거한다 - 중간역이 제거될 경우 재배치된다")
    @Test
    public void deleteSectionFromLine_middleStation__success() {
        //when
        ExtractableResponse<Response> response = 지하철_노선에_지하철역_삭제_요청(이호선.getId(), 서울대입구역.getId());

        //then
        지하철_노선에_지하철역_삭제됨(response);
        ExtractableResponse<Response> lineResponse = 지하철_노선_조회_요청(이호선.getId());
        중간역이제거된경우_노선의총거리는같다(노선첫구간요청.getDistance(), lineResponse);
    }

    @DisplayName("노선의 구간을 제거한다 - 노선에 없는 역은 제거할 수 없다")
    @Test
    public void deleteSectionFromLine_Station__fail__NoneExist() {
        //when
        ExtractableResponse<Response> response = 지하철_노선에_지하철역_삭제_요청(이호선.getId(), 역삼역.getId());

        //then
        지하철_노선에_지하철역_식제안됨(response);
    }

    @DisplayName("노선의 구간을 제거한다 - 노선의 마지막 남은 구간은 삭제될 수 없다")
    @Test
    public void deleteSectionFromLine_middleStation__fail__LastSection() {
        //when
        지하철_노선에_지하철역_삭제_요청(이호선.getId(), 서울대입구역.getId());
        ExtractableResponse<Response> response = 지하철_노선에_지하철역_삭제_요청(이호선.getId(), 당산역.getId());

        //then
        지하철_노선에_지하철역_식제안됨(response);
    }


    public static ExtractableResponse<Response> 지하철_노선에_지하철역_삭제_요청(Long lineId, Long lineStationId) {
        return RestAssured
                .given().log().all()
                .pathParam("id", lineId)
                .param("stationId", lineStationId)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().delete("/lines/{id}/sections")
                .then().log().all().extract();
    }

    private void 지하철_노선에_지하철역_삭제됨(ExtractableResponse<Response> response) {
        Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    private void 지하철_노선에_지하철역_식제안됨(ExtractableResponse<Response> response) {
        Assertions.assertThat(response.statusCode()).isNotEqualTo(HttpStatus.OK.value());
    }

    private void 중간역이제거된경우_노선의총거리는같다(int expectedTotalDistance, ExtractableResponse<Response> response) {
        assertThat(response.body().jsonPath().getList("lineStations")).isNotEmpty();
        assertThat(response.body().jsonPath().getList("lineStations", LineStationResponse.class).stream()
                .mapToLong(LineStationResponse::getDistance)
                .sum()).isEqualTo(expectedTotalDistance);

    }

    private void 종점이제거된경우_노선의총거리는_삭제된구간을삭제한것과같다(int expectedTotalDistance, ExtractableResponse<Response> response) {
        assertThat(response.body().jsonPath().getList("lineStations")).isNotEmpty();
        assertThat(response.body().jsonPath().getList("lineStations", LineStationResponse.class).stream()
                .mapToLong(LineStationResponse::getDistance)
                .sum()).isEqualTo(expectedTotalDistance);

    }
}
