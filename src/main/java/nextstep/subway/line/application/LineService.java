package nextstep.subway.line.application;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.domain.Section;
import nextstep.subway.line.domain.SectionRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.SectionRequest;
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

    private final SectionRepository sectionRepository;

    private final StationRepository stationRepository;

    public LineService(LineRepository lineRepository, SectionRepository sectionRepository, StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.sectionRepository = sectionRepository;
        this.stationRepository = stationRepository;
    }

    public LineResponse saveLine(LineRequest request) {
        Line persistLine = lineRepository.save(request.toLine());

        if (request.getUpStation() != null && request.getDownStation() != null) {
            Station station1 = stationRepository.findById(request.getUpStation()).orElseThrow(IllegalArgumentException::new);
            Station station2 = stationRepository.findById(request.getDownStation()).orElseThrow(IllegalArgumentException::new);
            Section section = new Section(station1, station2, request.getDistance());

            persistLine.addSection(section);
        }
        return LineResponse.of(persistLine);
    }

    // 비즈니스 로직 도메인으로 이동 인자만 전달
    public LineResponse saveSection(Long id, SectionRequest sectionRequest) {
        Line line = lineRepository.findById(id).orElseThrow(IllegalArgumentException::new);
        Station station1 = stationRepository.findById(sectionRequest.getUpStationId()).orElseThrow(IllegalArgumentException::new);
        Station station2 = stationRepository.findById(sectionRequest.getDownStationId()).orElseThrow(IllegalArgumentException::new);
        Section newSection = new Section(station1, station2,sectionRequest.getDistance());
        line.addSection(sectionRepository.save(newSection));
        return LineResponse.of(line);
    }

    @Transactional(readOnly = true)
    public List<LineResponse> findAllLines() {
        List<Line> lines = lineRepository.findAll();

        return lines.stream()
                .map(LineResponse::of)
                .collect(Collectors.toList());
    }

    public LineResponse findById(Long id) {
        Line line = lineRepository.findById(id).orElseThrow(() -> new IllegalArgumentException());
        return LineResponse.of(line);
    }

    public LineResponse updateLineById(Long id, LineRequest request) {
        Line line = lineRepository.findById(id).orElseThrow(() -> new IllegalArgumentException());
        line.changeLine(request.getName(), request.getColor());
        Line persistLine = lineRepository.save(line);
        return LineResponse.of(persistLine);
    }

    public void deleteLineById(Long id) {
        lineRepository.deleteById(id);
    }

    public void deleteSectionById(Long id, Station station) {
        Line line = lineRepository.findById(id).orElseThrow(() -> new IllegalArgumentException());
        line.deleteSection(station);
    }
}
