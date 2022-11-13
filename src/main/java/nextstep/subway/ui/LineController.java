package nextstep.subway.ui;

import nextstep.subway.application.LineService;
import nextstep.subway.application.StationService;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.dto.StationRequest;
import nextstep.subway.dto.StationResponse;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
public class LineController {
    private LineService lineService;

    public LineController(LineService lineService) {
        this.lineService = lineService;
    }

    @PostMapping("/lines")
    public ResponseEntity<LineResponse> createLine(@RequestBody LineRequest lineRequest) {
        LineResponse line = lineService.saveLine(lineRequest);
        return ResponseEntity.created(URI.create("/lines/" + line.getId())).body(line);
    }
//
//    @GetMapping(value = "/stations", produces = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity<List<StationResponse>> showStations() {
//        return ResponseEntity.ok().body(stationService.findAllStations());
//    }
//
//    @GetMapping(value = "/stations/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity<StationResponse> searchStation(@PathVariable Long id) {
//        return ResponseEntity.ok().body(stationService.findStationById(id));
//    }
//
//    @DeleteMapping("/stations/{id}")
//    public ResponseEntity deleteStation(@PathVariable Long id) {
//        stationService.deleteStationById(id);
//        return ResponseEntity.noContent().build();
//    }
//
//    @ExceptionHandler(DataIntegrityViolationException.class)
//    public ResponseEntity handleIllegalArgsException() {
//        return ResponseEntity.badRequest().build();
//    }
}
