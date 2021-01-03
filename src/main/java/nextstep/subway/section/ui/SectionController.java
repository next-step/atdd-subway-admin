package nextstep.subway.section.ui;

import lombok.RequiredArgsConstructor;
import nextstep.subway.section.application.SectionService;
import nextstep.subway.section.domain.Section;
import nextstep.subway.section.dto.SectionResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/sections")
public class SectionController {
    private final SectionService sectionService;

    @GetMapping("/{id}")
    public ResponseEntity<SectionResponse> getSection(@PathVariable Long id) {
        Section section = sectionService.findSectionById(id);
        return ResponseEntity.ok(SectionResponse.of(section));
    }
}
