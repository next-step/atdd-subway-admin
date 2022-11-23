package nextstep.subway.ui;

import java.net.URI;
import java.util.List;
import javax.persistence.PersistenceException;
import nextstep.subway.application.LineService;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.dto.SectionRequest;
import nextstep.subway.dto.SectionResponse;
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

@RestController
@RequestMapping("/lines")
public class LineController {

    private final LineService lineService;

    public LineController(LineService lineService) {
        this.lineService = lineService;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<LineResponse>> showLines() {
        return ResponseEntity.ok()
                .body(lineService.findAllLines());
    }

    @GetMapping("/{lineId}/sections")
    public ResponseEntity<SectionResponse> showSections(@PathVariable("lineId") Long lineId) {
        return ResponseEntity.ok()
                .body(lineService.findSectionResponsesByLineId(lineId));
    }

    @GetMapping(value = "/{lineId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LineResponse> showLineById(@PathVariable("lineId") Long lineId) {
        return ResponseEntity.ok()
                .body(lineService.findResponseById(lineId));
    }

    @PostMapping
    public ResponseEntity<LineResponse> createLine(@RequestBody LineRequest lineRequest) {
        Long id = lineService.saveLine(lineRequest);
        LineResponse lineResponse = new LineResponse();
        lineResponse.setId(id);
        return ResponseEntity.created(URI.create("/lines/" + id))
                .body(lineResponse);
    }

    @PostMapping("/{lineId}/sections")
    public ResponseEntity addSection(@PathVariable("lineId") Long lineId,
                                     @RequestBody SectionRequest sectionRequest) {
        lineService.addSection(lineId, sectionRequest);
        return ResponseEntity.ok()
                .build();
    }

    @PutMapping(value = "/{name}")
    public ResponseEntity modifyLine(@PathVariable("name") String name, @RequestBody LineRequest lineRequest) {
        lineService.updateLine(name, lineRequest);
        return ResponseEntity.ok()
                .build();
    }

    @DeleteMapping("/{lineId}")
    public ResponseEntity deleteLine(@PathVariable("lineId") Long lineId) {
        lineService.deleteLineById(lineId);
        return ResponseEntity.noContent()
                .build();
    }

    @DeleteMapping("/{lineId}/sections")
    public ResponseEntity removeLineStation(@PathVariable("lineId") Long lineId, @RequestParam Long stationId) {
        lineService.deleteSectionByStationId(lineId, stationId);
        return ResponseEntity.ok()
                .build();
    }

    @ExceptionHandler({PersistenceException.class, IllegalArgumentException.class})
    public ResponseEntity handleIllegalArgsException() {
        return ResponseEntity.badRequest()
                .build();
    }
}
