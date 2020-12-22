package nextstep.subway.line.ui;

import java.net.URI;
import java.util.List;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import nextstep.subway.line.application.LineService;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/lines")
public class LineController {

	private final LineService lineService;

	@PostMapping
	public ResponseEntity<LineResponse> createLine(@RequestBody @Valid LineRequest lineRequest) {
		LineResponse line = lineService.saveLine(lineRequest);
		return ResponseEntity.created(URI.create("/lines/" + line.getId())).body(line);
	}

	@GetMapping
	public ResponseEntity<List<LineResponse>> getLines() {
		List<LineResponse> lines = lineService.getLines();
		return ResponseEntity.ok().body(lines);
	}

	@GetMapping("/{id}")
	public ResponseEntity<LineResponse> getLineById(@PathVariable("id") Long id) {
		LineResponse line = lineService.getLineById(id);
		return ResponseEntity.ok().body(line);
	}

	@PutMapping("/{id}")
	public ResponseEntity<LineResponse> updateLine(@PathVariable("id") Long id,
		@RequestBody @Valid LineRequest request) {
		LineResponse line = lineService.updateLine(id, request);
		return ResponseEntity.ok().body(line);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteLine(@PathVariable("id") Long id) {
		lineService.deleteLine(id);
		return ResponseEntity.noContent().build();
	}
}
