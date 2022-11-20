package nextstep.subway.acceptance.section;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.acceptance.RestAssuredSetUp;
import nextstep.subway.dto.SectionRequest;
import nextstep.subway.dto.StationResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.stream.Collectors;

import static nextstep.subway.acceptance.line.LineAcceptanceCommon.지하철_노선_등록;
import static nextstep.subway.acceptance.line.LineAcceptanceCommon.지하철_노선_조회;
import static nextstep.subway.acceptance.section.SectionAcceptanceCommon.지하철_구간_등록;
import static nextstep.subway.acceptance.section.SectionAcceptanceCommon.지하철_구간_삭제;
import static nextstep.subway.acceptance.station.StationAcceptaneCommon.지하철_역_등록;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("지하철 구간 기능")
public class SectionAcceptanceTest extends RestAssuredSetUp {

    @DisplayName("새로운 역을 상행 종점으로 등록할 경우 등록성공")
    @Test
    void createSectionWithNewUpStation() {
        //given
        long jamSilStationId = 지하철_역_등록("잠실역").jsonPath().getLong("id");
        long hongDaeStationId = 지하철_역_등록("홍대입구역").jsonPath().getLong("id");
        long lineId = 지하철_노선_등록("2호선", "green", jamSilStationId, hongDaeStationId, 10)
                .jsonPath()
                .getLong("id");
        long gangnamStationId = 지하철_역_등록("강남역")
                .jsonPath()
                .getLong("id");

        //when
        ExtractableResponse<Response> response = 지하철_구간_등록(lineId, new SectionRequest(gangnamStationId, jamSilStationId, 11));

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("새로운 역을 하행 종점으로 등록할 경우 등록성공")
    @Test
    void createSectionWithNewDownStation() {
        //given
        long jamSilStationId = 지하철_역_등록("잠실역").jsonPath().getLong("id");
        long hongDaeStationId = 지하철_역_등록("홍대입구역").jsonPath().getLong("id");
        long lineId = 지하철_노선_등록("2호선", "green", jamSilStationId, hongDaeStationId, 10)
                .jsonPath()
                .getLong("id");
        long gangnamStationId = 지하철_역_등록("강남역")
                .jsonPath()
                .getLong("id");

        //when
        ExtractableResponse<Response> response = 지하철_구간_등록(lineId, new SectionRequest(hongDaeStationId, gangnamStationId, 2));

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("역 사이에 새로운 역을 등록할 기존 역 사이 길이보다 작으면 등록성공")
    @Test
    void createSectionBetweenSections() {
        //given
        long jamSilStationId = 지하철_역_등록("잠실역").jsonPath().getLong("id");
        long hongDaeStationId = 지하철_역_등록("홍대입구역").jsonPath().getLong("id");
        long lineId = 지하철_노선_등록("2호선", "green", jamSilStationId, hongDaeStationId, 10)
                .jsonPath()
                .getLong("id");
        long gangnamStationId = 지하철_역_등록("강남역")
                .jsonPath()
                .getLong("id");

        //when
        ExtractableResponse<Response> response = 지하철_구간_등록(lineId, new SectionRequest(jamSilStationId, gangnamStationId, 2));

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없음")
    @Test
    void createSectionWithOverDistance() {
        //given
        long jamSilStationId = 지하철_역_등록("잠실역").jsonPath().getLong("id");
        long hongDaeStationId = 지하철_역_등록("홍대입구역").jsonPath().getLong("id");
        long lineId = 지하철_노선_등록("2호선", "green", jamSilStationId, hongDaeStationId, 10)
                .jsonPath()
                .getLong("id");
        long gangnamStationId = 지하철_역_등록("강남역")
                .jsonPath()
                .getLong("id");

        //when
        ExtractableResponse<Response> response = 지하철_구간_등록(lineId, new SectionRequest(jamSilStationId, gangnamStationId, 10));

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());

    }

    @DisplayName("상행역과 하행역이 한 구간에 모두 포함되어 있으면 추가할 수 없음")
    @Test
    void createSectionWithAlreadyExistsSection() {
        //given
        long upStationId = 지하철_역_등록("강남역").jsonPath().getLong("id");
        long downStationId = 지하철_역_등록("논현역").jsonPath().getLong("id");
        long lineId = 지하철_노선_등록("2호선", "green", upStationId, downStationId, 10)
                .jsonPath()
                .getLong("id");

        //when
        ExtractableResponse<Response> response = 지하철_구간_등록(lineId, new SectionRequest(upStationId, downStationId, 4));

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());

    }

    @DisplayName("상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없음")
    @Test
    void createSectionWithAlreadyExistsAllStation() {
        //given
        long jamSilStationId = 지하철_역_등록("잠실역").jsonPath().getLong("id");
        long hongDaeStationId = 지하철_역_등록("홍대입구역").jsonPath().getLong("id");
        long lineId = 지하철_노선_등록("2호선", "green", jamSilStationId, hongDaeStationId, 10)
                .jsonPath().getLong("id");
        long bangBaeStationId = 지하철_역_등록("방배역").jsonPath().getLong("id");
        지하철_구간_등록(lineId, new SectionRequest(hongDaeStationId, bangBaeStationId, 4));

        //when
        ExtractableResponse<Response> response = 지하철_구간_등록(lineId, new SectionRequest(jamSilStationId, bangBaeStationId, 2));

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());

    }

    @DisplayName("상행역과 하행역이 둘 중 하나도 포함되어있지 않으면 추가할 수 없음")
    @Test
    void createSectionWithNotContainsAnyStation() {
        //given
        long upStationId = 지하철_역_등록("서대문역").jsonPath().getLong("id");
        long downStationId = 지하철_역_등록("굽은다리역").jsonPath().getLong("id");
        long lineId = 지하철_노선_등록("2호선", "green", "강남역", "잠실역", 10)
                .jsonPath()
                .getLong("id");

        //when
        ExtractableResponse<Response> response = 지하철_구간_등록(lineId, new SectionRequest(upStationId, downStationId, 4));

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());

    }


    @DisplayName("노선에 등록되어있지 않은 역을 삭제할 수 없음")
    @Test
    void removeSectionWithNoneExistsStation() {
        //given
        long samsungStationId = 지하철_역_등록("삼성역").jsonPath().getLong("id");
        long jamSilStationId = 지하철_역_등록("잠실역").jsonPath().getLong("id");
        long hongDaeStationId = 지하철_역_등록("홍대입구역").jsonPath().getLong("id");
        long lineId = 지하철_노선_등록("2호선", "green", jamSilStationId, hongDaeStationId, 10)
                .jsonPath()
                .getLong("id");
        long gangnamStationId = 지하철_역_등록("강남역")
                .jsonPath()
                .getLong("id");
        지하철_구간_등록(lineId, new SectionRequest(hongDaeStationId, gangnamStationId, 2));

        //when
        ExtractableResponse<Response> response = 지하철_구간_삭제(lineId, samsungStationId);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("구간이 하나인 노선에서 마지막 구간을 제거할 수 없음")
    @Test
    void removeSectionWithLastSectionInLine() {
        //given
        long jamSilStationId = 지하철_역_등록("잠실역").jsonPath().getLong("id");
        long hongDaeStationId = 지하철_역_등록("홍대입구역").jsonPath().getLong("id");
        long lineId = 지하철_노선_등록("2호선", "green", jamSilStationId, hongDaeStationId, 10)
                .jsonPath()
                .getLong("id");

        //when
        ExtractableResponse<Response> response = 지하철_구간_삭제(lineId, jamSilStationId);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("종점이 제거 될 경우 다음으로 오던 역이 종점이 됨")
    @Test
    void removeSectionWithEdgeStation() {
        //given
        long bangbaeStationId = 지하철_역_등록("방배역").jsonPath().getLong("id");
        long jamSilStationId = 지하철_역_등록("잠실역").jsonPath().getLong("id");
        long hongDaeStationId = 지하철_역_등록("홍대입구역").jsonPath().getLong("id");
        long lineId = 지하철_노선_등록("2호선", "green", jamSilStationId, hongDaeStationId, 10)
                .jsonPath()
                .getLong("id");
        지하철_구간_등록(lineId, new SectionRequest(jamSilStationId, bangbaeStationId, 4));

        //when
        지하철_구간_삭제(lineId, jamSilStationId);

        //then
        ExtractableResponse<Response> response = 지하철_노선_조회(lineId);
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(response.jsonPath()
                        .getList("stations", StationResponse.class).stream().map(StationResponse::getName)
                        .collect(Collectors.toList())).containsAnyOf("방배역", "홍대입구역"));
    }

    @DisplayName("중간역이 제거될 경우 재배치를 함")
    @Test
    void removeSectionWithRearrage() {
        //given
        long bangbaeStationId = 지하철_역_등록("방배역").jsonPath().getLong("id");
        long jamSilStationId = 지하철_역_등록("잠실역").jsonPath().getLong("id");
        long hongDaeStationId = 지하철_역_등록("홍대입구역").jsonPath().getLong("id");
        long lineId = 지하철_노선_등록("2호선", "green", jamSilStationId, hongDaeStationId, 10)
                .jsonPath()
                .getLong("id");
        지하철_구간_등록(lineId, new SectionRequest(hongDaeStationId, bangbaeStationId, 5));

        //when
        지하철_구간_삭제(lineId, hongDaeStationId);

        //then
        ExtractableResponse<Response> response = 지하철_노선_조회(lineId);
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(response.jsonPath()
                        .getList("stations", StationResponse.class).stream().map(StationResponse::getName)
                        .collect(Collectors.toList())).containsAnyOf("방배역", "잠실역"));
    }
}
