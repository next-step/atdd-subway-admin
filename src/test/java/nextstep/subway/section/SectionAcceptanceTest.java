package nextstep.subway.section;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.RestAssuredSetUp;
import nextstep.subway.dto.SectionRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static nextstep.subway.line.LineAcceptanceCommon.지하철_노선_등록;
import static nextstep.subway.section.SectionAcceptanceCommon.지하철_구간_등록;
import static nextstep.subway.station.StationAcceptaneCommon.지하철_역_등록;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 구간 기능")
public class SectionAcceptanceTest extends RestAssuredSetUp {

    @DisplayName("역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없음")
    @Test
    void createSectionWithOverDistance() {
        long lineId = 지하철_노선_등록("2호선", "green", "강남역", "논현역", 10)
                .jsonPath()
                .getLong("id");
        long upStationId = 지하철_역_등록("길동역")
                .jsonPath()
                .getLong("id");
        long downStationId = 지하철_역_등록("잠실역")
                .jsonPath()
                .getLong("id");

        ExtractableResponse<Response> response = 지하철_구간_등록(lineId, new SectionRequest(upStationId, downStationId, 10));

        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());

    }

    @DisplayName("상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없음")
    @Test
    void createSectionWithAlreadyExistsAllStation() {
        long upStationId = 지하철_역_등록("강남역").jsonPath().getLong("id");
        long downStationId = 지하철_역_등록("논현역").jsonPath().getLong("id");
        long lineId = 지하철_노선_등록("2호선", "green", upStationId, downStationId, 10)
                .jsonPath()
                .getLong("id");

        ExtractableResponse<Response> response = 지하철_구간_등록(lineId, new SectionRequest(upStationId, downStationId, 4));

        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());

    }

    @DisplayName("상행역과 하행역이 둘 중 하나도 포함되어있지 않으면 추가할 수 없음")
    @Test
    void createSectionWithNotContainsAnyStation() {
        long upStationId = 지하철_역_등록("서대문역").jsonPath().getLong("id");
        long downStationId = 지하철_역_등록("굽은다리역").jsonPath().getLong("id");
        long lineId = 지하철_노선_등록("2호선", "green", "강남역", "잠실역", 10)
                .jsonPath()
                .getLong("id");

        ExtractableResponse<Response> response = 지하철_구간_등록(lineId, new SectionRequest(upStationId, downStationId, 4));

        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());

    }

}
