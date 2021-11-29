package nextstep.subway.line.application;

import java.util.List;
import java.util.stream.Collectors;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.domain.Section;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.SectionRequest;
import nextstep.subway.station.application.StationService;
import nextstep.subway.station.domain.Station;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class LineService {
	private final LineRepository lineRepository;
	private final StationService stationService;

	public LineService(LineRepository lineRepository, StationService stationService) {
		this.lineRepository = lineRepository;
		this.stationService = stationService;
	}

	public LineResponse saveLine(LineRequest request) {
		Station upStation = stationService.findStationById(request.getUpStationId());
		Station downStation = stationService.findStationById(request.getDownStationId());
		Section section = Section.create(upStation, downStation, request.getDistance());
		Line line = request.toLine();
		line.initSection(section);
		Line savedLine = lineRepository.save(line);
		return LineResponse.of(savedLine);
	}

	@Transactional(readOnly = true)
	public List<LineResponse> findAllLines() {
		return lineRepository.findAll()
			.stream()
			.map(LineResponse::of)
			.collect(Collectors.toList());
	}

	@Transactional(readOnly = true)
	public LineResponse findById(Long id) {
		return LineResponse.of(lineRepository.findById(id)
			.orElseThrow(() -> new IllegalArgumentException("라인이 존재하지 않습니다.")));
	}

	public LineResponse updateById(Long id, LineRequest lineRequest) {
		Line line = lineRepository.findById(id)
			.orElseThrow(() -> new IllegalArgumentException("라인이 존재하지 않습니다."));
		line.update(lineRequest.toLine());
		return LineResponse.of(line);
	}

	public void deleteById(Long id) {
		lineRepository.deleteById(id);
	}

	public LineResponse addSection(Long lineId, SectionRequest sectionRequest) {
		Line line = lineRepository.findById(lineId)
			.orElseThrow(() -> new IllegalArgumentException("라인이 존재하지 않습니다."));
		Station upStation = stationService.findStationById(sectionRequest.getUpStationId());
		Station downStation = stationService.findStationById(sectionRequest.getDownStationId());
		line.addSection(Section.create(upStation, downStation, sectionRequest.getDistance()));
		return LineResponse.of(line);
	}
}
