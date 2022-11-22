package nextstep.subway.ui;

import java.net.URI;
import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import nextstep.subway.application.LineService;
import nextstep.subway.application.SectionService;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.dto.SectionRequest;

@RequestMapping("/lines")
@RestController
public class LineController {
    private final LineService lineService;
    private final SectionService sectionService;

    public LineController(LineService lineService, SectionService sectionService) {
        this.lineService = lineService;
        this.sectionService = sectionService;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<LineResponse>> showLines() {
        return ResponseEntity.ok().body(lineService.findAllLines());
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LineResponse> showLine(
        @PathVariable Long id
    ) {
        return ResponseEntity.ok().body(lineService.findLineById(id));
    }

    @PostMapping
    public ResponseEntity<LineResponse> createLine(
        @RequestBody LineRequest lineRequest
    ) {
        LineResponse line = lineService.saveLine(lineRequest);
        return ResponseEntity.created(URI.create("/lines/" + line.getId())).body(line);
    }

    @PutMapping("/{id}")
    public ResponseEntity<LineResponse> updateLine(
        @PathVariable Long id,
        @RequestBody LineRequest lineRequest
    ) {
        return ResponseEntity.ok().body(lineService.updateLineById(id, lineRequest));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLine(
        @PathVariable Long id
    ) {
        lineService.deleteLineById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/sections")
    public ResponseEntity<Void> addSection(
        @PathVariable Long id,
        @RequestBody SectionRequest sectionRequest
    ) {
        sectionService.saveSection(id, sectionRequest);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}/sections")
    public ResponseEntity<Void> removeSection(
        @PathVariable Long id,
        @RequestParam Long stationId
    ) {
        sectionService.removeSectionByStationId(id, stationId);
        return ResponseEntity.ok().build();
    }

    @ExceptionHandler({DataIntegrityViolationException.class, IllegalArgumentException.class})
    public ResponseEntity<Void> handleIllegalArgsException() {
        return ResponseEntity.badRequest().build();
    }
}
