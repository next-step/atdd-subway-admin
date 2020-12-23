package nextstep.subway.line.application;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import nextstep.subway.common.exception.NotFoundException;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.section.domain.Section;
import nextstep.subway.section.domain.SectionRepository;
import nextstep.subway.section.dto.SectionRequest;
import nextstep.subway.station.application.StationService;
import nextstep.subway.station.domain.Station;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LineService {

	private final LineRepository lineRepository;
	private final StationService stationService;
	private final SectionRepository sectionRepository;

	@Transactional
	public LineResponse saveLine(LineRequest request) {
		Station upStation = stationService.findById(request.getUpStationId());
		Station downStation = stationService.findById(request.getDownStationId());
		Line persistLine = lineRepository.save(
			Line.create(request.getName(), request.getColor(), upStation, downStation, request.getDistance()));

		return LineResponse.of(persistLine);
	}

	public List<LineResponse> getLines() {
		List<Line> lines = lineRepository.findAll();
		return lines.stream()
			.map(LineResponse::of)
			.collect(Collectors.toList());
	}

	public LineResponse getLineById(Long id) {
		Line line = findById(id);
		return LineResponse.of(line);
	}

	@Transactional
	public LineResponse updateLine(Long id, LineRequest request) {
		Line line = findById(id);
		line.update(request);
		return LineResponse.of(line);
	}

	@Transactional
	public void deleteLine(Long id) {
		lineRepository.delete(findById(id));
	}

	private Line findById(Long id) {
		return lineRepository.findById(id)
			.orElseThrow(() -> new NotFoundException("노선 정보를 찾을 수 없습니다."));
	}

	@Transactional
	public LineResponse addSection(long lineId, SectionRequest sectionRequest) {
		Line line = findById(lineId);
		Station upStation = stationService.findById(sectionRequest.getUpStationId());
		Station downStation = stationService.findById(sectionRequest.getDownStationId());
		line.addSection(Section.create(line, upStation, downStation, sectionRequest.getDistance()));
		return LineResponse.of(line);
	}
}
