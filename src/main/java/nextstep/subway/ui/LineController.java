package nextstep.subway.ui;

import java.net.URI;
import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
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

import nextstep.subway.application.LineService;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.dto.SectionRequest;

@RestController
@RequestMapping(value = "/lines")
public class LineController {

	private final LineService lineService;

	public LineController(LineService lineService) {
		this.lineService = lineService;
	}

	@PostMapping
	public ResponseEntity<LineResponse> createLine(@RequestBody LineRequest lineRequest) {
		LineResponse line = lineService.saveLine(lineRequest);
		return ResponseEntity.created(URI.create("/lines/" + line.getId())).body(line);
	}

	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<LineResponse>> showLines() {
		return ResponseEntity.ok().body(lineService.findAllLines());
	}

	@GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<LineResponse> showLine(@PathVariable Long id) {
		return ResponseEntity.ok().body(lineService.findLine(id));
	}

	@PutMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<LineResponse> updateLine(
			@PathVariable Long id, 
			@RequestBody LineRequest lineRequest) {
		lineService.updateNameAndColor(id, lineRequest);
		return ResponseEntity.ok().build();
	}

	@DeleteMapping(value = "/{id}")
	public ResponseEntity<LineResponse> updateLine(@PathVariable Long id) {
		lineService.deleteLine(id);
		return ResponseEntity.noContent().build();
	}
	
	@PostMapping(value = "/{lineId}/sections", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<LineResponse> addSection(
	        @PathVariable Long lineId, 
	        @RequestBody SectionRequest sectionRequest) {
		lineService.addSection(lineId, sectionRequest);
		return ResponseEntity.ok().build();
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<LineResponse> handleIllegalArgsException() {
		return ResponseEntity.badRequest().build();
	}
}
