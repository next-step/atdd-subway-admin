package nextstep.subway.line.ui;

import nextstep.subway.line.application.LineService;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.section.dto.SectionRequest;
import nextstep.subway.station.application.StationService;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.dto.StationResponse;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/lines")
public class LineController {
    private final LineService lineService;
    private final StationService stationService;


    public LineController(final LineService lineService, StationService stationService) {
        this.lineService = lineService;
        this.stationService = stationService;
    }

    @PostMapping
    public ResponseEntity<LineResponse> createLine(@RequestBody LineRequest lineRequest) {
        LineResponse line = lineService.saveLine(lineRequest);
        return ResponseEntity.created(URI.create("/lines/" + line.getId())).body(line);
    }

    @GetMapping
    public ResponseEntity<List<LineResponse>> showLines() {
        return ResponseEntity.ok().body(lineService.findAllLines());
    }

    @GetMapping(value = "{id}")
    public ResponseEntity<LineResponse> showLine(@PathVariable Long id) {
        return ResponseEntity.ok().body(lineService.findLineId(id));
    }

    @PutMapping(value = "{id}")
    public ResponseEntity<LineResponse> updateLine(@PathVariable Long id, @RequestBody LineRequest lineRequest) {
        return ResponseEntity.ok().body(lineService.updateLine(id, lineRequest));
    }

    @DeleteMapping("{id}")
    public ResponseEntity deleteLine(@PathVariable Long id) {
        lineService.deleteLineById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("{id}/sections")
    public ResponseEntity<LineResponse> addSection(@PathVariable Long id, @RequestBody SectionRequest sectionRequest) {
        LineResponse line = lineService.addSection(id, sectionRequest);
        return ResponseEntity.ok().body(line);
    }

    @DeleteMapping("/{id}/sections")
    public ResponseEntity removeStation(@PathVariable Long id, @RequestParam Long stationId) {
        StationResponse stationResponse = stationService.findStationById(stationId);
        lineService.removeSectionByStationId(id, stationResponse.toStation());
        return ResponseEntity.ok().build();
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity handleIllegalArgsException(DataIntegrityViolationException e) {
        return ResponseEntity.badRequest().build();
    }
}
