package nextstep.subway.section;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.section.dto.SectionRequest;
import nextstep.subway.station.dto.StationResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.stream.Collectors;

import static nextstep.subway.line.LineTestMethods.createLineResponse;
import static nextstep.subway.line.LineTestMethods.노선_단건_조회;
import static nextstep.subway.section.SectionTestMethods.구간_삭제;
import static nextstep.subway.section.SectionTestMethods.구간_추가;
import static nextstep.subway.station.StationTestMethods.createStationResponse;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("구간 관련 기능")
public class SectionAcceptanceTest extends AcceptanceTest {
    private static final int DEFAULT_DISTANCE = 10;

    /**
     * Given 기본노선(상행역-하행역)을 생성하고
     * When [새로운역-하행역]구간을 추가하고 노선을 조회하면
     * Then 상행역, 새로운역, 하행역이 조회된다
     */
    @DisplayName("역 사이에 새로운 역을 등록한다.")
    @Test
    void createSectionBetweenStations() {
        //given
        StationResponse 상행역 = createStationResponse("상행역");
        StationResponse 하행역 = createStationResponse("하행역");
        LineResponse 기본노선 = createLineResponse(
                "1호선", "blue", 상행역.getId(), 하행역.getId(), DEFAULT_DISTANCE
        );

        //when
        StationResponse 새로운역 = createStationResponse("새로운역");
        구간_추가(기본노선.getId(), 새로운역.getId(), 하행역.getId(), 5);
        LineResponse 기본노선_조회결과 = 노선_단건_조회(기본노선.getId()).as(LineResponse.class);

        //then
        List<String> stations = 기본노선_조회결과.getStations().stream()
                .map(StationResponse::getName).collect(Collectors.toList());
        assertThat(stations).containsExactly("상행역", "새로운역", "하행역");
    }

    /**
     * Given 기본노선(상행역-하행역)을 등록하고
     * When [새로운역-상행역]구간을 추가하고 노선을 조회하면
     * Then 새로운역, 상행역, 하행역이 조회된다
     */
    @DisplayName("새로운 역을 가장 앞에 등록한다.")
    @Test
    void createSectionBeforeFirstStation() {
        //given
        StationResponse 상행역 = createStationResponse("상행역");
        StationResponse 하행역 = createStationResponse("하행역");
        LineResponse 기본노선 = createLineResponse(
                "1호선", "blue", 상행역.getId(), 하행역.getId(), DEFAULT_DISTANCE
        );

        //when
        StationResponse 새로운역 = createStationResponse("새로운역");
        구간_추가(기본노선.getId(), 새로운역.getId(), 상행역.getId(), 5);
        LineResponse 기본노선_조회결과 = 노선_단건_조회(기본노선.getId()).as(LineResponse.class);

        //then
        List<String> stations = 기본노선_조회결과.getStations().stream()
                .map(StationResponse::getName).collect(Collectors.toList());
        assertThat(stations).containsExactly("새로운역", "상행역", "하행역");
    }

    /**
     * Given 기본노선(상행역-하행역)을 등록하고
     * When [하행역-새로운역]구간을 추가하고 노선을 조회하면
     * Then 상행역, 하행역, 새로운역이 조회된다
     */
    @DisplayName("새로운 역을 가장 뒤에 등록한다.")
    @Test
    void createSectionAfterLastStation() {
        //given
        StationResponse 상행역 = createStationResponse("상행역");
        StationResponse 하행역 = createStationResponse("하행역");
        LineResponse 기본노선 = createLineResponse(
                "1호선", "blue", 상행역.getId(), 하행역.getId(), DEFAULT_DISTANCE
        );

        //when
        StationResponse 새로운역 = createStationResponse("새로운역");
        구간_추가(기본노선.getId(), 하행역.getId(), 새로운역.getId(), 5);
        LineResponse 기본노선_조회결과 = 노선_단건_조회(기본노선.getId()).as(LineResponse.class);

        //then
        List<String> stations = 기본노선_조회결과.getStations().stream()
                .map(StationResponse::getName).collect(Collectors.toList());
        assertThat(stations).containsExactly("상행역", "하행역", "새로운역");
    }

    /**
     * Given 기본노선(상행역-하행역)을 등록하고
     * When [새로운역-하행역] 구간을 기존 길이보다 길거나 같게 등록하면
     * Then 등록이되지 않는다.
     */
    @DisplayName("역 사이에 새로운 역을 등록할 경우, 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없다.")
    @Test
    void exception_createLongerSectionBetweenStations() {
        //given
        StationResponse 상행역 = createStationResponse("상행역");
        StationResponse 하행역 = createStationResponse("하행역");
        LineResponse 기본노선 = createLineResponse(
                "1호선", "blue", 상행역.getId(), 하행역.getId(), DEFAULT_DISTANCE
        );

        //when
        StationResponse 새로운역 = createStationResponse("새로운역");
        ExtractableResponse<Response> response = 구간_추가(
                기본노선.getId(), 새로운역.getId(), 하행역.getId(), DEFAULT_DISTANCE
        );

        //Then
        LineResponse 기본노선_조회결과 = 노선_단건_조회(기본노선.getId()).as(LineResponse.class);
        List<String> stations = 기본노선_조회결과.getStations().stream()
                .map(StationResponse::getName).collect(Collectors.toList());
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value()),
                () -> assertThat(stations).containsExactly("상행역", "하행역")
        );
    }

    /**
     * Given 기본노선(상행역-하행역)을 등록하고
     * When [상행역-하행역)] 구간을 추가하면
     * Then 등록이되지 않는다.
     */
    @DisplayName("상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없다.")
    @Test
    void exception_createSectionWithAlreadyExistingStations() {
        //given
        StationResponse 상행역 = createStationResponse("상행역");
        StationResponse 하행역 = createStationResponse("하행역");
        LineResponse 기본노선 = createLineResponse(
                "1호선", "blue", 상행역.getId(), 하행역.getId(), DEFAULT_DISTANCE
        );

        //when
        ExtractableResponse<Response> response = 구간_추가(기본노선.getId(), 상행역.getId(), 하행역.getId(), 5);

        //Then
        LineResponse 기본노선_조회결과 = 노선_단건_조회(기본노선.getId()).as(LineResponse.class);
        List<String> stations = 기본노선_조회결과.getStations().stream()
                .map(StationResponse::getName).collect(Collectors.toList());
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value()),
                () -> assertThat(stations).containsExactly("상행역", "하행역")
        );
    }

    /**
     * Given 기본노선(상행역-하행역)을 등록하고
     * When [새로운역, 새로운역2] 구간을 추가하면
     * Then 예외가 발생한다
     */
    @DisplayName("상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없다.")
    @Test
    void exception_createSectionWithUnknownStations() {
        //given
        StationResponse 상행역 = createStationResponse("상행역");
        StationResponse 하행역 = createStationResponse("하행역");
        LineResponse 기본노선 = createLineResponse(
                "1호선", "blue", 상행역.getId(), 하행역.getId(), DEFAULT_DISTANCE
        );

        //when
        StationResponse 새로운역 = createStationResponse("새로운역");
        StationResponse 새로운역2 = createStationResponse("새로운역2");
        ExtractableResponse<Response> response = 구간_추가(기본노선.getId(), 새로운역.getId(), 새로운역2.getId(), 5);

        //Then
        LineResponse 기본노선_조회결과 = 노선_단건_조회(기본노선.getId()).as(LineResponse.class);
        List<String> stations = 기본노선_조회결과.getStations().stream()
                .map(StationResponse::getName).collect(Collectors.toList());
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value()),
                () -> assertThat(stations).containsExactly("상행역", "하행역")
        );
    }

    /**
     * Given 기본노선(상행역-중간역-하행역)을 생성하고
     * When [상행역]구간을 제거하고 노선을 조회하면
     * Then 중간역,하행역이 조회된다
     */
    @DisplayName("종점이 제거될 경우 다음으로 오던 역이 종점이 된다. (상행역 제거)")
    @Test
    void deleteFirstSection(){
        //given
        StationResponse 상행역 = createStationResponse("상행역");
        StationResponse 중간역 = createStationResponse("중간역");
        StationResponse 하행역 = createStationResponse("하행역");
        LineResponse 기본노선 = createLineResponse(
                "1호선", "blue", 상행역.getId(), 하행역.getId(), DEFAULT_DISTANCE
        );
        구간_추가(기본노선.getId(), 상행역.getId(), 중간역.getId(), 5);

        //when
        ExtractableResponse<Response> response = 구간_삭제(기본노선.getId(), 상행역.getId());
        LineResponse 기본노선_조회결과 = 노선_단건_조회(기본노선.getId()).as(LineResponse.class);

        //then
        List<String> stations = 기본노선_조회결과.getStations().stream()
                .map(StationResponse::getName).collect(Collectors.toList());
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(stations).containsExactly("중간역", "하행역")
        );
    }

    /**
     * Given 기본노선(상행역-중간역-하행역)을 생성하고
     * When [하행역]구간을 제거하고 노선을 조회하면
     * Then 상행역, 중간역이 조회된다
     */
    @DisplayName("종점이 제거될 경우 이전 역이 종점이 된다. (하행역 제거)")
    @Test
    void deleteLastSection(){
        //given
        StationResponse 상행역 = createStationResponse("상행역");
        StationResponse 중간역 = createStationResponse("중간역");
        StationResponse 하행역 = createStationResponse("하행역");
        LineResponse 기본노선 = createLineResponse(
                "1호선", "blue", 상행역.getId(), 하행역.getId(), DEFAULT_DISTANCE
        );
        구간_추가(기본노선.getId(), 상행역.getId(), 중간역.getId(), 5);

        //when
        ExtractableResponse<Response> response = 구간_삭제(기본노선.getId(), 하행역.getId());
        LineResponse 기본노선_조회결과 = 노선_단건_조회(기본노선.getId()).as(LineResponse.class);

        //then
        List<String> stations = 기본노선_조회결과.getStations().stream()
                .map(StationResponse::getName).collect(Collectors.toList());
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(stations).containsExactly("상행역", "중간역")
        );
    }

    /**
     * Given 기본노선(상행역-중간역-하행역)을 생성하고
     * When [중간역]구간을 제거하고 노선을 조회하면
     * Then 상행역,하행역이 조회된다
     */
    @DisplayName("중간역이 제거될 경우 재배치를 한다.")
    @Test
    void deleteMiddleSection(){
        //given
        StationResponse 상행역 = createStationResponse("상행역");
        StationResponse 중간역 = createStationResponse("중간역");
        StationResponse 하행역 = createStationResponse("하행역");
        LineResponse 기본노선 = createLineResponse(
                "1호선", "blue", 상행역.getId(), 하행역.getId(), DEFAULT_DISTANCE
        );
        구간_추가(기본노선.getId(), 상행역.getId(), 중간역.getId(), 5);

        //when
        ExtractableResponse<Response> response = 구간_삭제(기본노선.getId(), 중간역.getId());
        LineResponse 기본노선_조회결과 = 노선_단건_조회(기본노선.getId()).as(LineResponse.class);

        //then
        List<String> stations = 기본노선_조회결과.getStations().stream()
                .map(StationResponse::getName).collect(Collectors.toList());
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(stations).containsExactly("상행역", "하행역")
        );
    }

    /**
     * Given 기본노선(상행역-중간역-하행역)을 생성하고
     * When [새로운역]구간을 제거하면
     * Then 예외가 발생한다
     */
    @DisplayName("구간이 하나인 노선에서 마지막 구간을 제거할 수 없다.")
    @Test
    void exception_deleteUnknownSection(){
        //given
        StationResponse 상행역 = createStationResponse("상행역");
        StationResponse 중간역 = createStationResponse("중간역");
        StationResponse 하행역 = createStationResponse("하행역");
        LineResponse 기본노선 = createLineResponse(
                "1호선", "blue", 상행역.getId(), 하행역.getId(), DEFAULT_DISTANCE
        );
        구간_추가(기본노선.getId(), 상행역.getId(), 중간역.getId(), 5);

        //when
        StationResponse 새로운역 = createStationResponse("새로운역");
        ExtractableResponse<Response> response = 구간_삭제(기본노선.getId(), 새로운역.getId());
        LineResponse 기본노선_조회결과 = 노선_단건_조회(기본노선.getId()).as(LineResponse.class);

        //then
        List<String> stations = 기본노선_조회결과.getStations().stream()
                .map(StationResponse::getName).collect(Collectors.toList());
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value()),
                () -> assertThat(stations).containsExactly("상행역", "중간역", "하행역")
        );
    }

    /**
     * Given 기본노선(상행역-하행역)을 생성하고
     * When [상행역]구간을 제거하면
     * Then 예외가 발생한다
     */
    @DisplayName("구간이 하나인 노선에서 마지막 구간을 제거할 수 없다.")
    @Test
    void exception_deleteOnlyOneSection(){
        //given
        StationResponse 상행역 = createStationResponse("상행역");
        StationResponse 하행역 = createStationResponse("하행역");
        LineResponse 기본노선 = createLineResponse(
                "1호선", "blue", 상행역.getId(), 하행역.getId(), DEFAULT_DISTANCE
        );

        //when
        ExtractableResponse<Response> response = 구간_삭제(기본노선.getId(), 상행역.getId());
        LineResponse 기본노선_조회결과 = 노선_단건_조회(기본노선.getId()).as(LineResponse.class);

        //then
        List<String> stations = 기본노선_조회결과.getStations().stream()
                .map(StationResponse::getName).collect(Collectors.toList());
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value()),
                () -> assertThat(stations).containsExactly("상행역", "하행역")
        );
    }
}
