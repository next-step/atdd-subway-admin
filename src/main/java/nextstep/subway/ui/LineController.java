package nextstep.subway.ui;

import nextstep.subway.application.LineService;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.dto.LineUpdateRequest;
import nextstep.subway.dto.SectionRequest;
import nextstep.subway.exception.SameSectionRegistrationException;
import nextstep.subway.exception.SectionLengthOverException;
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

    public LineController(LineService lineSer) {
        this.lineService = lineSer;
    }

    @PostMapping
    public ResponseEntity<LineResponse> createLine(@RequestBody LineRequest lineRequest) {
        LineResponse lineResponse = lineService.saveLine(lineRequest);
        return ResponseEntity.created(URI.create("/lines/" + lineResponse.getId())).body(lineResponse);
    }

    @GetMapping
    public ResponseEntity<List<LineResponse>> showLines() {
        return ResponseEntity.ok().body(lineService.findAllLines());
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<LineResponse> showLine(@PathVariable Long id) {
        return ResponseEntity.ok().body(lineService.findLineById(id));
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<Void> updateLine(@PathVariable Long id, @RequestBody LineUpdateRequest lineUpdateRequest) {
        lineService.updateLine(id, lineUpdateRequest);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> deleteLine(@PathVariable Long id) {
        lineService.deleteLineById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/sections")
    public ResponseEntity<LineResponse> createSections(@PathVariable Long id, @RequestBody SectionRequest sectionRequest) {
        return ResponseEntity.ok(lineService.addSection(id, sectionRequest));
    }

    @ExceptionHandler({SectionLengthOverException.class, SameSectionRegistrationException.class})
    public ResponseEntity handleIllegalArgsException() {
        return ResponseEntity.badRequest().build();
    }
}
