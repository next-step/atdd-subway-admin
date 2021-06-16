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

    @PostMapping
    public ResponseEntity<LineResponse> createLine(@Valid @RequestBody final LineRequest lineRequest) {
        LineResponse line = lineService.saveLine(lineRequest);
        return ResponseEntity.created(URI.create(LINE + "/" + line.getId())).body(line);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<LineResponse>> showLines() {
        return ResponseEntity.ok().body(lineService.findAllLines());
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LineResponse> showLine(@PathVariable final Long id) {
        return ResponseEntity.ok().body(lineService.findLine(id));
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<String> updateLine(@PathVariable final Long id, @RequestBody final LineRequest lineRequest) {
        lineService.updateLine(id, lineRequest);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLine(@PathVariable final Long id) {
        lineService.deleteLineById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/sections")
    public ResponseEntity<SectionResponse> addSection(@PathVariable Long id, @RequestBody SectionRequest sectionRequest) {
        SectionResponse sectionResponse = lineService.appendSection(id, sectionRequest);

        String uri = String.format("%s/%s%s/%s", LINE, sectionResponse.getLineId(), SECTION, sectionResponse.getId());
        return ResponseEntity.created(URI.create(uri)).body(sectionResponse);
    }

    @GetMapping(value = "/{lineId}/sections/{sectionId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SectionResponse> showSection(@PathVariable Long lineId, @PathVariable Long sectionId) {
        return ResponseEntity.ok().body(lineService.findSection(sectionId));
    }

    @GetMapping(value = "/{lineId}/sections", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<SectionResponse>> showSection(@PathVariable Long lineId) {
        return ResponseEntity.ok().body(lineService.findAllSection());
    }
}
