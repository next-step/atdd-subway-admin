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

import java.util.stream.Collectors;

import static nextstep.subway.line.SectionHelper.섹션_추가;
import static org.assertj.core.api.Assertions.assertThat;

public class SectionAcceptanceTest extends AcceptanceTest {

    @Autowired
    LineService lineService;

    @Autowired
    StationRepository stationRepository;

    Long 일호선_id;

    Station 동춘역;
    Station 원인재역;
    Station 잠실역;
    Station 대청역;

    ExtractableResponse addResponse;

    @BeforeEach
    void 미리_생성() {
        동춘역 = stationRepository.save(new Station("동춘역"));
        원인재역 = stationRepository.save(new Station("원인재역"));
        잠실역 = stationRepository.save(new Station("잠실역"));
        대청역 = stationRepository.save(new Station("대청역"));

        LineRequest lineRequest = new LineRequest("1호선", "red", 동춘역.getId(), 원인재역.getId(), 10);
        일호선_id = lineService.saveLine(lineRequest).getId();

        SectionRequest sectionRequest = new SectionRequest(대청역.getId(), 동춘역.getId(), 10);

        addResponse = 섹션_추가(sectionRequest, 일호선_id);
    }

    @DisplayName("맨 앞에 구간 등록")
    @Test
    void 맨_앞에_구간_등록() {
        //then
        assertThat(addResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(addResponse.jsonPath().getObject(".", LineResponse.class).getStations()).hasSize(3);
        assertThat(addResponse.jsonPath().getObject(".", LineResponse.class)
                .getStations().stream().map(StationResponse::getName).collect(Collectors.toList()))
                .containsExactly("대청역", "동춘역", "원인재역");
    }

    @DisplayName("맨 뒤에 구간 등록")
    @Test
    void 맨_뒤에_구간_등록() {
        //given
        SectionRequest sectionRequest = new SectionRequest(원인재역.getId(), 잠실역.getId(), 5);

        //when
        ExtractableResponse response = 섹션_추가(sectionRequest, 일호선_id);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getObject(".", LineResponse.class).getStations()).hasSize(4);
        assertThat(response.jsonPath().getObject(".", LineResponse.class)
                .getStations().stream().map(StationResponse::getName).collect(Collectors.toList()))
                .containsExactly("대청역", "동춘역", "원인재역", "잠실역");
    }

    @DisplayName("중간에 등록(앞에 역 일치)")
    @Test
    public void 중간에_등록_앞_역_일치() {
        //given
        SectionRequest sectionRequest = new SectionRequest(대청역.getId(), 잠실역.getId(), 5);

        //when
        ExtractableResponse response = 섹션_추가(sectionRequest, 일호선_id);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getObject(".", LineResponse.class).getStations()).hasSize(4);
        assertThat(response.jsonPath().getObject(".", LineResponse.class)
                .getStations().stream().map(StationResponse::getName).collect(Collectors.toList()))
                .containsExactly("대청역", "잠실역", "동춘역", "원인재역");

    }

    @DisplayName("중간에 등록(뒤에 역 일치")
    @Test
    public void 중간에_등록_뒤_역_일치() {
        //given
        SectionRequest sectionRequest = new SectionRequest(잠실역.getId(), 동춘역.getId(), 5);

        //when
        ExtractableResponse response = 섹션_추가(sectionRequest, 일호선_id);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getObject(".", LineResponse.class).getStations()).hasSize(4);
        assertThat(response.jsonPath().getObject(".", LineResponse.class)
                .getStations().stream().map(StationResponse::getName).collect(Collectors.toList()))
                .containsExactly("대청역", "잠실역", "동춘역", "원인재역");
    }

    @DisplayName("등록하는 두 역 이미 존재")
    @Test
    public void 등록하는_두_역_이미_존재() {
        //given
        SectionRequest sectionRequest = new SectionRequest(대청역.getId(), 동춘역.getId(), 5);

        //when
        ExtractableResponse response = 섹션_추가(sectionRequest, 일호선_id);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("등록하는 두 역 존재 안함")
    @Test
    public void 등록하는_두_역_존재_안함() {
        //given
        Station 삼성역 = stationRepository.save(new Station("삼성역"));
        SectionRequest sectionRequest = new SectionRequest(삼성역.getId(), 잠실역.getId(), 5);

        //when
        ExtractableResponse response = 섹션_추가(sectionRequest, 일호선_id);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("중간에 등록시 거리 초과")
    @Test
    public void 중간에_등록시_거리_초과() {
        //given
        SectionRequest sectionRequest = new SectionRequest(대청역.getId(), 동춘역.getId(), 15);

        //when
        ExtractableResponse response = 섹션_추가(sectionRequest, 일호선_id);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }
}
