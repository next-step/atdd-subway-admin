package nextstep.subway.line.application;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.domain.Section;
import nextstep.subway.line.domain.SectionRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class LineService {
	private final LineRepository lineRepository;
	private final SectionRepository sectionRepository;

	public LineService(LineRepository lineRepository, SectionRepository sectionRepository) {
		this.lineRepository = lineRepository;
		this.sectionRepository = sectionRepository;
	}

	@Transactional
	public LineResponse saveLine(LineRequest request) {
		Line persistLine = lineRepository.save(request.toLine());

		Section endToend = new Section(persistLine, new Station(request.getUpStationId()),
			new Station(request.getDownStationId()),
			request.getDistance());

		sectionRepository.save(endToend);
		return LineResponse.of(persistLine);
	}

	public List<LineResponse> findAllLines() {
		List<Line> lines = lineRepository.findAll();
		return lines.stream()
			.map(LineResponse::of)
			.collect(Collectors.toList());
	}

	@Transactional
	public void deleteLineById(Long id) {
		sectionRepository.deleteAllByLineId(id);
		lineRepository.deleteById(id);
	}

	public LineResponse findLineById(Long id) {
		return LineResponse.of(getLine(id));
	}

	@Transactional
	public void updateLine(Long id, LineRequest lineRequest) {
		Line findLine = getLine(id);
		findLine.update(lineRequest.toLine());
	}

	private Line getLine(Long id) {
		Optional<Line> findLine = lineRepository.findById(id);
		return findLine.orElseThrow(() -> new IllegalArgumentException("없는 노선입니다."));
	}
}
