package nextstep.subway.station.ui;

import lombok.RequiredArgsConstructor;
import nextstep.subway.station.application.StationService;
import nextstep.subway.station.dto.StationRequest;
import nextstep.subway.station.dto.StationResponse;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.net.URI;
import java.util.List;
import static nextstep.subway.PageController.URIMapping.STATION;

@RestController
@RequestMapping(STATION)
@RequiredArgsConstructor
public class StationController {
    private final StationService stationService;

    @PostMapping
    public ResponseEntity<StationResponse> createStation(@RequestBody final StationRequest stationRequest) {
        StationResponse station = stationService.saveStation(stationRequest);
        return ResponseEntity.created(URI.create(STATION + "/" + station.getId())).body(station);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<StationResponse>> showStations() {
        return ResponseEntity.ok().body(stationService.findAllStations());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteStation(@PathVariable final Long id) {
        stationService.deleteStationById(id);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity handleIllegalArgsException(final DataIntegrityViolationException e) {
        return ResponseEntity.badRequest().build();
    }
}
