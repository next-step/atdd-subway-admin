package nextstep.subway.section;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import nextstep.subway.dto.SectionRequest;
import nextstep.subway.util.ExecuteRestEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import static org.assertj.core.api.Assertions.assertThat;

@Service
public class SectionExecuteRestEntity {

    @Autowired
    private ExecuteRestEntity executeRestEntity;

    public ValidatableResponse selectSection(String location) {
        return executeRestEntity.select(location);
    }

    public ExtractableResponse<Response> insertSectionSuccess(String location, SectionRequest request) {
        ExtractableResponse<Response> response = executeRestEntity.insert(request, location).extract();
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        return response;
    }

    public SectionRequest generateSectionRequest(Long upStationId, Long downStationId, int distance) {
        return new SectionRequest(upStationId, downStationId, distance);
    }

    public ExtractableResponse<Response> deleteSectionSuccess(String location) {
        ExtractableResponse<Response> response = executeRestEntity.delete(location).extract();
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
        return response;
    }
}
