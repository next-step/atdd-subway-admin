package nextstep.subway.station.ui;

import java.net.URI;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import nextstep.subway.station.application.StationService;
import nextstep.subway.station.dto.StationRequest;
import nextstep.subway.station.dto.StationResponse;

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

    @PostMapping("/stations/all")
    public ResponseEntity<List<StationResponse>> createStations(@RequestBody List<StationRequest> stationRequests) {
        try {
            List<StationResponse> stations = stationService.saveAllStations(stationRequests);
            return ResponseEntity.created(URI.create("/stations")).body(stations);
        } catch (DataIntegrityViolationException exception) {
            throw new DuplicateKeyException("중복된 지하철 역 이름은 등록될 수 없습니다.");
        }
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

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity handleIllegalArgsException(DataIntegrityViolationException e) {
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler(DuplicateKeyException.class)
    public ResponseEntity handleDuplicateKeyException(DuplicateKeyException e) {
        return ResponseEntity.badRequest().body(makeErrorMessage(HttpStatus.BAD_REQUEST, e.getMessage()));
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity handleNoSuchElementException(NoSuchElementException e) {
        return ResponseEntity.badRequest().body(makeErrorMessage(HttpStatus.BAD_REQUEST, e.getMessage()));
    }

    private Map<String, Object> makeErrorMessage(HttpStatus status, String errorMessage) {
        Map<String, Object> errors = new HashMap<>();
        errors.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")));
        errors.put("status", status.value());
        errors.put("errorMessage", errorMessage);
        return errors;
    }
}
