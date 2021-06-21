package nextstep.subway.section.ui;

import org.springframework.web.bind.annotation.RestController;

import nextstep.subway.section.application.SectionService;

@RestController
public class SectionController {

	private final SectionService service;

	public SectionController(SectionService service) {
		this.service = service;
	}
}
