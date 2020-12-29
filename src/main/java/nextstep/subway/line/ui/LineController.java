package nextstep.subway.line.ui;

import nextstep.subway.common.NotFoundException;
import nextstep.subway.line.application.LineService;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.NewLineRequest;
import nextstep.subway.line.dto.NewLineResponse;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/lines")
public class LineController {
	private final LineService lineService;

	public LineController(final LineService lineService) {
		this.lineService = lineService;
	}

	@PostMapping
	public ResponseEntity createLine(@RequestBody NewLineRequest lineRequest) {
		NewLineResponse line = lineService.saveLine_new(lineRequest);
		return ResponseEntity.created(URI.create("/lines/" + line.getId())).body(line);
	}

	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity showLine() {
		return ResponseEntity.ok().body(lineService.findAllLine_new());
	}

	@GetMapping("/{id}")
	public ResponseEntity<NewLineResponse> showLine(@PathVariable Long id) {
		return ResponseEntity.ok(lineService.findLineById_new(id));
	}

	@PutMapping("/{id}")
	public ResponseEntity updateLine(@RequestBody LineRequest lineRequest,
	                                 @PathVariable Long id) {
		lineService.updateLineById(id, lineRequest);
		return ResponseEntity.ok().build();
	}

	@DeleteMapping("/{id}")
	public ResponseEntity deleteLine(@PathVariable Long id) {
		lineService.deleteLineById(id);
		return ResponseEntity.noContent().build();
	}

	@ExceptionHandler(DataIntegrityViolationException.class)
	public ResponseEntity handleIllegalArgsException(DataIntegrityViolationException e) {
		return ResponseEntity.badRequest().build();
	}

	@ExceptionHandler(NotFoundException.class)
	public ResponseEntity handleLineNotFoundException(NotFoundException e) {
		return ResponseEntity.notFound().build();
	}
}
