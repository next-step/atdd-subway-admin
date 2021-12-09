package nextstep.subway.section.ui;

import java.net.URI;
import java.util.List;
import java.util.NoSuchElementException;
import javax.management.OperationsException;
import javax.naming.CannotProceedException;
import nextstep.subway.line.application.LineService;
import nextstep.subway.line.domain.Line;
import nextstep.subway.section.application.SectionService;
import nextstep.subway.section.dto.SectionRequest;
import nextstep.subway.section.dto.SectionResponse;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SectionController {

    private final LineService lineService;
    private final SectionService sectionService;

    public SectionController(final LineService lineService, final SectionService sectionService) {
        this.lineService = lineService;
        this.sectionService = sectionService;
    }

    @PostMapping("/lines/{lineId}/sections")
    public ResponseEntity addSection(
        @PathVariable Long lineId,
        @RequestBody SectionRequest sectionRequest) {
        Line line = lineService.fineById(lineId);
        SectionResponse section = sectionService.createSection(sectionRequest, line);
        return ResponseEntity.created(URI.create("/lines/" + lineId + "/sections/" + section.getId())).body(section);
    }

    @GetMapping("/lines/{lineId}/sections")
    public ResponseEntity showSections(
        @PathVariable Long lineId) {
        Line line = lineService.fineById(lineId);
        List<SectionResponse> sections = sectionService.getSections(line);
        return ResponseEntity.ok().body(sections);
    }

    @DeleteMapping("/lines/{lineId}/sections")
    public ResponseEntity deleteSection(
        @PathVariable Long lineId,
        @RequestParam Long stationId) {
        sectionService.removeSectionByStationId(stationId, lineId);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity handleNoSuchElementException(NoSuchElementException e) {
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity handleIllegalArgumentException(IllegalArgumentException e) {
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity handleRuntimeException(RuntimeException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}
