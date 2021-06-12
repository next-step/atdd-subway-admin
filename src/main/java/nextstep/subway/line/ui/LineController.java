package nextstep.subway.line.ui;

import java.net.URI;
import java.util.List;

import javax.persistence.EntityNotFoundException;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import nextstep.subway.line.application.LineService;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.section.dto.SectionRequest;

@RestController
@RequestMapping("/lines")
public class LineController {
	private final LineService lineService;

	public LineController(final LineService lineService) {
		this.lineService = lineService;
	}

	@PostMapping
	public ResponseEntity<LineResponse> createLine(@RequestBody LineRequest lineRequest) {
		LineResponse line = lineService.saveLine(lineRequest);
		return ResponseEntity.created(URI.create("/lines/" + line.getId())).body(line);
	}

	@GetMapping
	public ResponseEntity<List<LineResponse>> showLiens() {
		return ResponseEntity.ok().body(lineService.findAllLines());
	}

	@GetMapping("/{lineId}")
	public ResponseEntity<LineResponse> showLine(@PathVariable("lineId") long lineId) {
		return ResponseEntity.ok().body(lineService.getLine(lineId));
	}

	@PutMapping("/{lineId}")
	public ResponseEntity updateLine(@PathVariable("lineId") long lineId,
		@RequestBody LineRequest lineRequest) {
		this.lineService.updateLine(lineId, lineRequest);
		return ResponseEntity.ok().build();
	}

	@DeleteMapping("/{lineId}")
	public ResponseEntity deleteLine(@PathVariable("lineId") long lineId) {
		this.lineService.deleteLine(lineId);
		return ResponseEntity.noContent().build();
	}

	@PostMapping("/{lineId}/sections")
	public ResponseEntity addSection(@PathVariable("lineId") long lineId, @RequestBody SectionRequest sectionRequest) {
		//TODO 기능 구현
		this.lineService.addSection(lineId, sectionRequest);
		return ResponseEntity.created(URI.create("/lines/" + lineId + "/stations")).body(null);
	}

	@ExceptionHandler(DataIntegrityViolationException.class)
	public ResponseEntity handleIllegalArgsException(DataIntegrityViolationException e) {
		return ResponseEntity.badRequest().build();
	}

	@ExceptionHandler(EntityNotFoundException.class)
	public ResponseEntity handleIllegalArgsException(EntityNotFoundException e) {
		return ResponseEntity.badRequest().build();
	}
}
