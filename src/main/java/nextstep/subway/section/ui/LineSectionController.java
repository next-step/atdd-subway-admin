package nextstep.subway.section.ui;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import nextstep.subway.line.application.LineService;
import nextstep.subway.line.domain.LineNotFoundException;
import nextstep.subway.section.dto.SectionRequest;
import nextstep.subway.section.dto.SectionResponse;
import nextstep.subway.station.domain.StationNotFoundException;

@RestController
@RequestMapping("/lines/{lineId}/sections")
public class LineSectionController {

	private final LineService lineService;

	public LineSectionController(LineService lineService) {
		this.lineService = lineService;
	}

	@PostMapping
	public ResponseEntity<SectionResponse> addSection(@PathVariable Long lineId, @RequestBody SectionRequest sectionRequest) throws
			LineNotFoundException,
			StationNotFoundException {
		SectionResponse section = lineService.addSection(lineId, sectionRequest);
		return ResponseEntity.created(uri(lineId, section.getId()))
			.body(section);
	}

	private URI uri(Long lineId, Long sectionId) {
		return URI.create(String.format("/lines/%d/sections/%d", lineId, sectionId));
	}
}
