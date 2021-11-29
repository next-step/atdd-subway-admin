package nextstep.subway.section;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.section.dto.SectionRequest;
import nextstep.subway.station.TestStationFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static nextstep.subway.line.TestLineFactory.노선_생성;
import static nextstep.subway.utils.TestRequestFactory.요청;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("지하철 구간 관련 기능")
public class SectionAcceptanceTest extends AcceptanceTest {
    Long DEFAULT_UP_STATION_ID = 1L;
    Long DEFAULT_DOWN_STATION_ID = 2L;
    Long DEFAULT_LINE_ID = 1L;
    Long newStationId;
    int DISTANCE = 3;

    @BeforeEach
    void setup() {
        노선_생성("강남역", "삼성역", "2호선", "green", 10);
        newStationId = TestStationFactory.역_생성("역삼역").getId();
    }

    @DisplayName("상행역 사이에 새로운 역 등록")
    @Test
    void addUpStationSection() {
        // when
        ExtractableResponse<Response> response = 지하철_구간_추가_요청(DEFAULT_UP_STATION_ID, newStationId, DISTANCE);

        // then
        지하철_구간_생성됨(response, Arrays.asList(DEFAULT_UP_STATION_ID, newStationId, DEFAULT_DOWN_STATION_ID));
    }

    @DisplayName("하행역 사이에 새로운 역 등록")
    @Test
    void addDownStationSection() {
        // when
        ExtractableResponse<Response> response = 지하철_구간_추가_요청(newStationId, DEFAULT_DOWN_STATION_ID, DISTANCE);

        // then
        지하철_구간_생성됨(response, Arrays.asList(DEFAULT_UP_STATION_ID, newStationId, DEFAULT_DOWN_STATION_ID));
    }

    @DisplayName("새로운 역을 상행 종점으로 등록할 경우")
    @Test
    void addNewUpStationSection() {
        // when
        ExtractableResponse<Response> response = 지하철_구간_추가_요청(newStationId, DEFAULT_UP_STATION_ID, DISTANCE);

        // then
        지하철_구간_생성됨(response, Arrays.asList(newStationId, DEFAULT_UP_STATION_ID, DEFAULT_DOWN_STATION_ID));
    }

    private ExtractableResponse<Response> 지하철_구간_추가_요청(Long upStationId, Long downStationId, int distance) {
        SectionRequest sectionRequest = new SectionRequest(upStationId, downStationId, distance);
        String path = "lines/" + DEFAULT_LINE_ID + "/sections";
        return 요청(HttpMethod.POST, path, sectionRequest);
    }

    private void 지하철_구간_생성됨(ExtractableResponse<Response> response, List<Long> requestedStationIds) {
        List<Long> savedStationIds = 요청(HttpMethod.GET, "lines/" + DEFAULT_LINE_ID, null)
                .body().as(LineResponse.class)
                .getStations()
                .stream()
                .map(stationResponse -> stationResponse.getId())
                .collect(Collectors.toList());

        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value()),
                () -> 저장된_역_비교(savedStationIds, requestedStationIds)
        );
    }

    private void 저장된_역_비교(List<Long> savedStationIds, List<Long> requestedStationIds) {
        for (int i = 0; i < savedStationIds.size(); i++) {
            assertThat(savedStationIds.get(i)).isEqualTo(requestedStationIds.get(i));
        }
    }
}
