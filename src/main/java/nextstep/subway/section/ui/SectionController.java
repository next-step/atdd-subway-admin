package nextstep.subway.section.ui;

import nextstep.subway.section.application.SectionService;
import nextstep.subway.section.dto.SectionRequest;
import nextstep.subway.section.dto.SectionResponse;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
public class SectionController {
    private final SectionService sectionService;

    public SectionController(final SectionService sectionService) {
        this.sectionService = sectionService;
    }

    @PostMapping(value = "/lines/{id}/sections")
    public ResponseEntity<SectionResponse> createSection(@RequestBody SectionRequest sectionRequest, @PathVariable Long id) {
        SectionResponse section = sectionService.saveSection(sectionRequest, id);
        return ResponseEntity.created(URI.create("/sections/" + section.getId())).body(section);
    }

    @GetMapping(value = "/sections", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<SectionResponse>> showSections(){
        return ResponseEntity.ok().body(sectionService.findAllSections());
    }

    @GetMapping(value = "/lines/{id}/sections", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<SectionResponse>> showLineSections(@PathVariable Long id){
        return ResponseEntity.ok().body(sectionService.findSectionsByLineId(id));
    }

    @GetMapping(value = "/sections/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SectionResponse> showSection(@PathVariable Long id) {
        return ResponseEntity.ok().body(sectionService.findById(id));
    }
}
