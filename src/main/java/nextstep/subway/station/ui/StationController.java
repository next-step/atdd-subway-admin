package nextstep.subway.station.ui;

import nextstep.subway.station.application.StationCommandUseCase;
import nextstep.subway.station.application.StationQueryUseCase;
import nextstep.subway.station.dto.StationRequest;
import nextstep.subway.station.dto.StationResponse;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
public class StationController {
    private final StationQueryUseCase stationQueryUseCase;
    private final StationCommandUseCase stationCommandUseCase;

    public StationController(StationQueryUseCase stationQueryUseCase, StationCommandUseCase stationCommandUseCase) {
        this.stationQueryUseCase = stationQueryUseCase;
        this.stationCommandUseCase = stationCommandUseCase;
    }

    @PostMapping("/stations")
    public ResponseEntity<StationResponse> createStation(@RequestBody StationRequest stationRequest) {
        StationResponse station = stationCommandUseCase.saveStation(stationRequest);
        return ResponseEntity.created(URI.create("/stations/" + station.getId())).body(station);
    }

    @GetMapping(value = "/stations", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<StationResponse>> showStations() {
        return ResponseEntity.ok().body(stationQueryUseCase.findAllStations());
    }

    @DeleteMapping("/stations/{id}")
    public ResponseEntity<Void> deleteStation(@PathVariable Long id) {
        stationCommandUseCase.deleteStationById(id);
        return ResponseEntity.noContent().build();
    }
}
