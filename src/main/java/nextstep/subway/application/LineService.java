package nextstep.subway.application;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.dto.SectionRequest;

@Service
@Transactional(readOnly = true)
public class LineService {
	private LineRepository lineRepository;
	private StationService stationService;

	public LineService(
		LineRepository lineRepository,
		StationService stationService
	) {
		this.lineRepository = lineRepository;
		this.stationService = stationService;
	}

	@Transactional
	public LineResponse saveLine(LineRequest lineRequest) {
		Line persistLine = lineRepository.save(new Line(
			lineRequest.getName(),
			lineRequest.getColor()
		));

		if(lineRequest.isAddable()) {
			Station upStation = getStationById(lineRequest.getUpStationId());
			Station downStation = getStationById(lineRequest.getDownStationId());

			persistLine.addSection(upStation, downStation, lineRequest.getDistance());
		}

		return LineResponse.of(persistLine);
	}

	public List<LineResponse> findAllLines() {
		List<Line> lines = lineRepository.findAll();
		return lines.stream()
			.map(LineResponse::of)
			.collect(Collectors.toList());
	}

	public LineResponse findById(Long id) {
		Line line = getLineById(id);
		return LineResponse.of(line);
	}

	@Transactional
	public void updateLine(Long id, LineRequest lineRequest) {
		Line line = getLineById(id);
		line.updateLine(lineRequest.getName(), lineRequest.getColor());
	}

	@Transactional
	public void deleteLine(Long id) {
		lineRepository.deleteById(id);
	}

	@Transactional
	public void addSection(Long lineId, SectionRequest sectionRequest) {
		Station upStation = getStationById(sectionRequest.getUpStationId());
		Station downStation = getStationById(sectionRequest.getDownStationId());
		Line line = getLineById(lineId);

		line.addSection(upStation, downStation, sectionRequest.getDistance());
	}

	@Transactional
	public void removeSectionByStationId(Long lineId, Long stationId) {
		Station station = getStationById(stationId);
		Line line = getLineById(lineId);

		line.removeSection(station);
	}

	private Station getStationById(Long stationId) {
		return stationService.findById(stationId);
	}

	private Line getLineById(Long lineId) {
		return lineRepository.findById(lineId).orElseThrow(NoSuchElementException::new);
	}
}
