package nextstep.subway.line.ui;

import lombok.RequiredArgsConstructor;
import nextstep.subway.line.application.LineService;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.section.dto.SectionRequest;
import nextstep.subway.section.dto.SectionResponse;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.net.URI;
import java.util.List;
import static nextstep.subway.PageController.URIMapping.LINE;
import static nextstep.subway.PageController.URIMapping.SECTION;

@RestController
@RequestMapping(LINE)
@RequiredArgsConstructor
public class LineController {
    private final LineService lineService;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<LineResponse>> showLines() {
        return ResponseEntity.ok().body(lineService.findAllLines());
    }

    @GetMapping(value = "/{lineId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LineResponse> showLine(@PathVariable final Long lineId) {
        return ResponseEntity.ok().body(lineService.findLine(lineId));
    }

    @GetMapping(value = "/{lineId}/sections", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<SectionResponse>> showSection(@PathVariable Long lineId) {
        return ResponseEntity.ok().body(lineService.findAllSections());
    }

    @GetMapping(value = "/{lineId}/sections/{sectionId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SectionResponse> showSection(@PathVariable Long lineId, @PathVariable Long sectionId) {
        return ResponseEntity.ok().body(lineService.findSection(sectionId));
    }

    @PostMapping
    public ResponseEntity<LineResponse> createLine(@Valid @RequestBody final LineRequest lineRequest) {
        LineResponse line = lineService.saveLine(lineRequest);
        return ResponseEntity.created(URI.create(LINE + "/" + line.getId())).body(line);
    }

    @PostMapping("/{lineId}/sections")
    public ResponseEntity<SectionResponse> addSection(@PathVariable Long lineId, @RequestBody SectionRequest sectionRequest) {
        SectionResponse sectionResponse = lineService.appendSection(lineId, sectionRequest);

        String uri = String.format("%s/%s%s/%s", LINE, sectionResponse.getLineId(), SECTION, sectionResponse.getId());
        return ResponseEntity.created(URI.create(uri)).body(sectionResponse);
    }

    @PutMapping(value = "/{lineId}")
    public ResponseEntity<String> updateLine(@PathVariable final Long lineId, @RequestBody final LineRequest lineRequest) {
        lineService.updateLine(lineId, lineRequest);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{lineId}")
    public ResponseEntity<Void> deleteLine(@PathVariable final Long lineId) {
        lineService.deleteLineById(lineId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{lineId}/sections")
    public ResponseEntity<Void> deleteSectionByStationId(@PathVariable Long lineId, @RequestParam Long stationId) {
        lineService.deleteSectionByStationId(lineId, stationId);
        return ResponseEntity.noContent().build();
    }

}
