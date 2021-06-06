package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.application.LineService;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.*;
import nextstep.subway.station.domain.*;
import nextstep.subway.station.dto.StationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class SectionAcceptanceTest extends AcceptanceTest {

    @Autowired
    LineRepository lineRepository;

    @Autowired
    LineService lineService;

    @Autowired
    StationRepository stationRepository;

    Long 일호선_id;

    Station 동춘역;
    Station 원인재역;
    Station 잠실역;
    Station 대청역;

    @BeforeEach
    void 미리_생성() {
        동춘역 = stationRepository.save(new Station("동춘역"));
        원인재역 = stationRepository.save(new Station("원인재역"));
        잠실역 = stationRepository.save(new Station("잠실역"));
        대청역 = stationRepository.save(new Station("대청역"));

        LineRequest lineRequest = new LineRequest("1호선", "red", 동춘역.getId(), 원인재역.getId(), 10);
        일호선_id = lineService.saveLine(lineRequest).getId();
    }

    @DisplayName("역 사이 새로운 역 등록")
    @Test
    void 역_사이_새로운_역_등록() {
        //given
        SectionRequest sectionRequest = new SectionRequest(동춘역.getId(), 대청역.getId(), 5);

        //when
        ExtractableResponse response = RestAssured.given().log().all()
                .body(sectionRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines/" + 일호선_id + "/sections/")
                .then().log().all().extract();

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getObject(".", LineResponse.class).getStations()).hasSize(3);
        assertThat(response.jsonPath().getObject(".", LineResponse.class)
                .getStations().stream().map(StationResponse::getName).collect(Collectors.toList()))
                .containsExactly("동춘역", "대청역", "원인재역");
    }
}
