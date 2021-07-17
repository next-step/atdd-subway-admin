package nextstep.subway.section.domain;

import nextstep.subway.common.exception.NotFoundException;

/**
 *
 * @author heetaek.kim
 */
public final class SectionNotFoundContainsStationException extends NotFoundException {

	public SectionNotFoundContainsStationException(Section section) {
		super(section.toString());
	}

}
