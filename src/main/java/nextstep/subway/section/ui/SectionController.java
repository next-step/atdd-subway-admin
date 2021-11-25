package nextstep.subway.section.ui;

import nextstep.subway.section.application.SectionService;
import nextstep.subway.section.dto.SectionRequest;
import nextstep.subway.section.dto.SectionResponse;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping(value = "/lines")
public class SectionController {

    private final SectionService sectionService;

    public SectionController(SectionService sectionService) {
        this.sectionService = sectionService;
    }

    @PostMapping(value = "/{lineId}/sections", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity addSection(@PathVariable Long lineId,
                                     @RequestBody SectionRequest request) {
        SectionResponse section = sectionService.addSection(lineId, request);
        return ResponseEntity.created(URI.create(String.format("/lines/%d/sections/%d", lineId, section.getId()))).body(section);
    }

    @DeleteMapping(value = "/{lineId}/sections", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity removeSection(@PathVariable Long lineId,
                                        @RequestParam Long stationId) {

        sectionService.removeSection(lineId, stationId);
        return ResponseEntity.noContent().build();
    }
}
