package nextstep.subway.section.ui;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.section.application.SectionService;
import nextstep.subway.section.dto.SectionRequest;

@RestController
@RequestMapping("/lines")
public class SectionController {

    private final SectionService sectionService;

    public SectionController(final SectionService sectionService) {
        this.sectionService = sectionService;
    }

    @PostMapping(value = "/{id}/sections")
    public ResponseEntity<LineResponse> addSection(@PathVariable("id") Long id, @RequestBody SectionRequest sectionRequest) {
        sectionService.addSection(id, sectionRequest);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @DeleteMapping(value = "/{lineId}/stations/{stationId}")
    public ResponseEntity removeSection(@PathVariable("lineId") Long lineId, @PathVariable("stationId") Long stationId) {
        sectionService.removeSectionByStationId(lineId, stationId);
        return ResponseEntity.noContent().build();
    }
}
