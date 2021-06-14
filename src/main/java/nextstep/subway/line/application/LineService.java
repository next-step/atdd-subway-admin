package nextstep.subway.line.application;

import nextstep.subway.exception.DataNotFoundException;
import nextstep.subway.exception.DuplicateDataExistsException;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.section.Section;
import nextstep.subway.section.SectionRequest;
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

    public LineService(LineRepository lineRepository, StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    public LineResponse saveLine(LineRequest request) {
        //1. Line 저장
        Line persistLine = request.toLine();
        if (lineRepository.existsByName(persistLine.getName())) {
            throw new DuplicateDataExistsException("해당 이름을 가진 노선이 이미 존재합니다.");
        }
        //2. Section 데이터 추출
        Section section = createSection(request);

        //3. Line에 Section 데이터 추가
        persistLine.addSection(section);

        //4. Line 데이터 저장(Cascade.ALL 속성으로 Section 데이터도 저장됨)
        persistLine = lineRepository.save(persistLine);

        return LineResponse.of(persistLine);
    }

    private Section createSection(LineRequest request) {
        return createSection(request.getUpStationId(), request.getDownStationId(), request.getDistance());
    }

    private Section createSection(Long upStationId, Long downStationId, int distance) {
        Station upStation = findStationById(upStationId);
        Station downStation = findStationById(downStationId);
        return new Section(upStation, downStation, distance);
    }

    public List<LineResponse> findAllLines() {
        List<Line> lines = lineRepository.findAll();
        return lines.stream()
                .map(LineResponse::of)
                .collect(Collectors.toList());
    }

    public LineResponse findLineById(Long id) {
        return LineResponse.of(findById(id));
    }

    public LineResponse updateLine(Long id, LineRequest lineRequest) {
        Line foundLine = findById(id);
        foundLine.update(lineRequest.toLine());

        return LineResponse.of(foundLine);
    }

    public void deleteLine(Long id) {
        lineRepository.deleteById(id);
    }

    public LineResponse addSection(Long id, SectionRequest request) {
        Line foundLine = findById(id);
        foundLine.addSection(createSection(request.getUpStationId(), request.getDownStationId(), request.getDistance()));

        return LineResponse.of(foundLine);
    }

    private Line findById(Long id) {
        return lineRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("해당하는 지하철 노선을 찾을 수 없습니다."));
    }

    private Station findStationById(Long id) {
        return stationRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("등록되지 않은 지하철 역입니다."));
    }
}
