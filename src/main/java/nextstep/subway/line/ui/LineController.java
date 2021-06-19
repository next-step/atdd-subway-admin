package nextstep.subway.line.ui;

import nextstep.subway.line.application.LineService;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.SectionRequest;
import nextstep.subway.section.domain.Section;
import nextstep.subway.section.service.SectionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/lines")
public class LineController {
    private final LineService lineService;
    private final SectionService sectionService;

    public LineController(LineService lineService, SectionService sectionService) {
        this.lineService = lineService;
        this.sectionService = sectionService;
    }


    @PostMapping
    public ResponseEntity saveLine(@RequestBody LineRequest lineRequest) {
        lineService.validateCheck(lineRequest);
        Section section = sectionService.saveSection(lineRequest.getUpStationId(), lineRequest.getDownStationId(), lineRequest.getDistance());
        LineResponse response = lineService.saveLine(lineRequest, section);
        return ResponseEntity.created(URI.create("/lines/" + response.getId())).body(response);
    }

    @GetMapping
    public ResponseEntity getLines() {
        List<LineResponse> line = lineService.findAllLine();
        return ResponseEntity.ok(line);
    }

    @GetMapping("/{lineId}")
    public ResponseEntity getLine(@PathVariable Long lineId) {
        Line line = lineService.findLine(lineId);
        return ResponseEntity.ok(LineResponse.of(line));
    }

    @PutMapping("/{lineId}")
    public ResponseEntity updateLine(@PathVariable Long lineId, @RequestBody LineRequest lineRequest) {
        lineService.updateLine(lineId, lineRequest);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{lineId}")
    public ResponseEntity deleteLine(@PathVariable Long lineId) {
        lineService.deleteLine(lineId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{lineId}/sections")
    public ResponseEntity saveLineSection(@PathVariable Long lineId, @RequestBody SectionRequest sectionRequest) {
        lineService.saveLineSection(lineId, sectionRequest);
        Section section = sectionService.saveSection(sectionRequest.getUpStationId(), sectionRequest.getUpStationId(), sectionRequest.getDistance());
        Line line = lineService.findLine(lineId);
        sectionService.saveSection(line, sectionRequest);
        return ResponseEntity.created(URI.create("/sections/"+1l)).body(null);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity handleIllegalArgsException(RuntimeException e) {
        return ResponseEntity.badRequest().build();
    }
}
