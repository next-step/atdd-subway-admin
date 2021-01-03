package nextstep.subway.line.application;

import nextstep.subway.common.StationType;
import nextstep.subway.line.domain.*;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class LineService {

	private LineRepository lineRepository;

	private StationRepository stationRepository;

	public LineService(LineRepository lineRepository, StationRepository stationRepository) {
		this.lineRepository = lineRepository;
		this.stationRepository = stationRepository;
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
	public LineResponse saveLine(LineRequest lineRequest) {
		Station upStation = stationRepository.findById(lineRequest.getUpStationId()).get();
		Station downStation = stationRepository.findById(lineRequest.getDownStationId()).get();

		Section section = new Section(upStation.getId(), downStation.getId(), lineRequest.getDistance());
		List<LineStation> lineStations = Arrays.asList(new LineStation(upStation, section), new LineStation(downStation, section));
		Line line = new Line(lineRequest.getName(), lineRequest.getColor());
		line.addLineStations(lineStations);
		lineRepository.save(line);
		return LineResponse.of(line);
	}
}
