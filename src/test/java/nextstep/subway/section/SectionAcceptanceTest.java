package nextstep.subway.section;

import static nextstep.subway.section.SectionAcceptanceStep.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.LineAcceptanceStep;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.section.dto.SectionRequest;
import nextstep.subway.station.StationAcceptanceStep;
import nextstep.subway.station.dto.StationRequest;

@DisplayName("지하철 구간 관련 기능")
public class SectionAcceptanceTest extends AcceptanceTest {

    private Long 판교역_ID;
    private Long 수지역_ID;
    private Long 신분당선_ID;

    @BeforeEach
    void setFields() {
        // given
        판교역_ID = StationAcceptanceStep.지하철_역_등록되어_있음(new StationRequest("판교역"));
        수지역_ID = StationAcceptanceStep.지하철_역_등록되어_있음(new StationRequest("수지"));

        LineRequest lineRequest = LineRequest.of("신분당선", "bg-red-600", 판교역_ID, 수지역_ID, 4);
        신분당선_ID = LineAcceptanceStep.지하철_노선_등록되어_있음(lineRequest);
    }

    @DisplayName("노선에 구간을 등록한다.")
    @Test
    void addStation() {
        // given
        Long 정자역_ID = StationAcceptanceStep.지하철_역_등록되어_있음(new StationRequest("정자역"));
        SectionRequest sectionRequest = SectionRequest.of(판교역_ID, 정자역_ID, 1);

        // when
        ExtractableResponse<Response> response = 지하철_노선에_구간_등록_요청(신분당선_ID, sectionRequest);

        // then
        지하철_노선에_구간_등록됨(response);
    }

    @DisplayName("지하철 노선 조회 시, 역 정보가 정렬되어 포함된다.")
    @Test
    void getLineWithStations() {
        // given
        // 지하철_역_등록되어_있음
        // 지하철_노선에_구간_등록_요청

        // when
        // 지하철_노선_조회_요청

        // then
        // 지하철_노선_정보_응답됨
        // 지하철_노선에_역_순서_정렬됨
    }

    @DisplayName("노선에 상행 종점으로 등록한다.")
    @Test
    void addUpStation() {
        // given
        // 지하철_역_등록되어_있음

        // when
        // 지하철_노선에_구간_등록_요청

        // then
        // 지하철_노선에_구간_등록됨
    }

    @DisplayName("노선에 하행 종점으로 등록한다.")
    @Test
    void addDownStation() {
        // given
        // 지하철_역_등록되어_있음

        // when
        // 지하철_노선에_구간_등록_요청

        // then
        // 지하철_노선에_구간_등록됨
    }

    @DisplayName("노선에 기존 역 사이 길이보다 큰 구간을 등록한다.")
    @Test
    void addStationTooLongDistance() {
        // given
        // 지하철_역_등록되어_있음

        // when
        // 지하철_노선에_구간_등록_요청

        // then
        // 지하철_노선에_구간_등록에_실패함
    }

    @DisplayName("노선에 기존 역 사이 길이가 같은 구간을 등록한다.")
    @Test
    void addStationEqualsDistance() {
        // given
        // 지하철_역_등록되어_있음

        // when
        // 지하철_노선에_구간_등록_요청

        // then
        // 지하철_노선에_구간_등록에_실패함
    }

    @DisplayName("노선에 이미 존재하는 구간을 등록한다.")
    @Test
    void addStationExistsStation() {
        // given
        // 지하철_역_등록되어_있음

        // when
        // 지하철_노선에_구간_등록_요청

        // then
        // 지하철_노선에_구간_등록에_실패함
    }

    @DisplayName("노선에 상행/하행 정보가 없는 구간을 등록한다.")
    @Test
    void addStationWithEmptyUpOrDownStation() {
        // given
        // 지하철_역_등록되어_있음

        // when
        // 지하철_노선에_구간_등록_요청

        // then
        // 지하철_노선에_구간_등록에_실패함
    }
}
