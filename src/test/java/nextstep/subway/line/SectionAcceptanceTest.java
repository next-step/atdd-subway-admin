package nextstep.subway.line;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.station.dto.StationRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static nextstep.subway.line.step.LineAcceptanceStep.새로운_지하철_노선_생성_요청;
import static nextstep.subway.line.step.LineAcceptanceStep.응답_헤더에서_ID_추출;
import static nextstep.subway.line.step.SectionAcceptanceStep.새로운_지하철_하행종점역_구간_추가_요청;
import static nextstep.subway.line.step.SectionAcceptanceStep.새로운_지하철_구간_추가_요청;
import static nextstep.subway.station.step.StationAcceptanceStep.지하철역_생성됨;
import static org.assertj.core.api.Assertions.assertThat;

public class SectionAcceptanceTest extends AcceptanceTest {
    @DisplayName("시나리오1: 종점 사이에 새로운 지하철 구간 등록")
    @Test
    void createNewSectionBetweenTwoStations() {
        String station1Name = "잠실";
        String station2Name = "종합운동장";
        String station3Name = "삼성";
        String lineName = "2호선";
        String lineColor = "초록색";
        Long createdDistance = 20L;
        Long requestDistance = 10L;
        // given
        // 등록된 구간이 있음
        ExtractableResponse<Response> station1Response = 지하철역_생성됨(new StationRequest(station1Name));
        Long station1Id = 응답_헤더에서_ID_추출(station1Response);

        ExtractableResponse<Response> station2Response = 지하철역_생성됨(new StationRequest(station2Name));
        Long station2Id = 응답_헤더에서_ID_추출(station2Response);

        ExtractableResponse<Response> lineResponse = 새로운_지하철_노선_생성_요청(
                new LineRequest(lineName, lineColor, station1Id, station2Id, createdDistance)
        );
        Long lineId = 응답_헤더에서_ID_추출(lineResponse);

        // and 새로 등록할 구간의 역이 등록되어 있음
        ExtractableResponse<Response> station3Response = 지하철역_생성됨(new StationRequest(station3Name));
        Long station3Id = 응답_헤더에서_ID_추출(station3Response);

        // when
        // 사용자가 새로운 지하철 구간 등록 요청한다.
        ExtractableResponse<Response> sectionCreatedResponse = 새로운_지하철_구간_추가_요청(
                station1Id, station3Id, requestDistance, lineId
        );

        // then
        // 지하철 구간 등록 성공
        assertThat(sectionCreatedResponse.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    @DisplayName("시나리오2: 상행 종점역 교체")
    @Test
    void changeEndUpStation() {
        String originalEndUpStationName = "잠실";
        String originalEndDownStationName = "종합운동장";
        String newEndUpStationName = "삼성";
        String lineName = "2호선";
        String lineColor = "초록색";
        Long createdDistance = 20L;
        Long requestDistance = 10L;
        // given
        // 등록된 구간이 있음
        ExtractableResponse<Response> originalEndUpResponse = 지하철역_생성됨(
                new StationRequest(originalEndUpStationName));
        Long originalEndUpId = 응답_헤더에서_ID_추출(originalEndUpResponse);

        ExtractableResponse<Response> originalEndDownResponse = 지하철역_생성됨(
                new StationRequest(originalEndDownStationName));
        Long originalEndDownId = 응답_헤더에서_ID_추출(originalEndDownResponse);

        ExtractableResponse<Response> lineResponse = 새로운_지하철_노선_생성_요청(
                new LineRequest(lineName, lineColor, originalEndUpId, originalEndDownId, createdDistance)
        );
        Long lineId = 응답_헤더에서_ID_추출(lineResponse);

        // and 새로 등록할 구간의 역이 등록되어 있음
        ExtractableResponse<Response> newEndUpStationResponse = 지하철역_생성됨(
                new StationRequest(newEndUpStationName));
        Long newEndUpStationId = 응답_헤더에서_ID_추출(newEndUpStationResponse);

        // when
        // 사용자가 새로운 지하철 구간 등록 요청한다.
        ExtractableResponse<Response> sectionCreatedResponse = 새로운_지하철_구간_추가_요청(
                newEndUpStationId, originalEndUpId, requestDistance, lineId
        );

        // then
        // 지하철 구간 등록 성공
        assertThat(sectionCreatedResponse.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    @DisplayName("시나리오3: 하행 종점역 교체")
    @Test
    void changeEndDownStation() {
        String originalEndUpStationName = "잠실";
        String originalEndDownStationName = "종합운동장";
        String newEndDownStationName = "삼성";
        String lineName = "2호선";
        String lineColor = "초록색";
        Long createdDistance = 20L;
        Long requestDistance = 10L;
        // given
        // 등록된 구간이 있음
        ExtractableResponse<Response> originalEndUpResponse = 지하철역_생성됨(
                new StationRequest(originalEndUpStationName));
        Long originalEndUpId = 응답_헤더에서_ID_추출(originalEndUpResponse);

        ExtractableResponse<Response> originalEndDownResponse = 지하철역_생성됨(
                new StationRequest(originalEndDownStationName));
        Long originalEndDownId = 응답_헤더에서_ID_추출(originalEndDownResponse);

        ExtractableResponse<Response> lineResponse = 새로운_지하철_노선_생성_요청(
                new LineRequest(lineName, lineColor, originalEndUpId, originalEndDownId, createdDistance)
        );
        Long lineId = 응답_헤더에서_ID_추출(lineResponse);

        // and 새로 등록할 구간의 역이 등록되어 있음
        ExtractableResponse<Response> newEndDownStationResponse = 지하철역_생성됨(new StationRequest(newEndDownStationName));
        Long newEndDownStationId = 응답_헤더에서_ID_추출(newEndDownStationResponse);

        // when
        // 사용자가 새로운 지하철 구간 등록 요청한다.
        ExtractableResponse<Response> sectionCreatedResponse = 새로운_지하철_구간_추가_요청(
                originalEndDownId, newEndDownStationId, requestDistance, lineId);

        // then
        // 지하철 구간 등록 성공
        assertThat(sectionCreatedResponse.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    @DisplayName("시나리오4: 지나치게 긴 거리의 새로운 지하철 구간을 추가 시도")
    @Test
    void tooLongDistanceNewSectionTest() {
        String originalEndUpStationName = "잠실";
        String originalEndDownStationName = "종합운동장";
        String newUpStationName = "삼성";
        String lineName = "2호선";
        String lineColor = "초록색";
        Long createdDistance = 20L;
        Long requestDistance = createdDistance + 10;
        // given
        // 등록된 구간이 있음
        ExtractableResponse<Response> originalEndUpResponse = 지하철역_생성됨(
                new StationRequest(originalEndUpStationName));
        Long originalEndUpId = 응답_헤더에서_ID_추출(originalEndUpResponse);

        ExtractableResponse<Response> originalEndDownResponse = 지하철역_생성됨(
                new StationRequest(originalEndDownStationName));
        Long originalEndDownId = 응답_헤더에서_ID_추출(originalEndDownResponse);

        ExtractableResponse<Response> lineResponse = 새로운_지하철_노선_생성_요청(
                new LineRequest(lineName, lineColor, originalEndUpId, originalEndDownId, createdDistance)
        );
        Long lineId = 응답_헤더에서_ID_추출(lineResponse);

        // and 새로 등록할 구간의 역이 등록되어 있음
        ExtractableResponse<Response> newUpStationResponse = 지하철역_생성됨(new StationRequest(newUpStationName));
        Long newUpStationId = 응답_헤더에서_ID_추출(newUpStationResponse);

        // when
        // 사용자가 지나치게 긴 거리의 구간 추가를 요청한다.
        ExtractableResponse<Response> sectionCreatedResponse = 새로운_지하철_구간_추가_요청(
                newUpStationId, originalEndDownId, requestDistance, lineId);

        assertThat(sectionCreatedResponse.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("시나리오5-1: 새로 추가하려는 구간의 역이 모두 이미 기존 노선에 존재하는 상태에서 새로운 구간 추가 시도(연속된 역)")
    @Test
    void addSectionFailWhenAlreadyAllStationsInTest() {
        String station1Name = "잠실";
        String station2Name = "종합운동장";
        String station3Name = "삼성";
        String lineName = "2호선";
        String lineColor = "초록색";
        Long section1Distance = 20L;
        Long section2Distance = 20L;
        // given
        // 등록된 구간이 있음
        ExtractableResponse<Response> station1Response = 지하철역_생성됨(new StationRequest(station1Name));
        Long station1Id = 응답_헤더에서_ID_추출(station1Response);

        ExtractableResponse<Response> station2Response = 지하철역_생성됨(new StationRequest(station2Name));
        Long station2Id = 응답_헤더에서_ID_추출(station2Response);

        ExtractableResponse<Response> lineResponse = 새로운_지하철_노선_생성_요청(
                new LineRequest(lineName, lineColor, station1Id, station2Id, section1Distance));
        Long lineId = 응답_헤더에서_ID_추출(lineResponse);

        // and 라인에 구간 추가됨
        ExtractableResponse<Response> station3Response = 지하철역_생성됨(new StationRequest(station3Name));
        Long station3Id = 응답_헤더에서_ID_추출(station3Response);
        새로운_지하철_하행종점역_구간_추가_요청(station2Id, station3Id, section2Distance, lineId);

        // when
        // 이미 노선에 존재하는 역들로만 구성된 Section 추가 요청
        ExtractableResponse<Response> response = 새로운_지하철_구간_추가_요청(
                station1Id, station2Id, 10L, lineId);

        // then
        // 새로운 지하철 노선 구간 등록 실패
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("시나리오5-2: 새로 추가하려는 구간의 역이 모두 이미 기존 노선에 존재하는 상태에서 새로운 구간 추가 시도(건너뛴 역)")
    @Test
    void addSectionFailWhenAlreadyAllStationsIn2Test() {
        String station1Name = "잠실";
        String station2Name = "종합운동장";
        String station3Name = "삼성";
        String lineName = "2호선";
        String lineColor = "초록색";
        Long section1Distance = 20L;
        Long section2Distance = 20L;
        // given
        // 등록된 구간이 있음
        ExtractableResponse<Response> station1Response = 지하철역_생성됨(new StationRequest(station1Name));
        Long station1Id = 응답_헤더에서_ID_추출(station1Response);

        ExtractableResponse<Response> station2Response = 지하철역_생성됨(new StationRequest(station2Name));
        Long station2Id = 응답_헤더에서_ID_추출(station2Response);

        ExtractableResponse<Response> lineResponse = 새로운_지하철_노선_생성_요청(
                new LineRequest(lineName, lineColor, station1Id, station2Id, section1Distance));
        Long lineId = 응답_헤더에서_ID_추출(lineResponse);

        // and 라인에 구간 추가됨
        ExtractableResponse<Response> station3Response = 지하철역_생성됨(new StationRequest(station3Name));
        Long station3Id = 응답_헤더에서_ID_추출(station3Response);
        새로운_지하철_하행종점역_구간_추가_요청(station2Id, station3Id, section2Distance, lineId);

        // when
        // 이미 노선에 존재하는 역들로만 구성된 Section 추가 요청
        ExtractableResponse<Response> response = 새로운_지하철_구간_추가_요청(
                station1Id, station3Id, 10L, lineId);

        // then
        // 새로운 지하철 노선 구간 등록 실패
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("시나리오6: 상행역과 하행역 둘 중 하나도 포함되지 않는 지하철 노선 구간 등록")
    @Test
    void addSectionWithAllNotIncludedStation() {
        String station1Name = "잠실";
        String station2Name = "종합운동장";
        String station3Name = "삼성";
        String station4Name = "신도림";
        String lineName = "2호선";
        String lineColor = "초록색";
        Long distance = 10L;
        // given
        // 등록된 구간이 있음
        ExtractableResponse<Response> station1Response = 지하철역_생성됨(new StationRequest(station1Name));
        Long station1Id = 응답_헤더에서_ID_추출(station1Response);

        ExtractableResponse<Response> station2Response = 지하철역_생성됨(new StationRequest(station2Name));
        Long station2Id = 응답_헤더에서_ID_추출(station2Response);

        ExtractableResponse<Response> lineResponse = 새로운_지하철_노선_생성_요청(
                new LineRequest(lineName, lineColor, station1Id, station2Id, distance));
        Long lineId = 응답_헤더에서_ID_추출(lineResponse);

        // and 등록할 구간 역들 등록되어 있음
        ExtractableResponse<Response> station3Response = 지하철역_생성됨(new StationRequest(station3Name));
        Long station3Id = 응답_헤더에서_ID_추출(station3Response);
        ExtractableResponse<Response> station4Response = 지하철역_생성됨(new StationRequest(station4Name));
        Long station4Id = 응답_헤더에서_ID_추출(station4Response);

        // when
        // 신규 구간 등록 요청
        ExtractableResponse<Response> response = 새로운_지하철_구간_추가_요청(
                station3Id, station4Id, distance - 5, lineId);

        // then
        // 구간 등록 실패
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }
}
