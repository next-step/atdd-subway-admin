package nextstep.subway.section.ui;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import nextstep.subway.line.application.LineService;
import nextstep.subway.line.domain.LineNotFoundException;
import nextstep.subway.section.domain.SectionCannotAddException;
import nextstep.subway.section.domain.SectionDistanceNotEnoughException;
import nextstep.subway.section.dto.SectionRequest;
import nextstep.subway.station.domain.StationNotFoundException;

@RestController
@RequestMapping("/lines/{lineId}/sections")
public class LineSectionController {

	private final LineService lineService;

	public LineSectionController(LineService lineService) {
		this.lineService = lineService;
	}

	@PostMapping
	public ResponseEntity<Void> addSection(@PathVariable Long lineId, @RequestBody SectionRequest sectionRequest) throws
			LineNotFoundException,
			StationNotFoundException,
			SectionCannotAddException,
			SectionDistanceNotEnoughException {
		lineService.addSection(lineId, sectionRequest);
		return ResponseEntity.ok().build();
	}
}
