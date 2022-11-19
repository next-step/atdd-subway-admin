package nextstep.subway.ui;

import java.net.URI;
import java.util.List;
import javax.persistence.PersistenceException;
import nextstep.subway.application.StationService;
import nextstep.subway.dto.StationRequest;
import nextstep.subway.dto.StationResponse;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/stations")
public class StationController {

    private StationService stationService;

    public StationController(StationService stationService) {
        this.stationService = stationService;
    }

    @PostMapping
    public ResponseEntity<StationResponse> createStation(@RequestBody StationRequest stationRequest) {
        Long id = stationService.saveStation(stationRequest);
        StationResponse stationResponse = new StationResponse();
        stationResponse.setId(id);
        return ResponseEntity.created(URI.create("/stations/" + id)).body(stationResponse);
    }

    @GetMapping("/{id}")
    public ResponseEntity<StationResponse> showStationById(@PathVariable Long id) {
        return ResponseEntity.ok().body(stationService.findResponseById(id));
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<StationResponse>> showStations() {
        return ResponseEntity.ok().body(stationService.findAllStations());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteStation(@PathVariable Long id) {
        stationService.deleteStationById(id);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler({DataIntegrityViolationException.class, PersistenceException.class})
    public ResponseEntity handleIllegalArgsException() {
        return ResponseEntity.badRequest().build();
    }
}
