package nextstep.subway.line.ui;

import static java.lang.String.*;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import nextstep.subway.line.application.SectionService;
import nextstep.subway.line.dto.SectionRequest;

@RestController
public class SectionController {

    private final SectionService sectionService;

    public SectionController(SectionService sectionService) {
        this.sectionService = sectionService;
    }

    @PostMapping(value = "/{lineId}/sections")
    public ResponseEntity addSection(@PathVariable("lineId") Long lindId, @RequestBody SectionRequest sectionRequest) {
        sectionService.addSection(lindId, sectionRequest);
        return ResponseEntity.created(URI.create(format("/%s/sections", lindId))).build();
    }
}
