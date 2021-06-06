package nextstep.subway.station.ui;

import java.net.URI;
import java.util.List;
import nextstep.subway.station.application.StationCommandService;
import nextstep.subway.station.application.StationQueryService;
import nextstep.subway.station.dto.StationRequest;
import nextstep.subway.station.dto.StationResponse;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StationController {

    private final StationCommandService stationCommandService;
    private final StationQueryService stationQueryService;

    public StationController(StationCommandService stationCommandService,
                             StationQueryService stationQueryService) {
        this.stationCommandService = stationCommandService;
        this.stationQueryService = stationQueryService;
    }

    @PostMapping("/stations")
    public ResponseEntity<StationResponse> createStation(@RequestBody StationRequest stationRequest) {
        Long stationId = stationCommandService.save(stationRequest.toStation());
        return ResponseEntity.created(URI.create("/stations/" + stationId))
                             .body(StationResponse.of(stationQueryService.findById(stationId)));
    }

    @GetMapping(value = "/stations", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<StationResponse>> showStations() {
        return ResponseEntity.ok().body(stationQueryService.findAllStations());
    }

    @DeleteMapping("/stations/{id}")
    public ResponseEntity<Void> deleteStation(@PathVariable Long id) {
        stationCommandService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
