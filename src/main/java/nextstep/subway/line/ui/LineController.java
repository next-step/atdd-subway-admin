package nextstep.subway.line.ui;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import nextstep.subway.line.application.LineService;
import nextstep.subway.line.dto.SectionRequest;
import nextstep.subway.line.dto.LineAndStationResponse;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;

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
    public ResponseEntity<List<LineAndStationResponse>> showLines() {
        return ResponseEntity.ok().body(lineService.findAllLines());
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<LineAndStationResponse> findByLine(@PathVariable Long id) {
        return ResponseEntity.ok().body(lineService.findByLine(id));
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity updateLine(@PathVariable Long id, @RequestBody LineRequest lineRequest) {
        lineService.updateLineById(id, lineRequest);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteLine(@PathVariable Long id) {
        lineService.deleteStationById(id);
        return ResponseEntity.noContent().build();
    }


    @PostMapping("/{id}/sections")
    public ResponseEntity addSection(@PathVariable Long id, @RequestBody SectionRequest sectionRequest) {
        lineService.addSection(id, sectionRequest);
        return ResponseEntity.noContent().build();
    }
}
