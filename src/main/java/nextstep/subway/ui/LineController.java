package nextstep.subway.ui;

import nextstep.subway.application.LineService;
import nextstep.subway.application.StationService;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Station;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.dto.SectionRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
public class LineController {
    private LineService lineService;
    private StationService stationService;

    public LineController(LineService lineService, StationService stationService) {
        this.lineService = lineService;
        this.stationService = stationService;
    }

    @PostMapping("/lines")
    public ResponseEntity<LineResponse> createLine(@RequestBody LineRequest lineRequest) {
        LineResponse lineResponse = lineService.saveLine(lineRequest);
        return ResponseEntity.created(URI.create("/lines/" + lineResponse.getId())).body(lineResponse);
    }
    
    @GetMapping(value = "/lines", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<LineResponse>> getLines() {
        List<LineResponse> lineResponses = lineService.findAllLines();
        return ResponseEntity.ok().body(lineResponses);
    }

    @GetMapping(value = "/lines/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LineResponse> getLines(@PathVariable Long id) {
        LineResponse lineResponse = lineService.findById(id);
        return ResponseEntity.ok().body(lineResponse);
    }

    @DeleteMapping("/lines/{id}")
    public ResponseEntity deleteLine(@PathVariable Long id) {
        lineService.deleteLineById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/lines/{lineId}/sections")
    public ResponseEntity addSection(
            @PathVariable Long lineId,
            @RequestBody SectionRequest sectionRequest) {
        Station upStation = stationService.findById(sectionRequest.getUpStation_id());
        Station downStation = stationService.findById(sectionRequest.getDownStation_id());
        lineService.saveSection(lineId, upStation, downStation, sectionRequest.getDistance());
        return ResponseEntity.ok().build();
    }

    @ExceptionHandler({DataIntegrityViolationException.class, EmptyResultDataAccessException.class})
    public ResponseEntity handleIllegalArgsException() {
        return ResponseEntity.badRequest().build();
    }

}
