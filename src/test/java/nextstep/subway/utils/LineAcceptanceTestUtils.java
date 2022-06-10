package nextstep.subway.utils;

import static nextstep.subway.station.LineAcceptanceTest.LINE_BASE_URL;
import static nextstep.subway.utils.ResponseBodyExtractUtils.getId;
import static nextstep.subway.utils.RestAssuredUtils.post;
import static nextstep.subway.utils.StationsAcceptanceUtils.generateStation;

import io.restassured.response.Response;
import nextstep.subway.dto.line.CreateLineRequest;

public class LineAcceptanceTestUtils {

    public static Response generateLine(final String lineName, final String upStationName, final String downStationName) {
        Response upStationResponse = generateStation(upStationName);
        Response downStationResponse = generateStation(downStationName);
        CreateLineRequest createLineRequest = new CreateLineRequest(
            lineName,
            "some-color-code",
            Long.parseLong(getId(upStationResponse)),
            Long.parseLong(getId(downStationResponse)),
            10
        );
        return post(LINE_BASE_URL, createLineRequest).extract().response();
    }
}
