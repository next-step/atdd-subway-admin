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

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static nextstep.subway.line.LineAcceptanceTestRequest.지하철_노선_생성_요청;
import static nextstep.subway.line.LineAcceptanceTestRequest.지하철_노선_조회;
import static nextstep.subway.line.SectionAcceptanceTestRequest.지하철_노선_구간_등록_요청;
import static nextstep.subway.line.SectionAcceptanceTestRequest.지하철_노선에_지하철역_제외_요청;
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

    @DisplayName("`Line`에 `Section` 추가 예외 - 역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이와 같으면 등록을 할 수 없음")
    @Test
    void exceptionToAddSectionWithSameDistance() {
        // When
        StationResponse 삼성역 = 지하철역_생성_요청("삼성역");
        ExtractableResponse<Response> response = 지하철_노선_구간_등록_요청(_2호선.getId(), 강남역.getId(), 삼성역.getId(), 100);
        // Then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("`Line`에 `Section` 추가 예외 - 역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크면 등록을 할 수 없음")
    @Test
    void exceptionToAddSectionGreaterThanExistingDistance() {
        // When
        StationResponse 삼성역 = 지하철역_생성_요청("삼성역");
        ExtractableResponse<Response> response = 지하철_노선_구간_등록_요청(_2호선.getId(), 강남역.getId(), 삼성역.getId(), 200);
        // Then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("`Line`에 `Section` 추가 예외 - 상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없음")
    @Test
    void exceptionToAddSection2() {
        // When
        ExtractableResponse<Response> response = 지하철_노선_구간_등록_요청(_2호선.getId(), 강남역.getId(), 잠실역.getId(), 100);
        // Then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("`Line`에 `Section` 추가 예외 - 상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없음")
    @Test
    void exceptionToAddSection3() {
        // When
        StationResponse 건대입구역 = 지하철역_생성_요청("건대입구역");
        StationResponse 구의역 = 지하철역_생성_요청("구의역");
        ExtractableResponse<Response> response = 지하철_노선_구간_등록_요청(_2호선.getId(), 건대입구역.getId(), 구의역.getId(), 100);
        // Then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("`Line`에 등록된 `Station` 삭제")
    @Test
    void removeSectionInLine() {
        // Given
        StationResponse 삼성역 = 지하철역_생성_요청("삼성역");
        지하철_노선_구간_등록_요청(_2호선.getId(), 삼성역.getId(), 잠실역.getId(), 50);
        // When
        ExtractableResponse<Response> response = 지하철_노선에_지하철역_제외_요청(_2호선.getId(), 잠실역.getId());
        // Then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
        지하철_노선의_지하철역_동일_확인(LineAcceptanceTestRequest.지하철_노선_조회(_2호선.getId()), Arrays.asList(강남역, 삼성역));
    }

    @DisplayName("`Line`에 등록된 `Station`이 2개일때 `Station` 삭제")
    @Test
    void exceptionToRemoveSectionInLine() {
        // When
        ExtractableResponse<Response> response = 지하철_노선에_지하철역_제외_요청(_2호선.getId(), 강남역.getId());
        // Then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    private static void 지하철_노선의_지하철역_동일_확인(LineResponse line, List<StationResponse> stations) {
        List<Long> stationIds = line.getStations().stream()
                .map(StationResponse::getId)
                .collect(Collectors.toList());
        List<Long> expectedStationIds = stations.stream()
                .map(StationResponse::getId)
                .collect(Collectors.toList());
        assertThat(stationIds).containsExactlyElementsOf(expectedStationIds);
    }
}
