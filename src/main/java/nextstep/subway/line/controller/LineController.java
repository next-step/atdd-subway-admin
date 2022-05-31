package nextstep.subway.line.controller;

import java.net.URI;
import java.util.List;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.service.LineService;
import nextstep.subway.section.dto.SectionRequest;
import nextstep.subway.section.dto.SectionResponse;
import nextstep.subway.section.service.SectionService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LineController {

    private final LineService lineService;
    private final SectionService sectionService;

    public LineController(LineService lineService, SectionService sectionService) {
        this.lineService = lineService;
        this.sectionService = sectionService;
    }

    @PostMapping("/lines")
    public ResponseEntity<LineResponse> createLine(@RequestBody LineRequest lineRequest) {
        LineResponse lineResponse = lineService.saveLine(lineRequest);
        return ResponseEntity.created(URI.create("/lines/" + lineResponse.getId())).body(lineResponse);
    }

    @GetMapping(value = "/lines", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<LineResponse>> showLines() {
        return ResponseEntity.ok().body(lineService.findAllLines());
    }

    @GetMapping("/lines/{line_id}")
    public ResponseEntity<LineResponse> showLine(@PathVariable(name = "line_id") Long lineId) {
        return ResponseEntity.ok().body(lineService.findLine(lineId));
    }

    @PutMapping("/lines/{line_id}")
    public ResponseEntity modifyLine(@PathVariable(name = "line_id") Long lineId,
        @RequestBody LineRequest lineRequest) {
        lineService.modifyLine(lineId, lineRequest);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/lines/{line_id}")
    public ResponseEntity deleteLine(@PathVariable(name = "line_id") Long lineId) {
        lineService.deleteLine(lineId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/lines/{line_id}/sections")
    public ResponseEntity<LineResponse> addSection(@PathVariable(name = "line_id") Long lineId,
        @RequestBody SectionRequest sectionRequest) {
        LineResponse lineResponse = sectionService.addSection(lineId, sectionRequest);
        return ResponseEntity.created(URI.create("/lines/" + lineId + "/sections")).body(lineResponse);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity handleIllegalArgsException() {
        return ResponseEntity.badRequest().build();
    }

}
