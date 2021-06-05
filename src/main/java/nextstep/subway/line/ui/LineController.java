package nextstep.subway.line.ui;

import nextstep.subway.line.application.LineService;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;

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

import java.net.URI;
import java.util.List;

import javax.persistence.EntityNotFoundException;

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

	@GetMapping("/{lineID}")
	public ResponseEntity<LineResponse> showLine(@PathVariable("lineID") long lineID) {
		return ResponseEntity.ok().body(lineService.getLine(lineID));
	}

	@PutMapping("/{lineID}")
	public ResponseEntity updateLine(@PathVariable("lineID") long lineID,
		@RequestBody LineRequest lineRequest) {
		this.lineService.updateLine(lineID,lineRequest);
		return ResponseEntity.ok().build();
	}

	@DeleteMapping("/{lineID}")
	public ResponseEntity deleteLine(@PathVariable("lineID") long lineID) {
		this.lineService.deleteLine(lineID);
		return ResponseEntity.noContent().build();
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
