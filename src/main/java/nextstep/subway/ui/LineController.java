package nextstep.subway.ui;

import java.net.URI;
import java.util.List;
import javax.persistence.PersistenceException;
import nextstep.subway.application.LineService;
import nextstep.subway.application.SectionService;
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
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/lines")
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

    @GetMapping("/{lineId}/section")
    public ResponseEntity<List<SectionResponse>> showSections(@PathVariable("lineId") Long lineId) {
        return ResponseEntity.ok().body(sectionService.findResponsesByLineId(lineId));
    }

    @GetMapping(value = "/{name}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LineResponse> showLineByName(@PathVariable("name") String name) {
        return ResponseEntity.ok().body(lineService.findResponseByName(name));
    }

    @PostMapping
    public ResponseEntity<LineResponse> createLine(@RequestBody LineRequest lineRequest) {
        Long id = lineService.saveLine(lineRequest);
        LineResponse lineResponse = new LineResponse();
        lineResponse.setId(id);
        return ResponseEntity.created(URI.create("/lines/" + id)).body(lineResponse);
    }

    @PostMapping("/{lineId}/section")
    public ResponseEntity addSection(@PathVariable("lineId") Long lineId,
                                     @RequestBody SectionRequest sectionRequest) {
        sectionService.addSection(lineId, sectionRequest);
        return ResponseEntity.ok().build();
    }

    @PutMapping(value = "/{name}")
    public ResponseEntity modifyLine(@PathVariable("name") String name, @RequestBody LineRequest lineRequest) {
        lineService.updateLine(name, lineRequest);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteLine(@PathVariable Long id) {
        lineService.deleteLineById(id);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler({PersistenceException.class, IllegalArgumentException.class})
    public ResponseEntity handleIllegalArgsException() {
        return ResponseEntity.badRequest().build();
    }
}
