package nextstep.subway.line.ui;

import nextstep.subway.line.application.exceptions.AlreadyExistsLineNameException;
import nextstep.subway.line.application.LineService;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.dto.StationResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/lines")
public class LineController {
    private final LineService lineService;

    public LineController(final LineService lineService) {
        this.lineService = lineService;
    }

    @PostMapping
    public ResponseEntity<LineResponse> createLine(@RequestBody LineRequest lineRequest) {
        Line line = lineService.saveLine(lineRequest.toLine(), lineRequest.getUpStationId(),
            lineRequest.getDownStationId(), lineRequest.getDistance());
        LineResponse lineResponse = LineResponse.of(line);
        return ResponseEntity.created(URI.create("/lines/" + lineResponse.getId())).body(lineResponse);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<LineResponse>> findLines() {
        List<LineResponse> lineResponses = LineResponse.list(lineService.findAllLines());
        return ResponseEntity.ok().body(lineResponses);
    }

    @GetMapping(value = "/{lineId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LineResponse> findLine(@PathVariable Long lineId) {
        Line line = lineService.findLineById(lineId);
        List<StationResponse> stationResponses = StationResponse.list(line.orderedStations());
        return ResponseEntity.ok().body(LineResponse.of(line, stationResponses));
    }

    @PutMapping(value = "/{lineId}")
    public ResponseEntity<?> updateLine(@PathVariable Long lineId, @RequestBody LineRequest lineRequest) {
        lineService.updateLineById(lineId, lineRequest.toLine());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping(value = "/{lineId}")
    public ResponseEntity<?> deleteLine(@PathVariable Long lineId) {
        lineService.deleteLineById(lineId);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler({AlreadyExistsLineNameException.class})
    public ResponseEntity<String> handleDuplicateException(
        AlreadyExistsLineNameException alreadyExistsLineNameException) {
        String message = alreadyExistsLineNameException.getMessage();
        return ResponseEntity.status(HttpStatus.CONFLICT).body(message);
    }
}
