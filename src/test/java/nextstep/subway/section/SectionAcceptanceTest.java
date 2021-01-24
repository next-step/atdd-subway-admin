package nextstep.subway.section;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.LineRequestTestModule;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.section.dto.SectionRequest;
import nextstep.subway.station.StationAcceptanceTest;
import nextstep.subway.station.dto.StationRequest;
import nextstep.subway.station.dto.StationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 구간 관련 기능")
public class SectionAcceptanceTest extends AcceptanceTest {

    private StationResponse 소요산역;
    private StationResponse 인천역;
    private StationResponse 서울역;
    private LineResponse 일호선;

    @BeforeEach
    void beforeEach() {
        소요산역 = StationAcceptanceTest.지하철역_생성_요청(new StationRequest("소요산역")).as(StationResponse.class);
        인천역 = StationAcceptanceTest.지하철역_생성_요청(new StationRequest("인천역")).as(StationResponse.class);
        서울역 = StationAcceptanceTest.지하철역_생성_요청(new StationRequest("서울역")).as(StationResponse.class);

        일호선 = LineRequestTestModule.지하철_노선_생성_요청(LineRequest.builder()
                                    .name("1호선")
                                    .color("파란색")
                                    .upStationId(소요산역.getId())
                                    .downStationId(인천역.getId())
                                    .distance(10).build()).as(LineResponse.class);
    }

    @DisplayName("역과 역사이에 새로운 역을 등록할 경우 : 기존 상행역 - 새로운 하행역 관계")
    @Test
    void addSection() {
        // given
        SectionRequest sectionRequest = new SectionRequest(소요산역.getId(), 서울역.getId(), 5);

        // when
        ExtractableResponse<Response> response = 지하철_구간_등록_요청(sectionRequest);

        // then
        노선에_구간_등록_확인(response, HttpStatus.OK);
        노선에_포함된_지하철_확인(response,"소요산역", "서울역", "인천역");
    }

    @DisplayName("역과 역사이에 새로운 역을 등록할 경우 : 기존 하행역 - 새로운 상행역 관계")
    @Test
    void addSection2() {
        // given
        SectionRequest sectionRequest = new SectionRequest(서울역.getId(), 인천역.getId(), 5);

        // when
        ExtractableResponse<Response> response = 지하철_구간_등록_요청(sectionRequest);

        // then
        노선에_구간_등록_확인(response, HttpStatus.OK);
        노선에_포함된_지하철_확인(response,"소요산역", "서울역", "인천역");
    }

    @DisplayName("새로운 역을 상행 종점으로 등록할 경우")
    @Test
    void addSection3() {
        // given
        SectionRequest sectionRequest = new SectionRequest(서울역.getId(), 소요산역.getId(), 5);

        // when
        ExtractableResponse<Response> response = 지하철_구간_등록_요청(sectionRequest);

        // then
        노선에_구간_등록_확인(response, HttpStatus.OK);
        노선에_포함된_지하철_확인(response,"서울역", "소요산역", "인천역");
    }

    @DisplayName("새로운 역을 하행 종점으로 등록할 경우")
    @Test
    void addSection4() {
        // given
        SectionRequest sectionRequest = new SectionRequest(인천역.getId(), 서울역.getId(), 5);

        // when
        ExtractableResponse<Response> response = 지하철_구간_등록_요청(sectionRequest);

        // then
        노선에_구간_등록_확인(response, HttpStatus.OK);
        노선에_포함된_지하철_확인(response,"소요산역", "인천역", "서울역");
    }

    @DisplayName("상행역 아래에 새로운 역을 등록할 경우 기존 길이보다 같거나 크면 등록이 안됨")
    @Test
    void addInvalidDistanceUpStation() {
        // given
        SectionRequest sectionRequest = new SectionRequest(소요산역.getId(), 서울역.getId(), 10);

        // when
        ExtractableResponse<Response> response = 지하철_구간_등록_요청(sectionRequest);

        // then
        노선에_구간_등록_확인(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @DisplayName("하행역 위에 새로운 역을 등록할 경우 기존 길이보다 같거나 크면 등록이 안됨")
    @Test
    void addInvalidDistanceDownStation() {
        // given
        SectionRequest sectionRequest = new SectionRequest(서울역.getId(), 인천역.getId(), 10);

        // when
        ExtractableResponse<Response> response = 지하철_구간_등록_요청(sectionRequest);

        // then
        노선에_구간_등록_확인(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @DisplayName("상행역과 하행역이 이미 노선에 모두 등록 되었다면 추가 안됨")
    @Test
    void alreadyExistStations() {
        // given
        SectionRequest sectionRequest = new SectionRequest(소요산역.getId(), 인천역.getId(), 5);

        // when
        ExtractableResponse<Response> response = 지하철_구간_등록_요청(sectionRequest);

        // then
        노선에_구간_등록_확인(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @DisplayName("상하행역 중 하나라도 포함되어있지 않을 경우")
    @Test
    void notExistStations() {
        // given
        StationResponse 창동역 = StationAcceptanceTest.지하철역_생성_요청(new StationRequest("창동역")).as(StationResponse.class);
        StationResponse 용산역 = StationAcceptanceTest.지하철역_생성_요청(new StationRequest("용산역")).as(StationResponse.class);
        SectionRequest sectionRequest = new SectionRequest(창동역.getId(), 용산역.getId(), 5);

        // when
        ExtractableResponse<Response> response = 지하철_구간_등록_요청(sectionRequest);

        // then
        노선에_구간_등록_확인(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ExtractableResponse<Response> 지하철_구간_등록_요청(SectionRequest sectionRequest) {
        return RestAssured
                .given().log().all()
                .body(sectionRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines/{id}/sections", 일호선.getId())
                .then().log().all().extract();
    }

    private void 노선에_포함된_지하철_확인(ExtractableResponse<Response> response, String...expectedStations) {
        List<String> stations = response.jsonPath().getList("stations", StationResponse.class)
                .stream().map(StationResponse::getName).collect(toList());
        assertThat(stations).containsExactly(expectedStations);
    }

    private void 노선에_구간_등록_확인(ExtractableResponse<Response> response, HttpStatus httpStatus) {
        assertThat(response.statusCode()).isEqualTo(httpStatus.value());
    }
}
