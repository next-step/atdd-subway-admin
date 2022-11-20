package nextstep.subway.controller;

import java.net.URI;
import java.util.List;
import javassist.NotFoundException;
import nextstep.subway.dto.ErrorResponse;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.dto.SectionRequest;
import nextstep.subway.dto.SectionResponse;
import nextstep.subway.dto.StationResponse;
import nextstep.subway.exception.CannotDeleteException;
import nextstep.subway.service.LineService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/lines")
@RestController
public class LineController {
    private LineService lineService;

    public LineController(LineService lineService) {
        this.lineService = lineService;
    }

    @PostMapping()
    public ResponseEntity<LineResponse> createLine(@RequestBody LineRequest lineRequest) {
        LineResponse line = lineService.saveLine(lineRequest);
        return ResponseEntity.created(URI.create("/lines/" + line.getId())).body(line);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<LineResponse>> showLines() {
        return ResponseEntity.ok().body(lineService.findAllLines());
    }

    @GetMapping("/{id}")
    public ResponseEntity<LineResponse> showLine(@PathVariable Long id) throws NotFoundException {
        return ResponseEntity.ok().body(lineService.findLine(id));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<LineResponse> update(@PathVariable Long id, @RequestBody LineRequest lineRequest) throws NotFoundException {
        LineResponse line = lineService.updateLine(id, lineRequest);
        return ResponseEntity.ok().body(line);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteLine(@PathVariable Long id) throws NotFoundException {
        lineService.deleteLineById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "/{id}/sections")
    public ResponseEntity<SectionResponse> addSection(@PathVariable Long id, @RequestBody SectionRequest sectionRequest) throws NotFoundException {
        SectionResponse section = lineService.addSection(id, sectionRequest);
        return ResponseEntity.created(URI.create(String.format("/lines/%d/sections/", id) + section.getId())).body(section);
    }

    @GetMapping(value = "/{id}/sections", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<SectionResponse>> getLineSections(@PathVariable Long id) throws NotFoundException {
        return ResponseEntity.ok().body(lineService.findLineSections(id));
    }

    @GetMapping(value = "/{lineId}/sections/{sectionId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SectionResponse> getLineSection(@PathVariable Long lineId, @PathVariable Long sectionId) throws NotFoundException {
        return ResponseEntity.ok().body(lineService.findLineSection(lineId, sectionId));
    }

    @GetMapping(value = "/{id}/stations", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<StationResponse>> getLineStations(@PathVariable Long id) throws NotFoundException {
        return ResponseEntity.ok().body(lineService.findLineStations(id));
    }

    @DeleteMapping("/{lineId}/sections")
    public ResponseEntity removeLineStation(
            @PathVariable Long lineId,
            @RequestParam Long stationId) throws NotFoundException {
        lineService.removeSectionByStationId(lineId, stationId);
        return ResponseEntity.ok().build();
    }

    @ExceptionHandler(value = {DataIntegrityViolationException.class, IllegalArgumentException.class,
            CannotDeleteException.class})
    public ResponseEntity<ErrorResponse> handleIllegalArgsException(Exception ex) {
        return ResponseEntity.badRequest().body(new ErrorResponse("BAD_REQUEST", 400, ex.getMessage()));
    }

    @ExceptionHandler(value = {NotFoundException.class})
    public ResponseEntity<ErrorResponse> handleNotFoundExceptionException() {
        return ResponseEntity.notFound().build();
    }

}
