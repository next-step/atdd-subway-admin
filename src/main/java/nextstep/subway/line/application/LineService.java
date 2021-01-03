package nextstep.subway.line.application;

import nextstep.subway.common.StationType;
import nextstep.subway.line.domain.*;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.domain.Station;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class LineService {

	private LineRepository lineRepository;

	private LineStationRepository lineStationRepository;

	public LineService(LineRepository lineRepository, LineStationRepository lineStationRepository) {
		this.lineRepository = lineRepository;
		this.lineStationRepository = lineStationRepository;
	}

	public List<LineResponse> listLine() {
		List<Line> findLines = lineRepository.findAll();
		return findLines.stream().map(LineResponse::of).collect(Collectors.toList());
	}

	public LineResponse findLineById(Long id) {
		Line line = lineRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("해당 노선이 없습니다 id=" + id));
		return LineResponse.of(line);
	}

	@Transactional
	public void update(LineRequest lineRequest) {
		Line line = lineRepository.findById(lineRequest.getId()).orElseThrow(() -> new IllegalArgumentException("해당 노선이 없습니다 id=" + lineRequest.getId()));
		line.update(lineRequest.toLine());
		lineRepository.save(line);
	}

	@Transactional
	public void delete(Long id) {
		Line line = lineRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("해당 노선이 없습니다 id=" + id));
		lineRepository.delete(line);
	}

	@Transactional
	public LineResponse saveLine(LineRequest lineRequest, Station upStation, Station downStation) {
		Line line = lineRepository.save(new Line(lineRequest.getName(), lineRequest.getColor()));
//		List<LineStation> lineStations  = lineStationRepository.saveAll(Arrays.asList(new LineStation(line, upStation, StationType.UP_STATION), new LineStation(line, downStation, StationType.)));
		List<LineStation> lineStations  = lineStationRepository.saveAll(Arrays.asList(new LineStation(line, upStation, new Section(upStation.getId(), downStation.getId(), lineRequest.getDistance())), new LineStation(line, downStation, new Section(upStation.getId(), downStation.getId(), lineRequest.getDistance()))));
		return LineResponse.of(line, lineStations);
	}
}
