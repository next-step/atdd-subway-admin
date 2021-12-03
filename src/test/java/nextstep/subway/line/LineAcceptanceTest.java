package nextstep.subway.line;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.StationAcceptanceTest;
import nextstep.subway.station.dto.StationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {
    private StationResponse 강남역;
    private StationResponse 역삼역;
    private StationResponse 신사역;
    private StationResponse 삼성역;

    @BeforeEach
    void setup() {
        강남역 = StationAcceptanceTest.지하철역을_생성한다("강남역");
        역삼역 = StationAcceptanceTest.지하철역을_생성한다("역삼역");
        신사역 = StationAcceptanceTest.지하철역을_생성한다("신사역");
        삼성역 = StationAcceptanceTest.지하철역을_생성한다("삼성역");
    }

    @DisplayName("지하철 노선을 구역과 함께 생성할 수 있다.")
    @Test
    void createLineWithSection() {
        // when
        // 지하철_노선_생성_요청
        ExtractableResponse<Response> response =
                LineAcceptanceFixture.노선_생성을_요청한다(역삼역, 강남역, 9, "2호선", "green");

        // then
        // 지하철_노선_생성됨
        같은_응답인지_확인한다(response, HttpStatus.CREATED);
    }

    @DisplayName("지하철 노선을 구역 없이 생성할 수 없다.")
    @Test
    void createLineWithoutSectionException() {
        // when
        // 지하철_노선_생성_요청
        ExtractableResponse<Response> response = LineAcceptanceFixture.노선_생성을_요청한다("2호선", "green");

        // then
        // 지하철_노선_생성됨
        같은_응답인지_확인한다(response, HttpStatus.BAD_REQUEST);
    }


    @DisplayName("지하철 노선 중복으로 생성할 수 없다.")
    @Test
    void createLine2() {
        // given
        // 지하철_노선_등록되어_있음
        LineAcceptanceFixture.노선_생성을_요청한다(역삼역, 강남역, 9, "2호선", "green");

        // when
        // 지하철_노선_생성_요청
        ExtractableResponse<Response> response =
                LineAcceptanceFixture.노선_생성을_요청한다(신사역, 삼성역, 9, "2호선", "green");

        // then
        // 지하철_노선_생성_실패됨
        같은_응답인지_확인한다(response, HttpStatus.BAD_REQUEST);
    }

    @DisplayName("지하철 노선 목록을 구간과 함께 조회한다.")
    @Test
    void getLinesWithSection() {
        // given
        // 지하철_노선_등록되어_있음
        ExtractableResponse<Response> createdResponse1 =
                LineAcceptanceFixture.노선_생성을_요청한다(역삼역, 강남역, 9, "2호선", "green");
        // 지하철_노선_등록되어_있음
        ExtractableResponse<Response> createdResponse2 =
                LineAcceptanceFixture.노선_생성을_요청한다(신사역, 삼성역, 6, "3호선", "orange");

        // when
        // 지하철_노선_목록_조회_요청
        ExtractableResponse<Response> response = LineAcceptanceFixture.노선목록_조회를_요청한다();

        // then
        // 지하철_노선_목록_응답됨
        같은_응답인지_확인한다(response, HttpStatus.OK);
        // 지하철_노선_목록_포함됨
        지하철_노선_목록인지_확인한다(LineAcceptanceFixture.ofLineResponses(createdResponse1, createdResponse2), response);

    }

    @DisplayName("지하철 노선을 구간과 함께 조회한다.")
    @Test
    void getLineWithSection() {
        // given
        // 지하철_노선_등록되어_있음
        LineResponse 생성된_노선 = LineAcceptanceTest.노선을_생성한다(역삼역, 강남역, 9, "2호선", "green");

        // when
        // 지하철_노선_조회_요청
        ExtractableResponse<Response> 조회된_노선 = LineAcceptanceFixture.노선을_조회를_요청한다(생성된_노선.getId());

        // then
        // 지하철_노선_응답됨
        같은_응답인지_확인한다(조회된_노선, HttpStatus.OK);
        // 지하철_노선_동일_확인됨
        지하철_노선_동일_확인한다(생성된_노선, 조회된_노선);
    }


    @DisplayName("지하철 노선을 구간과 함께 제거할 수 있다.")
    @Test
    void deleteLineWithSection() {
        // given
        // 지하철_노선_등록되어_있음
        LineResponse createdLineResponse = LineAcceptanceTest.노선을_생성한다(역삼역, 강남역, 9, "2호선", "green");

        // when
        // 지하철_노선_제거_요청
        ExtractableResponse<Response> response = LineAcceptanceFixture.노선_삭제를_요청한다(createdLineResponse.getId());

        // then
        // 지하철_노선_삭제됨
        같은_응답인지_확인한다(response, HttpStatus.NO_CONTENT);
        // 지하철_노선_삭제_확인됨
        ExtractableResponse<Response> getResponse = LineAcceptanceFixture.노선을_조회를_요청한다(createdLineResponse.getId());
        같은_응답인지_확인한다(getResponse, HttpStatus.BAD_REQUEST);
    }

    public static LineResponse 노선을_생성한다(StationResponse upStation, StationResponse downStation, int distance, String lineName, String lineColor) {
        return LineAcceptanceFixture.ofLineResponse(
                LineAcceptanceFixture.노선_생성을_요청한다(upStation, downStation, distance, lineName, lineColor)
        );
    }

    private void 지하철_노선_동일_확인한다(LineResponse createdLineResponse, ExtractableResponse<Response> response) {
        assertThat(createdLineResponse).isEqualTo(LineAcceptanceFixture.ofLineResponse(response));
    }

    private void 지하철_노선_목록인지_확인한다(List<LineResponse> expectedLines, ExtractableResponse<Response> response) {
        List<LineResponse> lines = LineAcceptanceFixture.ofLineResponses(response);
        assertThat(lines).containsAll(expectedLines);
    }

    private void 같은_응답인지_확인한다(ExtractableResponse<Response> response, HttpStatus status) {
        assertThat(response.statusCode()).isEqualTo(status.value());
    }
}
