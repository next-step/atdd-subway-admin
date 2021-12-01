package nextstep.subway.line.ui;

import nextstep.subway.common.exception.DuplicateEntityException;
import nextstep.subway.common.exception.InvalidDuplicatedSection;
import nextstep.subway.common.exception.InvalidEntityRequiredException;
import nextstep.subway.common.exception.NegativeNumberDistanceException;
import nextstep.subway.common.exception.NotContainsStationException;
import nextstep.subway.common.exception.NotFoundEntityException;
import nextstep.subway.common.exception.NotFoundStationException;
import nextstep.subway.line.application.LineService;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.section.dto.SectionRequest;
import nextstep.subway.station.dto.StationResponse;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public ResponseEntity<LineResponse> createLine(@RequestBody LineRequest lineRequest) {
        LineResponse line = lineService.saveLine(lineRequest);
        return ResponseEntity.created(URI.create("/lines/" + line.getId())).body(line);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<LineResponse>> showLines() {
        return ResponseEntity.ok().body(lineService.findAllLines());
    }

    @GetMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LineResponse> showLineById(@PathVariable Long id) {
        return ResponseEntity.ok().body(LineResponse.of(lineService.findLineById(id)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<LineResponse> deleteLine(@PathVariable Long id) {
        lineService.deleteLineById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{lineId}/sections")
    public ResponseEntity<LineResponse> addSection(@PathVariable Long lineId, @RequestBody SectionRequest sectionRequest) {
        return ResponseEntity.ok().body(lineService.addSection(lineId, sectionRequest));
    }

    @ExceptionHandler({
            NegativeNumberDistanceException.class, InvalidDuplicatedSection.class,
            NotContainsStationException.class, DuplicateEntityException.class,
            InvalidEntityRequiredException.class, NotFoundEntityException.class, NotFoundStationException.class
    })
    public ResponseEntity<LineResponse> handleInvalidInputException(RuntimeException e) {
        return ResponseEntity.badRequest().build();
    }
}
