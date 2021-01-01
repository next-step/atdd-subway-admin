package nextstep.subway.line.ui;

import java.net.URI;
import java.util.List;

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
	public ResponseEntity<LineResponse> createLine(@RequestBody LineRequest lineRequest) {
		LineResponse line = lineService.saveLine(lineRequest);
		return ResponseEntity.created(URI.create("/lines/" + line.getId())).body(line);
	}

	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<LineResponse>> showLines() {
		return ResponseEntity.ok().body(lineService.showLines());
	}

	@GetMapping(path = "/{id}")
	public ResponseEntity<LineResponse> showLine(@PathVariable Long id) {
		return ResponseEntity.ok().body(lineService.showLine(id));
	}

	@PutMapping(path = "/{id}")
	public ResponseEntity<LineResponse> updateLine(@PathVariable Long id, @RequestBody LineRequest lineRequest) {
		return ResponseEntity.ok().body(lineService.updateLine(id, lineRequest));
	}

	@DeleteMapping(path = "/{id}")
	public ResponseEntity<LineResponse> deleteLine(@PathVariable Long id) {
		lineService.deleteLine(id);
		return ResponseEntity.noContent().build();
	}

	@ExceptionHandler(RuntimeException.class)
	public ResponseEntity<LineResponse> handleLineRuntimeException(RuntimeException e) {
		return ResponseEntity.badRequest().build();
	}
}
