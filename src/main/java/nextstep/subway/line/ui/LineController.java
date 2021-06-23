package nextstep.subway.line.ui;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import nextstep.subway.line.application.LineService;
import nextstep.subway.line.domain.LineNameDuplicatedException;
import nextstep.subway.line.domain.LineNotFoundException;
import nextstep.subway.line.domain.LineStationDuplicatedException;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.section.domain.SectionRequest;
import nextstep.subway.section.domain.SectionResponse;
import nextstep.subway.station.domain.StationNotFoundException;

@RestController
@RequestMapping("/lines")
public class LineController {

    private final LineService lineService;

    public LineController(final LineService lineService) {
        this.lineService = lineService;
    }

    @PostMapping
    public ResponseEntity<LineResponse> createLine(@RequestBody LineRequest lineRequest) throws
            LineNameDuplicatedException,
            StationNotFoundException,
            LineStationDuplicatedException {
        LineResponse line = lineService.saveLine(lineRequest);
        return ResponseEntity.created(URI.create("/lines/" + line.getId())).body(line);
    }

    @GetMapping
    public ResponseEntity<List<LineResponse>> getLines() {
        return ResponseEntity.ok(lineService.findAllLines());
    }

    @GetMapping("{id}")
    public ResponseEntity<LineResponse> getLineById(@PathVariable Long id) throws LineNotFoundException {
        return ResponseEntity.ok(lineService.findById(id));
    }

    @PutMapping("{id}")
    public ResponseEntity<LineResponse> updateLineById(@PathVariable Long id, @RequestBody LineRequest request) throws LineNotFoundException {
        return ResponseEntity.ok(lineService.updateById(id, request));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<LineResponse> deleteLineById(@PathVariable Long id) throws LineNotFoundException {
        lineService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/sections")
    public ResponseEntity<SectionResponse> addSection(@PathVariable Long id, @RequestBody SectionRequest sectionRequest) throws
            LineNotFoundException, StationNotFoundException {
        SectionResponse section = lineService.addSection(id, sectionRequest);
        String uri = String.format("/lines/%d/sections/%d", id, section.getId());
        return ResponseEntity
            .created(URI.create(uri))
            .body(section);
    }
}
