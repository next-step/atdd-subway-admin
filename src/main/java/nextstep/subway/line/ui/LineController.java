package nextstep.subway.line.ui;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import nextstep.subway.line.application.LineService;
import nextstep.subway.line.dto.AddSectionRequest;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.LineResponses;

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
	public ResponseEntity<List<LineResponse>> readLineList() {
		LineResponses lines = lineService.findLineList();
		return ResponseEntity.ok().body(lines.get());
	}

	@GetMapping("/{id}")
	public ResponseEntity<LineResponse> readLine(@PathVariable("id") Long id) {
		LineResponse line = lineService.findLine(id);
		return ResponseEntity.ok().body(line);
	}

	@PutMapping("/{id}")
	public ResponseEntity<LineResponse> updateLine(@PathVariable("id") Long id, @RequestBody LineRequest lineRequest) {
		lineService.updateLine(id, lineRequest);
		return ResponseEntity.ok().build();
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<LineResponse> deleteLine(@PathVariable("id") Long id) {
		lineService.deleteLine(id);
		return ResponseEntity.noContent().build();
	}

	@PostMapping("/{id}/sections")
	public ResponseEntity<Void> addSection(@PathVariable("id") Long id,
		@RequestBody AddSectionRequest addSectionRequest) {
		lineService.addSection(id, addSectionRequest);
		return ResponseEntity.ok().build();
	}

	@DeleteMapping("/{lineId}/sections")
	public ResponseEntity<Void> deleteLineStation(@PathVariable Long lineId, @RequestParam Long stationId) {
		lineService.deleteSectionByStationId(lineId, stationId);
		return ResponseEntity.ok().build();
	}
}
