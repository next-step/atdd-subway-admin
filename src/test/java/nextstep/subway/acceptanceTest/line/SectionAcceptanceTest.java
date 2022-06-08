package nextstep.subway.acceptanceTest.line;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 구간 관련기능 인수 테스트")
public class SectionAcceptanceTest extends LineAcceptance {

    /**
     * When 기존에 존재하는 구간과 같은 구간을 추가하면
     * Then 에러가 발생한다
     */
    @Test
    @DisplayName("같은 구간을 등록하면 에러가 발생한다")
    void equalSection() {
        //when
        ExtractableResponse<Response> response = addSection(_2호선_lineId, 1, 3, 4);
        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * When 등록된 구간과 등록하려는 구간의 열결점이 없으면
     * Then 에러가 발생한다
     */
    @Test
    @DisplayName("등록하려는 구간의 연결점이 없으면 에러가 발생한다")
    void noMatchSection() {
        //when
        ExtractableResponse<Response> response = addSection(_2호선_lineId, 2, 4, 4);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * When 등록된 구간보다 거리가 길거나 같으면
     * Then 에러가 발생한다
     */
    @Test
    @DisplayName("등록된 구간보다 거리가 길거나 같으면 에러가 발생한다")
    void sectionDistanceError() {
        //when
        ExtractableResponse<Response> response = addSection(_2호선_lineId, 1, 2, 11);
        ExtractableResponse<Response> response2 = addSection(_2호선_lineId, 1, 2, 10);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response2.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * Given 지하철 노선을 추가하고
     * When 라인에 없는 역을 삭제하면
     * Then 400 에러가 발생한다
     */
    @Test
    @DisplayName("라인에 없는 역을 삭제하면 400 에러가 발생한다")
    void deleteNotExistedStationException() {
        //given
        addSection(_2호선_lineId, 1, 2, 5);

        //when
        ExtractableResponse<Response> response = deleteStation(_2호선_lineId, 4);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * When 노선이 하나만 있는 라인에서 역을 삭제하면
     * Then 400 에러가 발생한다
     */
    @Test
    @DisplayName("노선이 하나만 있는 경우 역을 삭제하면 400 에러가 발생한다")
    void deleteStationExceptionIfSectionSizeIsZeroOrOne() {
        //when
        ExtractableResponse<Response> response = deleteStation(_2호선_lineId, 2);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }
}
