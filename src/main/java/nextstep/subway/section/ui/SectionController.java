package nextstep.subway.section.ui;

import nextstep.subway.common.exception.InvalidDuplicatedSection;
import nextstep.subway.common.exception.NegativeNumberDistanceException;
import nextstep.subway.common.exception.NotContainsStationException;
import nextstep.subway.line.application.LineService;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.section.dto.SectionRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SectionController {
    private LineService lineService;

    public SectionController(LineService lineService) {
        this.lineService = lineService;
    }

    @PostMapping("/{lineId}/sections")
    public ResponseEntity addSections(@PathVariable Long lineId, @RequestBody SectionRequest sectionRequest) {
        return ResponseEntity.ok().body(lineService.addSection(lineId, sectionRequest));
    }

    @ExceptionHandler(NegativeNumberDistanceException.class)
    public ResponseEntity<LineResponse> handleNegativeDistanceException(NegativeNumberDistanceException e) {
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler(InvalidDuplicatedSection.class)
    public ResponseEntity<LineResponse> handleInvalidDuplicatedSectionException(InvalidDuplicatedSection e) {
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler(NotContainsStationException.class)
    public ResponseEntity<LineResponse> handleNotContainsStationException(NotContainsStationException e) {
        return ResponseEntity.badRequest().build();
    }
}
