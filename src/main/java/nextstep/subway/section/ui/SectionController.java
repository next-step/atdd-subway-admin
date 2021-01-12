package nextstep.subway.section.ui;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import nextstep.subway.section.application.SectionService;
import nextstep.subway.section.dto.SectionRequest;

@RestController
public class SectionController {
	private final SectionService sectionService;

	public SectionController(SectionService sectionService) {
		this.sectionService = sectionService;
	}

	@PostMapping("/lines/{lineId}/sections")
	public ResponseEntity<Void> addSection(@PathVariable Long lineId, @RequestBody SectionRequest sectionRequest) {
		sectionService.saveSection(lineId, sectionRequest);
		return ResponseEntity.ok().build();
	}

	@DeleteMapping("/lines/{lineId}/sections")
	public ResponseEntity<Void> removeLineStation(@PathVariable Long lineId, @RequestParam Long stationId) {
		sectionService.removeSectionByStationId(lineId, stationId);
		return ResponseEntity.ok().build();
	}

	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<Void> handleIllegalArgsException(IllegalArgumentException e) {
		return ResponseEntity.badRequest().build();
	}

	@ExceptionHandler(DataIntegrityViolationException.class)
	public ResponseEntity<Void> handleIllegalArgsException(DataIntegrityViolationException e) {
		return ResponseEntity.badRequest().build();
	}
}
