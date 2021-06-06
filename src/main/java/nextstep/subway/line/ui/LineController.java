package nextstep.subway.line.ui;

import java.net.URI;
import java.util.NoSuchElementException;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import nextstep.subway.line.application.LineService;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;

@RestController
@RequestMapping("/lines")
public class LineController {
	private final LineService lineService;

	public LineController(final LineService lineService) {
		this.lineService = lineService;
	}

	@PostMapping
	public ResponseEntity createLine(@RequestBody LineRequest lineRequest) {
		LineResponse line = lineService.saveLine(lineRequest);
		return ResponseEntity.created(URI.create("/lines/" + line.getId())).body(line);
	}

	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity showLines() {
		return ResponseEntity.ok().body(lineService.findAllLines());
	}

	@GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity showLine(@PathVariable Long id) {
		return ResponseEntity.ok().body(lineService.findLine(id));
	}

	@ExceptionHandler(NoSuchElementException.class)
	public ResponseEntity handleNoSuchElementException(NoSuchElementException e) {
		return ResponseEntity.noContent().build();
	}

	@ExceptionHandler(DataIntegrityViolationException.class)
	public ResponseEntity handleDataIntegrityViolationException(DataIntegrityViolationException e) {
		return ResponseEntity.badRequest().build();
	}
}
