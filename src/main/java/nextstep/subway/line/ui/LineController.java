package nextstep.subway.line.ui;

import static java.lang.String.*;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import nextstep.subway.line.application.LineService;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.SectionRequest;

@RestController
@RequestMapping("/lines")
public class LineController {

    private final LineService lineService;

    public LineController(final LineService lineService) {
        this.lineService = lineService;
    }

    @PostMapping
    public ResponseEntity<LineResponse> createLine(@RequestBody LineRequest lineRequest) {
        LineResponse line = lineService.saveLine(lineRequest);
        return ResponseEntity.created(URI.create("/lines/" + line.getId())).body(line);
    }

    @GetMapping
    public ResponseEntity<List<LineResponse>> findAll() {
        List<LineResponse> lineAll = lineService.findAllLine();
        return ResponseEntity.ok(lineAll);
    }

    @GetMapping(value = "{id}")
    public ResponseEntity<LineResponse> findLineById(final @PathVariable(value = "id") Long id) {
        return ResponseEntity.ok(lineService.findLineById(id));
    }

    @PatchMapping(value = "{id}")
    public ResponseEntity<LineResponse> updateLine(final @PathVariable(value = "id") Long id,
        @RequestBody() LineRequest lineRequest) {
        LineResponse lineResponse = lineService.updateLine(id, lineRequest);
        return ResponseEntity.ok(lineResponse);
    }

    @DeleteMapping("{id}")
    public ResponseEntity updateLine(final @PathVariable(value = "id") Long id) {
        lineService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "/{lineId}/sections")
    public ResponseEntity addSection(@PathVariable("lineId") Long lindId, @RequestBody SectionRequest sectionRequest) {
        lineService.addSection(lindId, sectionRequest);
        return ResponseEntity.created(URI.create(format("/%s/sections", lindId))).build();
    }

    @DeleteMapping("{lineId}/sections")
    public ResponseEntity deleteSection(@PathVariable("lineId") final Long lineId, @RequestParam("stationId") final Long stationId) {
        lineService.deleteSection(lineId, stationId);
        return ResponseEntity.ok().build();
    }
}
