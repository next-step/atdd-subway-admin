package nextstep.subway.line.ui;

import nextstep.subway.common.NotFoundException;
import nextstep.subway.common.ValidationException;
import nextstep.subway.line.application.LineService;
import nextstep.subway.line.dto.AddSectionRequest;
import nextstep.subway.line.dto.LineUpdateRequest;
import nextstep.subway.line.dto.LineCreateRequest;
import nextstep.subway.line.dto.LineResponse;
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
	public ResponseEntity createLine(@RequestBody LineCreateRequest lineRequest) {
		LineResponse line = lineService.saveLine(lineRequest);
		return ResponseEntity.created(URI.create("/lines/" + line.getId())).body(line);
	}

	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity showLine() {
		return ResponseEntity.ok().body(lineService.findAllLine());
	}

	@GetMapping("/{id}")
	public ResponseEntity<LineResponse> showLine(@PathVariable Long id) {
		return ResponseEntity.ok(lineService.findLineById(id));
	}

	@PutMapping("/{id}")
	public ResponseEntity updateLine(@RequestBody LineUpdateRequest lineUpdateRequest,
	                                 @PathVariable Long id) {
		lineService.updateLineById(id, lineUpdateRequest);
		return ResponseEntity.ok().build();
	}

	@DeleteMapping("/{id}")
	public ResponseEntity deleteLine(@PathVariable Long id) {
		lineService.deleteLineById(id);
		return ResponseEntity.noContent().build();
	}

	@PostMapping("/{id}/sections")
	public ResponseEntity postSections(@PathVariable Long id,
	                                   @RequestBody AddSectionRequest addSectionRequest) {
		lineService.addSection(id, addSectionRequest);
		return ResponseEntity.ok().build();
	}

	@ExceptionHandler(DataIntegrityViolationException.class)
	public ResponseEntity handleIllegalArgsException(DataIntegrityViolationException e) {
		return ResponseEntity.badRequest().build();
	}

	@ExceptionHandler(NotFoundException.class)
	public ResponseEntity handleLineNotFoundException(NotFoundException e) {
		return ResponseEntity.notFound().build();
	}

	@ExceptionHandler(ValidationException.class)
	public ResponseEntity handleValidationException(ValidationException e) {
		return ResponseEntity.badRequest().build();
	}
}
