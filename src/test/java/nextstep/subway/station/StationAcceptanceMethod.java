package nextstep.subway.station;

import org.springframework.http.MediaType;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.station.dto.StationRequest;

public class StationAcceptanceMethod {
	public static ExtractableResponse<Response> deleteStation(ExtractableResponse<Response> createResponse) {
		String uri = createResponse.header("Location");

		return RestAssured.given().log().all()
			.when()
			.delete(uri)
			.then().log().all()
			.extract();
	}

	public static ExtractableResponse<Response> createStations(StationRequest stationRequest) {
		return RestAssured.given().log().all()
			.body(stationRequest)
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.when()
			.post("/stations")
			.then().log().all()
			.extract();
	}

	public static ExtractableResponse<Response> findAllStations() {
		return RestAssured.given().log().all()
			.when()
			.get("/stations")
			.then().log().all()
			.extract();
	}
}
