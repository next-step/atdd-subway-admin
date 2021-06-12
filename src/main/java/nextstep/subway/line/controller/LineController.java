package nextstep.subway.line.controller;

import nextstep.subway.line.application.LineService;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.LinesSubResponse;
import nextstep.subway.section.dto.SectionRequest;
import nextstep.subway.section.dto.SectionResponse;
import nextstep.subway.section.service.SectionService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@Validated
@RestController
@RequestMapping("/lines")
public class LineController {
    private final LineService lineService;
    private final SectionService sectionService;

    public LineController(final LineService lineService, SectionService sectionService) {
        this.lineService = lineService;
        this.sectionService = sectionService;
    }

    @PostMapping
    public ResponseEntity createLine(@RequestBody LineRequest lineRequest) throws NoSuchFieldException {
        lineService.validateDuplicatedName(lineRequest);
        SectionResponse sectionResponse = sectionService.createSection(new SectionRequest(lineRequest.getDistance()));
        LineResponse line = lineService.saveLine(lineRequest, sectionResponse);
        return ResponseEntity.created(URI.create("/lines/" + line.getId())).body(line);
    }

    @PostMapping("/{lineId}/sections")
    public ResponseEntity createSectionAndRegisterWithLine(@PathVariable Long lineId, @RequestBody SectionRequest sectionRequest) {
        Long sectionId = sectionService.createSection(new SectionRequest(sectionRequest.getDistance())).getId();
        SectionResponse sectionResponse = lineService.registerWithLine(lineId, sectionRequest, sectionId);
        return ResponseEntity.created(URI.create("/sections/" + sectionResponse.getId())).body(sectionResponse);
    }

    @GetMapping("/{lineId}")
    public ResponseEntity readLine(@PathVariable Long lineId) {
        LinesSubResponse linesSubResponse = lineService.readLine(lineId);
        return ResponseEntity.ok(linesSubResponse);
    }

    @GetMapping
    public ResponseEntity readLineAll() {
        List<LinesSubResponse> linesSubResponses = lineService.readLineAll();
        return ResponseEntity.ok(linesSubResponses);
    }

    @PutMapping("/{lineId}")
    public ResponseEntity changeLine(@PathVariable Long lineId, @RequestBody LineRequest lineRequest) {
        lineService.changeLine(lineId, lineRequest);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{lineId}")
    public ResponseEntity removeLine(@PathVariable Long lineId) {
        lineService.removeLine(lineId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{lineId}/sections")
    public ResponseEntity removeStation(@PathVariable Long lineId, @RequestParam Long stationId) {
        lineService.removeStation(lineId, stationId);
        return ResponseEntity.ok().build();
    }
}
