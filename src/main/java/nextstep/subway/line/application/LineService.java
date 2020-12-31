package nextstep.subway.line.application;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.domain.Section;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.SectionRequest;
import nextstep.subway.line.dto.SectionResponse;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
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
        if (request.isContainsStation()) {
            Station upStation = stationRepository.findById(request.getUpStationId())
                    .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 역입니다."));
            Station downStation = stationRepository.findById(request.getDownStationId())
                    .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 역입니다."));
            Line persistLine = lineRepository.save(request.toLineWithStation(upStation, downStation));
            return LineResponse.of(persistLine);
        }
        Line persistLine = lineRepository.save(request.toLine());
        return LineResponse.of(persistLine);
    }

    public List<LineResponse> findAllLines() {
        List<Line> lines = lineRepository.findAll();

        return lines.stream()
                .map(LineResponse::of)
                .collect(Collectors.toList());
    }

    public LineResponse findLineById(Long id) {
        return LineResponse.of(lineRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 노선입니다.")));
    }

    public LineResponse updateLine(LineRequest lineRequest, Long id) {
        Line line = lineRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 노선입니다."));
        line.update(lineRequest.toLine());
        return LineResponse.of(line);
    }

    public void deleteLineById(Long id) {
        lineRepository.deleteById(id);
    }

    public SectionResponse addSection(Long lindId, SectionRequest sectionRequest) {
        Line line = lineRepository.findById(lindId).orElseThrow(() -> new EntityNotFoundException("존재하지 않는 노선입니다."));
        Station upStation = stationRepository.findById(sectionRequest.getUpStationId()).orElseThrow(() -> new EntityNotFoundException("존재하지 않는 역입니다."));
        Station downStation = stationRepository.findById(sectionRequest.getDownStationId()).orElseThrow(() -> new EntityNotFoundException("존재하지 않는 역입니다."));
        Section section = line.addSection(upStation, downStation, sectionRequest.getDistance());
        return SectionResponse.of(section);
    }
}
