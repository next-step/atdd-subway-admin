package nextstep.subway.application;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.LineResponse;

@Service
@Transactional(readOnly = true)
public class LineService {
	private final StationRepository stationRepository;
	private final LineRepository lineRepository;

	public LineService(StationRepository stationRepository, LineRepository lineRepository) {
		this.stationRepository = stationRepository;
		this.lineRepository = lineRepository;
	}

	@Transactional
	public LineResponse saveLine(LineRequest lineRequest) {
		Optional<Station> upStation = stationRepository.findById(lineRequest.getUpStationId());
		Optional<Station> downStation = stationRepository.findById(lineRequest.getDownStationId());
		
		Line line = lineRepository.save(lineRequest.toLine(upStation.get(), downStation.get()));
		return LineResponse.of(line);
	}

	public List<LineResponse> findAllLines() {
		List<Line> lines = lineRepository.findAll();

		return lines
				.stream()
					.map(line -> LineResponse.of(line))
					.collect(Collectors.toList());
	}

	public LineResponse findLine(Long id) {
		Optional<Line> line = lineRepository.findById(id);
		return LineResponse.of(line.get());
	}

	public void updateNameAndColor(Long id, LineRequest lineRequest) {
		lineRepository.updateNameAndColor(lineRequest.getName(), lineRequest.getColor(), id);
	}

	public void deleteLine(Long id) {
		lineRepository.deleteById(id);
	}
}
