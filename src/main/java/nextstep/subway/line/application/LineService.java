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
import nextstep.subway.line.dto.SectionRequest;
import nextstep.subway.line.dto.SectionResponse;
import nextstep.subway.station.application.StationService;
import nextstep.subway.station.domain.Station;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class LineService {

	private final LineRepository lineRepository;
	private final SectionRepository sectionRepository;

	/*	Station은 도메인이 다르므로  StationRepository를 바로 접근하지 않고 StationService 계층을 통해서 접근	*/
	private final StationService stationService;

	public LineService(LineRepository lineRepository, SectionRepository sectionRepository,
		StationService stationService) {
		this.lineRepository = lineRepository;
		this.sectionRepository = sectionRepository;
		this.stationService = stationService;
	}

	@Transactional
	public LineResponse saveLine(LineRequest request) {
		Station upStation = stationService.getStationEntity(request.getUpStationId());
		Station downStation = stationService.getStationEntity(request.getDownStationId());

		Line persistLine = lineRepository.save(request.toLine());
		Section endToend = new Section(upStation, downStation, request.getDistance());
		sectionRepository.save(endToend);
		persistLine.addSection(endToend);
		return LineResponse.from(persistLine);
	}

	public List<LineResponse> findAllLineWithSections(){
		List<Line> lines = lineRepository.findAllLinesWithSectionsAndStations();
		return lines.stream()
			.map(LineResponse::from)
			.collect(Collectors.toList());
	}

	@Transactional
	public void deleteLineById(Long id) {
		sectionRepository.deleteAllByLineId(id);
		lineRepository.deleteById(id);
	}

	public LineResponse findLineWithSectionsById(Long id){
		Line line = lineRepository.findLineWithSectionsAndStationsById(id);
		validateExistLine(line);
		return LineResponse.from(line);
	}

	private void validateExistLine(Line line) {
		if(line == null){
			throw new IllegalArgumentException("없는 노선입니다.");
		}
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

	@Transactional
	public SectionResponse addSection(Long lineId, SectionRequest sectionRequest) {
		Line line = lineRepository.findLineWithSectionsAndStationsById(lineId);

		Station upStation = stationService.getStationEntity(sectionRequest.getUpStationId());
		Station downStation = stationService.getStationEntity(sectionRequest.getDownStationId());
		Section section = new Section(upStation, downStation, sectionRequest.getDistance());

		line.addSection(section);
		Section persistenceSection = sectionRepository.save(section);
		return SectionResponse.from(persistenceSection);
	}
}
