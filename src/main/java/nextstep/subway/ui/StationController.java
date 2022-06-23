package nextstep.subway.ui;

import nextstep.subway.application.StationService;
import nextstep.subway.error.ApiExceptionResponse;
import nextstep.subway.dto.StationRequest;
import nextstep.subway.dto.StationResponse;
import nextstep.subway.error.SubwayError;
import nextstep.subway.error.exception.SubWayApiException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
public class StationController {
    private StationService stationService;

    public StationController(StationService stationService) {
        this.stationService = stationService;
    }

    @PostMapping("/stations")
    public ResponseEntity<StationResponse> createStation(@RequestBody StationRequest stationRequest) {
        StationResponse station = stationService.saveStation(stationRequest);
        return ResponseEntity.created(URI.create("/stations/" + station.getId())).body(station);
    }

    @GetMapping(value = "/stations", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<StationResponse>> showStations() {
        return ResponseEntity.ok().body(stationService.findAllStations());
    }

    @DeleteMapping("/stations/{id}")
    public ResponseEntity deleteStation(@PathVariable Long id) {
        stationService.deleteStationById(id);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(SubWayApiException.class)
    public ResponseEntity<ApiExceptionResponse> handleSubWayApiException(SubWayApiException e) {
        return ResponseEntity.badRequest()
                .body(new ApiExceptionResponse(e.getError(), e.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiExceptionResponse> handleException(Exception e) {
        return ResponseEntity.badRequest()
                .body(new ApiExceptionResponse(SubwayError.LINE_NOT_FOUND, e.getMessage()));
    }
}
