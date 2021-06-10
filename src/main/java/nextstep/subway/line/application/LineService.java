package nextstep.subway.line.application;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.exception.LineNotFoundException;
import nextstep.subway.section.domain.Section;
import nextstep.subway.section.domain.SectionRepository;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class LineService {
	private final LineRepository lineRepository;
	private final StationRepository stationRepository;
	private final SectionRepository sectionRepository;

	public LineService(LineRepository lineRepository, StationRepository stationRepository, SectionRepository sectionRepository) {
		this.lineRepository = lineRepository;
		this.stationRepository = stationRepository;
		this.sectionRepository = sectionRepository;
	}

	public LineResponse saveLine(LineRequest request) {
		Line persistLine = lineRepository.save(request.toLine());
		List<Station> stations = stationRepository.findAllById(request.getStationsIds());
		Section section = sectionRepository.save(new Section(stations.get(0), stations.get(1), request.getDistance()));
		section.toLine(persistLine);
		return LineResponse.of(persistLine);
	}

	public List<LineResponse> getLines() {
		return lineRepository.findAll().stream().map(LineResponse::of)
				.collect(Collectors.toList());
	}

	public Line findLineById(Long lineId) {
		return lineRepository.findById(lineId)
				.orElseThrow(() -> new LineNotFoundException(lineId));
	}

	public void modifyLine(Long lineId, LineRequest request) {
		Line line = findLineById(lineId);
		line.update(request.toLine());
	}

	public void deleteLine(Long lineId) {
		Line line = findLineById(lineId);
		lineRepository.delete(line);
	}
}
