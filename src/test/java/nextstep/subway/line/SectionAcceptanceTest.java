package nextstep.subway.line;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineResponse;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static nextstep.subway.utils.LineRestAssuredUtils.지하철_노선_생성_요청;
import static nextstep.subway.utils.LineRestAssuredUtils.지하철_노선_조회_요청;
import static nextstep.subway.utils.SectionRestAssuredUtils.구간_등록_요청;
import static nextstep.subway.utils.SectionRestAssuredUtils.구간_삭제_요청;
import static nextstep.subway.utils.StationRestAssuredUtils.지하철_역_생성_요청;

@DisplayName("노선 구간 관련 기능")
public class SectionAcceptanceTest extends AcceptanceTest {

    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();
        지하철_역_생성_요청("사당역");
        지하철_역_생성_요청("잠실역");
    }

    @Test
    @DisplayName("구간을 등록한다 (새로운 역을 역 사이에 하행으로 등록)")
    void addTerminalDownStationSection() {
        LineResponse lineResponse = 지하철_노선_생성_요청("2호선", "green", "1", "2", "10").as(LineResponse.class);

        지하철_역_생성_요청("강남역");
        ExtractableResponse<Response> response = 구간_등록_요청(lineResponse.getId(), "1", "3", "3");

        Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    @Test
    @DisplayName("구간을 등록한다 (새로운 역을 역 사이에 상행으로 등록)")
    void addTerminalUpStationSection() {
        LineResponse lineResponse = 지하철_노선_생성_요청("2호선", "green", "1", "2", "10").as(LineResponse.class);

        지하철_역_생성_요청("삼성역");
        ExtractableResponse<Response> response = 구간_등록_요청(lineResponse.getId(), "3", "2", "3");

        Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    @Test
    @DisplayName("구간을 등록한다 (새로운 역을 상행 종점으로 등록)")
    void addMiddleDownStationSection() {
        LineResponse lineResponse = 지하철_노선_생성_요청("2호선", "green", "1", "2", "10").as(LineResponse.class);

        지하철_역_생성_요청("서울대입구역");
        ExtractableResponse<Response> response = 구간_등록_요청(lineResponse.getId(), "3", "1", "3");

        Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    @Test
    @DisplayName("구간을 등록한다 (새로운 역을 하행 종점으로 등록)")
    void addMiddleUpStationSection() {
        LineResponse lineResponse = 지하철_노선_생성_요청("2호선", "green", "1", "2", "10").as(LineResponse.class);

        지하철_역_생성_요청("강변역");
        ExtractableResponse<Response> response = 구간_등록_요청(lineResponse.getId(), "2", "3", "3");

        Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    @Test
    @DisplayName("구간 등록시, 상행과 하행이 동일한 경우 400 익셉션 발생")
    void addDuplicationStationsSection() {
        // when
        LineResponse lineResponse = 지하철_노선_생성_요청("2호선", "green", "1", "2", "10").as(LineResponse.class);

        지하철_역_생성_요청("교대역");
        ExtractableResponse<Response> response = 구간_등록_요청(lineResponse.getId(), "3", "3", "10");

        // then
        Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("구간 등록시, distance가 기존보다 크거나 같은 경우 400 익셉션 발생")
    void addOverLengthDistanceSection() {
        // when
        LineResponse lineResponse = 지하철_노선_생성_요청("2호선", "green", "1", "2", "10").as(LineResponse.class);

        지하철_역_생성_요청("강변");
        ExtractableResponse<Response> response = 구간_등록_요청(lineResponse.getId(), "1", "3", "10");

        // then
        Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("구간 등록시, 이미 존재하는 구간이면 400 익셉션 발생")
    void addDuplicationSection() {
        LineResponse lineResponse = 지하철_노선_생성_요청("2호선", "green", "1", "2", "10").as(LineResponse.class);

        ExtractableResponse<Response> response = 구간_등록_요청(lineResponse.getId(), "1", "2", "10");

        Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("구간을 삭제한다 (서울대입구-사당역-잠실역)에서 사당역을 삭제하는 테스트")
    void deleteSection() {
        LineResponse lineResponse = 지하철_노선_생성_요청("2호선", "green", "1", "2", "10").as(LineResponse.class);
        지하철_역_생성_요청("서울대입구역");
        구간_등록_요청(lineResponse.getId(), "3", "1", "3");

        ExtractableResponse<Response> response = 구간_삭제_요청(lineResponse.getId(), "1");
        Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

        지하철_노선_조회_요청(lineResponse.getId());
    }

    @Test
    @DisplayName("구간이 1개인 경우 삭제하면 404 익셉션 발생 ((사당역-잠실역)에서 사당역을 삭제하는 테스트)")
    void deleteSectionNotFoundSectionException() {
        LineResponse lineResponse = 지하철_노선_생성_요청("2호선", "green", "1", "2", "10").as(LineResponse.class);

        ExtractableResponse<Response> response = 구간_삭제_요청(lineResponse.getId(), "2");
        Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @Test
    @DisplayName("구간에 등록되어 있지 않은 역을 삭제할 때 404 익셉션 발생")
    void deleteSectionNotFoundStationException() {
        LineResponse lineResponse = 지하철_노선_생성_요청("2호선", "green", "1", "2", "10").as(LineResponse.class);

        ExtractableResponse<Response> response = 구간_삭제_요청(lineResponse.getId(), "3");
        Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }


}
