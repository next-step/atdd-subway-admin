package nextstep.subway.ui;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import nextstep.subway.application.SectionService;
import nextstep.subway.domain.Section;
import nextstep.subway.dto.LineResponse;

@RestController
@RequestMapping(value = "/sections")
public class SectionController {

	private final SectionService sectionService;

	public SectionController(SectionService sectionService) {
		this.sectionService = sectionService;
	}

	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<Section>> showSections() {
		return ResponseEntity.ok().body(sectionService.findAllSections());
	}
	
	@ExceptionHandler(Exception.class)
	public ResponseEntity<LineResponse> handleIllegalArgsException() {
		return ResponseEntity.badRequest().build();
	}
}
