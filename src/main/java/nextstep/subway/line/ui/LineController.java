package nextstep.subway.line.ui;

import nextstep.subway.line.application.LineService;
import nextstep.subway.line.application.exceptions.LineNotFoundException;
import nextstep.subway.line.domain.exceptions.InvalidSectionException;
import nextstep.subway.line.domain.exceptions.StationNotFoundException;
import nextstep.subway.line.domain.exceptions.TargetSectionNotFoundException;
import nextstep.subway.line.domain.exceptions.TooLongSectionException;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.SectionRequest;
import nextstep.subway.line.domain.exceptions.EntityNotFoundException;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
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

    @PostMapping("/{lineId}/sections")
    public ResponseEntity addSection(
            @PathVariable("lineId") Long lineId,
            @Validated @RequestBody SectionRequest sectionRequest
    ) {
        boolean addSectionResult = lineService.addSection(lineId, sectionRequest);

        if (addSectionResult) {
            return ResponseEntity.created(URI.create("/lines/" + lineId)).build();
        }

        return ResponseEntity.badRequest().build();
    }

    @PostMapping
    public ResponseEntity createLine(@Validated @RequestBody LineRequest lineRequest) {
        LineResponse lineResponse = lineService.saveLine(lineRequest);
        return ResponseEntity.created(URI.create("/lines/" + lineResponse.getId())).body(lineResponse);
    }

    @GetMapping
    public ResponseEntity getLines() {
        List<LineResponse> lineResponses = lineService.getAllLines();
        return ResponseEntity.ok().body(lineResponses);
    }

    @GetMapping("/{lineId}")
    public ResponseEntity getLine(
            @PathVariable("lineId") Long lineId
    ) {
        LineResponse lineResponse = lineService.getLine(lineId);
        return ResponseEntity.ok().body(lineResponse);
    }

    @PutMapping("/{lineId}")
    public ResponseEntity updateLine(
            @PathVariable("lineId") Long lineId,
            @Validated @RequestBody LineRequest lineRequest
    ) {
        LineResponse lineResponse = lineService.updateLine(lineId, lineRequest.getName(), lineRequest.getColor());
        return ResponseEntity.ok(URI.create("/lines/" + lineResponse.getId()));
    }

    @DeleteMapping("/{lineId}")
    public ResponseEntity deleteLine(
            @PathVariable("lineId") Long lineId
    ) {
        lineService.deleteLine(lineId);
        return ResponseEntity.noContent().build();
    }
}
