package nextstep.subway.section;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.LineMap;
import nextstep.subway.line.LineTestHelper;
import nextstep.subway.station.StationTestHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("지하철 구간 등록 관련 인수 테스트")
public class SectionAcceptanceTest extends AcceptanceTest {

    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();
        StationTestHelper.지하철_역_생성_요청("건대역");
        StationTestHelper.지하철_역_생성_요청("용마산역");
        Map<String, String> params = LineMap.of("bg-red-600", "신분당선", "1", "2", "10");
        LineTestHelper.지하철_노선_생성_요청(params);
        StationTestHelper.지하철_역_생성_요청("중곡역");
    }

    @DisplayName("역 사이에 새로운 역을 등록한다.")
    @Test
    void addSection() {
        //given
        Map<String, String> params = SectionTestHelper.구간_생성_요청_파라미터("1", "3", "4");

        // when
        ExtractableResponse<Response> response = SectionTestHelper.구간_등록_요청(params);

        // then
        지하철_노선에_지하철역_등록됨(response);
    }

    @DisplayName("새로운 역을 상행 종점으로 등록한다.")
    @Test
    void addAscendingStation() {
        //given
        Map<String, String> params = SectionTestHelper.구간_생성_요청_파라미터("3", "1", "4");

        // when
        ExtractableResponse<Response> response = SectionTestHelper.구간_등록_요청(params);

        // then
        지하철_노선에_지하철역_등록됨(response);
    }

    @DisplayName("새로운 역을 하행 종점으로 등록할 경우")
    @Test
    void addDescendingStation() {
        //given
        Map<String, String> params = SectionTestHelper.구간_생성_요청_파라미터("2", "3", "4");

        // when
        ExtractableResponse<Response> response = SectionTestHelper.구간_등록_요청(params);

        // then
        지하철_노선에_지하철역_등록됨(response);
    }

    @DisplayName("기존 역 사이 길이보다 크거나 같으면 등록 할 수 없음")
    @Test
    void distanceErrorValid() {
        // given
        Map<String, String> params = SectionTestHelper.구간_생성_요청_파라미터("1", "3", "20");

        // when
        ExtractableResponse<Response> response = SectionTestHelper.구간_등록_요청(params);

        // then
        지하철_노선에_지하철역_등록되지않음(response);
    }

    @DisplayName("이미 등록되어 있어서 등록 할 수 없음")
    @Test
    void alreadyAddedValid() {
        // given
        Map<String, String> params = SectionTestHelper.구간_생성_요청_파라미터("1", "2", "5");

        // when
        ExtractableResponse<Response> response = SectionTestHelper.구간_등록_요청(params);

        // then
        지하철_노선에_지하철역_등록되지않음(response);
    }

    @DisplayName("상행역과 하행역 둘 중 하나도 포함되어 있지 않을때 ")
    @Test
    void validNotAdded() {
        // given
        Map<String, String> params = SectionTestHelper.구간_생성_요청_파라미터("4", "5", "5");

        // when
        ExtractableResponse<Response> response = SectionTestHelper.구간_등록_요청(params);

        // then
        지하철_노선에_지하철역_등록되지않음(response);
    }

    @DisplayName("구간 제거 기능")
    @Test
    void deleteSection() {
        // given
        SectionTestHelper.구간_등록_요청(SectionTestHelper.구간_생성_요청_파라미터("1", "3", "4"));
        Map<String, String> params = SectionTestHelper.구간_제거_요청_파라미터("3");

        // when
        ExtractableResponse<Response> response = SectionTestHelper.구간_제거_요청(params);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("구간이 하나 일땐 제거 안됨")
    @Test
    void validSize() {
        Map<String, String> params = SectionTestHelper.구간_제거_요청_파라미터("1");

        // when
        ExtractableResponse<Response> response = SectionTestHelper.구간_제거_요청(params);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    private void 지하철_노선에_지하철역_등록됨(ExtractableResponse<Response> response) {
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value()),
                () -> assertThat(response.header("Location")).isNotBlank()
        );
    }

    private void 지하철_노선에_지하철역_등록되지않음(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }
}
