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
@Transactional
public class LineService {
	private final LineRepository lineRepository;
	private final StationRepository stationRepository;
	private final SectionRepository sectionRepository;

	public LineService(LineRepository lineRepository, StationRepository stationRepository,
		SectionRepository sectionRepository) {
		this.lineRepository = lineRepository;
		this.stationRepository = stationRepository;
		this.sectionRepository = sectionRepository;
	}

	public LineResponse saveLine(LineRequest request) {
		Station upStation = findStationById(request.getUpStationId());
		Station downStation = findStationById(request.getDownStationId());
		Line savedLine = lineRepository.save(request.toLine());
		sectionRepository.save(Section.create(savedLine, upStation, downStation, request.getDistance()));
		return LineResponse.of(savedLine);
	}

	private Station findStationById(Long stationId){
		return stationRepository.findById(stationId)
			.orElseThrow(() -> new IllegalArgumentException());
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
			.orElseThrow(() -> new IllegalArgumentException("조회 할 대상이 없습니다.")));
	}

	public LineResponse updateById(Long id, LineRequest lineRequest) {
		Line line  = lineRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("조회 할 대상이 없습니다."));
		line.update(lineRequest.toLine());
		return LineResponse.of(line);
	}

	public void deleteById(Long id) {
		lineRepository.deleteById(id);
	}
}
