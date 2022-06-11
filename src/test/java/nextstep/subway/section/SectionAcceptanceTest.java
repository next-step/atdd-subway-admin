package nextstep.subway.section;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.base.BaseUnitTest;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.dto.SectionRequest;
import nextstep.subway.dto.StationResponse;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import static nextstep.subway.Line.LineAcceptanceTest.지하철_노선_생성_신분당선;
import static nextstep.subway.station.StationAcceptanceTest.createStation;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DisplayName("구간 관련 기능")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SectionAcceptanceTest extends BaseUnitTest {

    private StationResponse 강남역;
    private StationResponse 광교역;
    private StationResponse 판교역;
    private LineResponse 신분당선;

    @BeforeEach
    void given() {
        //given
        //지하철역 등록
        강남역 = createStation("강남역").as(StationResponse.class);
        광교역 = createStation("광교역").as(StationResponse.class);
        판교역 = createStation("판교역").as(StationResponse.class);

        //given
        //노선 등록
        신분당선 = 지하철_노선_생성_신분당선(강남역, 광교역, 10L).as(LineResponse.class);
    }

    @DisplayName("역과 역 사이에 새로운 구간을 등록한다.")
    @Test
    void 구간_등록_테스트() {
        // when
        // 지하철_노선에_지하철역_등록_요청
        SectionRequest sectionRequest = SectionRequest.of(강남역.getId(), 판교역.getId(), 1L);
        ExtractableResponse<Response> createResponse = 지하철_구간_등록(신분당선.getId(), sectionRequest);

        // then
        // 지하철_노선에_지하철역_등록됨
        assertThat(createResponse.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        ExtractableResponse<Response> response = 지하철_노선_조회(신분당선.getId());
        LineResponse lineResponse = response.as(LineResponse.class);
        assertThat(lineResponse.getSections().size()).isEqualTo(2);
    }

    @DisplayName("상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없다.")
    @Test
    void 상행역_하행역_노선_등록() {
        // when
        // 동일한 구간 등록
        구간_등록_테스트();
        SectionRequest sectionRequest = SectionRequest.of(강남역.getId(), 판교역.getId(), 1L);
        ExtractableResponse<Response> response = 지하철_구간_등록(신분당선.getId(), sectionRequest);

        // then
        // 에러 확인
        Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    public static ExtractableResponse<Response> 지하철_구간_등록(Long id, SectionRequest request) {
        return RestAssured.given().log().all()
                .body(request)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines/" + id + "/sections")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 지하철_노선_조회(Long id) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/lines/" + id)
                .then().log().all()
                .extract();
    }
}
