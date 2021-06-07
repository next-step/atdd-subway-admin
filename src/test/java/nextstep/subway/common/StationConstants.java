package nextstep.subway.common;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import nextstep.subway.station.dto.StationRequest;
import nextstep.subway.station.dto.StationResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static org.assertj.core.api.Assertions.assertThat;

public enum StationConstants {

    GYULHYEON(1L, "귤현역"),
    BAKCHON(2L, "박촌역"),
    GEOMDAN_ORYU(3L, "검단오류역"),
    WANGGIL(4L, "왕길역"),
    SEOUL(5L, "서울역"),
    GONGDEOK(6L, "공덕역"),
    HONGIK_UNIV(7L, "홍대입구역"),
    DMC(8L, "디지털미디어시티역"),
    MAGONGNARU(9L, "마곡나루역"),
    GIMPO_AIRPORT(10L, "김포공항역"),
    GYEYANG(11L, "계양역");

    private final Long id;
    private final String name;

    private static final Map<StationConstants, StationResponse> CACHE =
        Arrays.stream(values())
              .collect(collectingAndThen(
                  toMap(Function.identity(),
                        c -> new StationResponse(c.getId(), c.getName(), null, null)),
                  Collections::unmodifiableMap));

    private static final List<StationResponse> ALL_STATIONS =
        Arrays.stream(values())
              .map(CACHE::get)
              .collect(collectingAndThen(toList(), Collections::unmodifiableList));

    StationConstants(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public StationResponse toResponse() {
        return CACHE.get(this);
    }

    public static void createAllStations() {
        ALL_STATIONS.forEach(stationResponse -> {
            ExtractableResponse<Response> response =
                RestAssured.given().log().all()
                           .body(new StationRequest(stationResponse.getName()))
                           .contentType(MediaType.APPLICATION_JSON_VALUE)
                           .when().post("/stations")
                           .then().log().all()
                           .extract();

            assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        });
    }
}
