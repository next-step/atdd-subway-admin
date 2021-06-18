package nextstep.subway.line.ui;

import java.net.URI;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
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

	@GetMapping(value = "/{id}")
	public ResponseEntity getLine(@PathVariable Long id) {
		LineResponse line = lineService.getLine(id);

		return ResponseEntity.ok().body(line);
	}

	@GetMapping
	public ResponseEntity getLines() {
		List<LineResponse> lines = lineService.getLines();

		return ResponseEntity.ok().body(lines);
	}

	@PostMapping
	public ResponseEntity createLine(@RequestBody LineRequest lineRequest) {
		LineResponse line = null;
		try {
			line = lineService.saveLine(lineRequest);
		} catch (DataIntegrityViolationException e) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
		}

		return ResponseEntity.created(URI.create("/lines/" + line.getId())).body(line);
	}

	@PutMapping(value = "/{id}")
	public ResponseEntity updateLine(@PathVariable Long id, @RequestBody LineRequest lineRequest) {
		lineService.updateLine(id, lineRequest);

		return ResponseEntity.ok().build();
	}

	@DeleteMapping(value = "/{id}")
	public ResponseEntity deleteLine(@PathVariable Long id) {
		lineService.deleteLine(id);

		return ResponseEntity.ok().build();
	}

	@PostMapping(value = "/{lineId}/sections")
	public ResponseEntity addSection(@PathVariable Long lineId, @RequestBody SectionRequest sectionRequest) {
		LineResponse lineResponse = lineService.addSection(lineId, sectionRequest);

		return ResponseEntity.ok().body(lineResponse);
	}

	@ExceptionHandler({NoSuchElementException.class, EmptyResultDataAccessException.class})
	public Object notFound(Exception e) {
		return ResponseEntity.notFound().build();
	}
}
