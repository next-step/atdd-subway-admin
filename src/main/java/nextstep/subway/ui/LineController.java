package nextstep.subway.ui;

import nextstep.subway.application.LineService;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.dto.SectionRequest;
import nextstep.subway.dto.SectionResponse;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
public class LineController {
    private final LineService lineService;

    public LineController(LineService lineService) {
        this.lineService = lineService;
    }

    @PostMapping("/lines")
    public ResponseEntity<LineResponse> createLine(@RequestBody LineRequest lineRequest) {
        LineResponse line = lineService.saveLine(lineRequest);
        return ResponseEntity.created(URI.create("/lines/" + line.getId())).body(line);
    }

    @GetMapping("/lines/{id}")
    public ResponseEntity<LineResponse> showLines(@PathVariable Long id) {
        return ResponseEntity.ok().body(lineService.findLine(id));
    }

    @PutMapping("/lines/{id}")
    public ResponseEntity<LineResponse> modifyLine(@PathVariable Long id, @RequestBody LineRequest lineRequest) {
        return ResponseEntity.ok().body(lineService.modify(id, lineRequest));
    }

    @DeleteMapping("/lines/{id}")
    public ResponseEntity<Void> deleteLines(@PathVariable Long id) {
        lineService.deleteLineById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping(value = "/lines", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<LineResponse>> showLines() {
        return ResponseEntity.ok().body(lineService.findAllLines());
    }

    @PostMapping(value = "/lines/{lineId}/sections")
    public ResponseEntity<SectionResponse> createSections(@PathVariable final Long lineId,
                                                          @RequestBody SectionRequest sectionRequest) {
        SectionResponse section = lineService.addSection(lineId, sectionRequest);
        return ResponseEntity.created(URI.create("/lines/" + section.getId() + "/sections")).body(section);
    }

    @GetMapping(value = "/lines/{lineId}/sections")
    public ResponseEntity<List<SectionResponse>> showSections(@PathVariable final Long lineId) {
        return ResponseEntity.ok(lineService.findAllSections(lineId));
    }

    @DeleteMapping(value = "/lines/{lineId}/sections")
    public ResponseEntity<List<SectionResponse>> deleteSections(
            @PathVariable final Long lineId, @RequestParam Long stationId) {
        lineService.deleteSection(lineId, stationId);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler({DataIntegrityViolationException.class, IllegalArgumentException.class})
    public ResponseEntity<Void> handleIllegalArgsException() {
        return ResponseEntity.badRequest().build();
    }
}
