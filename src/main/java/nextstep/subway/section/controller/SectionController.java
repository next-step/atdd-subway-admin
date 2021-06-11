package nextstep.subway.section.controller;

import nextstep.subway.section.dto.SectionRequest;
import nextstep.subway.section.dto.SectionResponse;
import nextstep.subway.section.service.SectionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RequestMapping("/sections")
@RestController
public class SectionController {
    private final SectionService sectionService;

    public SectionController(SectionService sectionService) {
        this.sectionService = sectionService;
    }

    @PostMapping
    public ResponseEntity createSection(@RequestBody SectionRequest sectionRequest) {
        SectionResponse sectionResponse = sectionService.createSection(sectionRequest);
        return ResponseEntity.created(URI.create("/sections/" + sectionResponse.getId())).build();
    }
}
