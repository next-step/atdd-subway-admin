package nextstep.subway.application;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.dto.SectionRequest;
import nextstep.subway.exception.LineNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class LineService {

	private final StationService stationService;
	private final LineRepository lineRepository;

	public LineService(StationService stationService, LineRepository lineRepository) {
		this.stationService = stationService;
		this.lineRepository = lineRepository;
	}

	public LineResponse saveLine(LineRequest lineRequest) {
		Line line = getLine(lineRequest);

		line = lineRepository.save(line);

		return LineResponse.of(line);
	}

	private Line getLine(LineRequest lineRequest) {
		Station upStation = getStation(lineRequest.getUpStationId());
		Station downStation = getStation(lineRequest.getDownStationId());

		return lineRequest.toLine(upStation, downStation);
	}

	public void updateLine(Long lineId, LineRequest lineRequest) {
		Line line = getLine(lineId);
		line.update(lineRequest.getName(), lineRequest.getColor());
	}

	public void removeLine(Long id) {
		lineRepository.deleteById(id);
	}

	private Line getLine(Long lineId) {
		return lineRepository.findById(lineId)
				.orElseThrow(() -> new LineNotFoundException(lineId));
	}

	public LineResponse addSections(long lineId, SectionRequest request) {
		Line line = getLine(lineId);
		Station upStation = getStation(request.getUpStationId());
		Station downStation = getStation(request.getDownStationId());

		line.addSection(upStation, downStation, request.getDistance());

		return LineResponse.of(line);
	}

	private Station getStation(Long stationId) {
		return stationService.getStation(stationId);
	}

    public void removeSection(Long id, Long stationId) {
		Line line = getLine(id);
		Station station = getStation(stationId);
		line.removeSection(station);
	}
}
