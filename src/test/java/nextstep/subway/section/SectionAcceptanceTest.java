package nextstep.subway.section;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.section.dto.SectionRequest;
import nextstep.subway.station.dto.StationResponse;
import nextstep.subway.line.LineTestMethods;
import nextstep.subway.station.StationTestMethods;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.stream.Collectors;

import static nextstep.subway.section.SectionTestMethods.구간_추가;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("구간 관련 기능")
public class SectionAcceptanceTest extends AcceptanceTest {
    private StationResponse 상행역;
    private StationResponse 하행역;
    private LineResponse 기본노선;

    public static final int DEFAULT_DISTANCE = 10;

    @BeforeEach
    void initialize(){
        상행역 = StationTestMethods.지하철역_생성("상행역").as(StationResponse.class);
        하행역 = StationTestMethods.지하철역_생성("하행역").as(StationResponse.class);
        기본노선 = LineTestMethods.노선_생성(
                LineRequest.of("1호선", "blue", 상행역.getId(), 하행역.getId(), DEFAULT_DISTANCE))
                .as(LineResponse.class);
    }

    /**
     * Given 기본노선(상행역-하행역)에 [새로운역-하행역]구간을 추가하고
     * When 노선을 조회하면
     * Then 상행역, 새로운역, 하행역이 조회된다
     */
    @DisplayName("역 사이에 새로운 역을 등록한다.")
    @Test
    void createSectionBetweenStations() {
        //given
        StationResponse 새로운역 = StationTestMethods.지하철역_생성("새로운역").as(StationResponse.class);
        SectionRequest 새로운역_하행역_구간 = new SectionRequest(새로운역.getId(), 하행역.getId(), 5);
        구간_추가(기본노선.getId(), 새로운역_하행역_구간);

        //when
        LineResponse 기본노선_조회결과 = LineTestMethods.노선_단건_조회(기본노선.getId()).as(LineResponse.class);
        List<String> 기본노선_stations = 기본노선_조회결과.getStations().stream()
                .map(StationResponse::getName).collect(Collectors.toList());

        //then
        assertThat(기본노선_stations).containsExactly("상행역", "새로운역", "하행역");
    }

    /**
     * Given 기본노선(상행역-하행역)에 [새로운역-상행역]구간을 추가하고
     * When 노선을 조회하면
     * Then 새로운역, 상행역, 하행역이 조회된다
     */
    @DisplayName("새로운 역을 가장 앞에 등록한다.")
    @Test
    void createSectionBeforeFirstStation() {
        //given
        StationResponse 새로운역 = StationTestMethods.지하철역_생성("새로운역").as(StationResponse.class);
        SectionRequest 새로운역_상행역_구간 = new SectionRequest(새로운역.getId(), 상행역.getId(), 5);
        구간_추가(기본노선.getId(), 새로운역_상행역_구간);

        //when
        LineResponse 기본노선_조회결과 = LineTestMethods.노선_단건_조회(기본노선.getId()).as(LineResponse.class);
        List<String> 기본노선_stations = 기본노선_조회결과.getStations().stream()
                .map(StationResponse::getName).collect(Collectors.toList());

        //then
        assertThat(기본노선_stations).containsExactly("새로운역", "상행역", "하행역");
    }

    /**
     * Given 기본노선(상행역-하행역)에 [하행역-새로운역]구간을 추가하고
     * When 노선을 조회하면
     * Then 상행역, 하행역, 새로운역이 조회된다
     */
    @DisplayName("새로운 역을 가장 뒤에 등록한다.")
    @Test
    void createSectionAfterLastStation() {
        //given
        StationResponse 새로운역 = StationTestMethods.지하철역_생성("새로운역").as(StationResponse.class);
        SectionRequest 하행역_새로운역_구간 = new SectionRequest(하행역.getId(), 새로운역.getId(), 5);
        구간_추가(기본노선.getId(), 하행역_새로운역_구간);

        //when
        LineResponse 기본노선_조회결과 = LineTestMethods.노선_단건_조회(기본노선.getId()).as(LineResponse.class);
        List<String> 기본노선_stations = 기본노선_조회결과.getStations().stream()
                .map(StationResponse::getName).collect(Collectors.toList());

        //then
        assertThat(기본노선_stations).containsExactly("상행역", "하행역", "새로운역");
    }

    /**
     * Given 기본노선(상행역-하행역)을 등록하고
     * When [새로운역-하행역] 구간을 기존 길이보다 길거나 같게 등록하면
     * Then 등록이되지 않는다.
     */
    @DisplayName("역 사이에 새로운 역을 등록할 경우, 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없다.")
    @Test
    void exception_createLongerSectionBetweenStations() {
        //when
        StationResponse 새로운역 = StationTestMethods.지하철역_생성("새로운역").as(StationResponse.class);
        SectionRequest 새로운역_하행역_구간 = new SectionRequest(새로운역.getId(), 하행역.getId(), DEFAULT_DISTANCE);
        ExtractableResponse<Response> response = 구간_추가(기본노선.getId(), 새로운역_하행역_구간);

        //Then
        LineResponse 기본노선_조회결과 = LineTestMethods.노선_단건_조회(기본노선.getId()).as(LineResponse.class);
        List<String> 기본노선_stations = 기본노선_조회결과.getStations().stream()
                .map(StationResponse::getName).collect(Collectors.toList());
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value()),
                () -> assertThat(기본노선_stations).containsExactly("상행역", "하행역")
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
        //when
        StationResponse 새로운역 = StationTestMethods.지하철역_생성("새로운역").as(StationResponse.class);
        SectionRequest 새로운역_하행역_구간 = new SectionRequest(상행역.getId(), 하행역.getId(), 5);
        ExtractableResponse<Response> response = 구간_추가(기본노선.getId(), 새로운역_하행역_구간);

        //Then
        LineResponse 기본노선_조회결과 = LineTestMethods.노선_단건_조회(기본노선.getId()).as(LineResponse.class);
        List<String> 기본노선_stations = 기본노선_조회결과.getStations().stream()
                .map(StationResponse::getName).collect(Collectors.toList());
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value()),
                () -> assertThat(기본노선_stations).containsExactly("상행역", "하행역")
        );
    }

    /**
     * Given 상행종점역, 하행종점역을 가진 노선을 등록하고
     * When 새로운역, 새로운역2 구간을 추가하면
     * Then 예외가 발생한다
     */
    @DisplayName("상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없다.")
    @Test
    void exception_createSectionWithUnknownStations() {
        //when
        StationResponse 새로운역 = StationTestMethods.지하철역_생성("새로운역").as(StationResponse.class);
        StationResponse 새로운역2 = StationTestMethods.지하철역_생성("새로운역2").as(StationResponse.class);
        SectionRequest 새로운역_새로운역2_구간 = new SectionRequest(새로운역.getId(), 새로운역2.getId(), 5);
        ExtractableResponse<Response> response = 구간_추가(기본노선.getId(), 새로운역_새로운역2_구간);

        //Then
        LineResponse 기본노선_조회결과 = LineTestMethods.노선_단건_조회(기본노선.getId()).as(LineResponse.class);
        List<String> 기본노선_stations = 기본노선_조회결과.getStations().stream()
                .map(StationResponse::getName).collect(Collectors.toList());
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value()),
                () -> assertThat(기본노선_stations).containsExactly("상행역", "하행역")
        );
    }
}
