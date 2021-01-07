package nextstep.subway.line.ui;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import nextstep.subway.line.application.SectionService;
import nextstep.subway.line.dto.SectionRequest;

/**
 * @author : byungkyu
 * @date : 2021/01/06
 * @description :
 **/
@RestController
@RequestMapping("/lines")
public class SectionController {
	private final SectionService sectionService;

	public SectionController(SectionService sectionService) {
		this.sectionService = sectionService;
	}

	@PostMapping("/{id}/sections")
	public ResponseEntity addSection(@PathVariable Long id, @RequestBody SectionRequest sectionRequest) {
		sectionService.addSection(id, sectionRequest);
		return ResponseEntity.ok().build();
	}
}
