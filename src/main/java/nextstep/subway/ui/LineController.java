package nextstep.subway.ui;

import java.net.URI;
import java.util.List;
import nextstep.subway.application.LineService;
import nextstep.subway.application.LineStationService;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.dto.SectionRequest;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
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

    LineService lineService;
    LineStationService sectionService;

    public LineController(LineService lineService, LineStationService sectionService) {
        this.lineService = lineService;
        this.sectionService = sectionService;
    }

    @PostMapping
    public ResponseEntity<LineResponse> createLine(@RequestBody LineRequest lineRequest) {
        LineResponse lineResponse = lineService.saveLine(lineRequest);

        return ResponseEntity.created(URI.create("/lines/" + lineResponse.getId())).body(lineResponse);
    }

    @GetMapping
    public ResponseEntity<List<LineResponse>> readLines() {
        List<LineResponse> lineResponses = lineService.readLines();

        return ResponseEntity.ok().body(lineResponses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LineResponse> readLine(@PathVariable long id) throws NotFoundException {
        LineResponse lineResponse = lineService.readLine(id);

        return ResponseEntity.ok().body(lineResponse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteLine(@PathVariable long id) {
        lineService.deleteLine(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity updateLine(@PathVariable long id, @RequestBody LineRequest lineRequest)
            throws NotFoundException {
        lineService.updateLine(id, lineRequest);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{lineId}/sections")
    public ResponseEntity<LineResponse> addSection(@PathVariable Long lineId,
                                                   @RequestBody SectionRequest sectionRequest) {
        LineResponse lineResponse = sectionService.addSection(sectionRequest, lineId);
        return ResponseEntity.ok().body(lineResponse);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<String> handleNotFoundException() {
        return ResponseEntity.badRequest().body("존재하지 않는 노선입니다.");
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntimeException(RuntimeException runtimeException) {
        return ResponseEntity.badRequest().body(runtimeException.getMessage());
    }
}
