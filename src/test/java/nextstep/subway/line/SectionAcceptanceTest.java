package nextstep.subway.line;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.dto.StationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static nextstep.subway.line.LineAcceptanceTestRequest.지하철_노선_생성_요청;
import static nextstep.subway.line.LineAcceptanceTestRequest.지하철_노선_조회;
import static nextstep.subway.line.SectionAcceptanceTestRequest.지하철_노선_구간_등록_요청;
import static nextstep.subway.station.StationAcceptanceTestRequest.지하철역_생성_요청;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("지하철 구간 관련 기능")
class SectionAcceptanceTest extends AcceptanceTest {

    private StationResponse 강남역;
    private StationResponse 잠실역;
    private LineResponse _2호선;

    @BeforeEach
    void beforeEach() {
        강남역 = 지하철역_생성_요청("강남역");
        잠실역 = 지하철역_생성_요청("잠실역");
        _2호선 = 지하철_노선_생성_요청("2호선", "bg-red-600", 강남역.getId(), 잠실역.getId(), 100);
    }

    @DisplayName("`Line`에 상행 `Station`이 같은 `Section` 추가")
    @Test
    void addSectionWithSameUpStation() {
        // When
        StationResponse 삼성역 = 지하철역_생성_요청("삼성역");
        ExtractableResponse<Response> response = 지하철_노선_구간_등록_요청(_2호선.getId(), 강남역.getId(), 삼성역.getId(), 20);
        // Then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(지하철_노선_조회(_2호선.getId()).getStations())
                        .extracting(StationResponse::getName)
                        .containsExactly("강남역", "삼성역", "잠실역")
        );
    }

    @DisplayName("`Line`에 하행 `Station`이 같은 `Section` 추가")
    @Test
    void addSectionWithSameDownStation() {
        // When
        StationResponse 삼성역 = 지하철역_생성_요청("삼성역");
        ExtractableResponse<Response> response = 지하철_노선_구간_등록_요청(_2호선.getId(), 삼성역.getId(), 잠실역.getId(), 20);
        // Then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(지하철_노선_조회(_2호선.getId()).getStations())
                        .extracting(StationResponse::getName)
                        .containsExactly("강남역", "삼성역", "잠실역")
        );
    }

    @DisplayName("`Line`에 새로운 상행 `Station`의 `Section` 추가")
    @Test
    void addSectionWithNewUpStation() {
        // When
        StationResponse 서초역 = 지하철역_생성_요청("서초역");
        ExtractableResponse<Response> response = 지하철_노선_구간_등록_요청(_2호선.getId(), 서초역.getId(), 강남역.getId(), 20);
        // Then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(지하철_노선_조회(_2호선.getId()).getStations())
                        .extracting(StationResponse::getName)
                        .containsExactly("서초역", "강남역", "잠실역")
        );
    }

    @DisplayName("`Line`에 새로운 하행 `Station`의 `Section` 추가")
    @Test
    void addSectionWithNewDownStation() {
        // When
        StationResponse 강변역 = 지하철역_생성_요청("강변역");
        ExtractableResponse<Response> response = 지하철_노선_구간_등록_요청(_2호선.getId(), 잠실역.getId(), 강변역.getId(), 20);
        // Then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(지하철_노선_조회(_2호선.getId()).getStations())
                        .extracting(StationResponse::getName)
                        .containsExactly("강남역", "잠실역", "강변역")
        );
    }
}
