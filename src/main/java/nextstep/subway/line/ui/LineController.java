package nextstep.subway.line.ui;

import nextstep.subway.line.application.LineService;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.section.dto.SectionRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity createLine(@RequestBody LineRequest lineRequest) {
        LineResponse line = lineService.saveLine(lineRequest);
        return ResponseEntity.created(URI.create("/lines/" + line.getId())).body(line);
    }

    @GetMapping
    public List<LineResponse> findAllLines() {
        return lineService.findByAll();
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity findLine(@PathVariable Long id) {
        LineResponse line = lineService.findById(id);
        return ResponseEntity.ok().body(line);
    }

    @PutMapping(path = "/{id}")
    public ResponseEntity modifyLine(@RequestBody LineRequest lineRequest, @PathVariable Long id) {
        LineResponse line = lineService.updateLine(lineRequest, id);
        return ResponseEntity.ok().body(line);
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity deleteLine(@PathVariable Long id) {
        lineService.deleteLine(id);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    @PostMapping(path ="/{id}/sections")
    public ResponseEntity addSection(
            @PathVariable Long id,
            @RequestBody SectionRequest sectionRequest) {
        LineResponse line = lineService.addStation(id, sectionRequest);
        return ResponseEntity.created(URI.create("/lines/" + line.getId() + "/sections/")).body(line);
    }

}
