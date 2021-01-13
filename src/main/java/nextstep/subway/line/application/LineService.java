package nextstep.subway.line.application;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.domain.Section;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.SectionRequest;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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
        Line persistLine = lineRepository.save(request.toLine());

        if (request.getUpStation() != null && request.getDownStation() != null) {
            Station upStation = stationRepository.findById(request.getUpStation()).orElseThrow(() -> new IllegalArgumentException());
            persistLine.addStation(upStation);
            Station downStation = stationRepository.findById(request.getDownStation()).orElseThrow(() -> new IllegalArgumentException());
            persistLine.addStation(downStation);
        }
        final Line result = lineRepository.save(persistLine);
        return LineResponse.of(result);
    }

    public LineResponse saveSection(Long id, SectionRequest sectionRequest) {
        /*
        int count = 0;
        */

        List<Section> newSection = new ArrayList<>();

        Line targetLine = lineRepository.findById(id).orElseThrow(() -> new IllegalArgumentException());

        if (targetLine.getSections().size() == 0) {
            targetLine.addSection(sectionRequest.toSection());
        }

        if (targetLine.getSections().size() >= 1) {
            for (Section sectionValue: targetLine.getSections()) {
                int newDistance = 0;
                //역사이에 새로운 역을 등록
                if (sectionValue.getUpStation() == sectionRequest.getUpStation() && sectionValue.getDownStation() != sectionRequest.getDownStation() && sectionValue.getDistance() > sectionRequest.getDistance()) {
                    newDistance = sectionValue.getDistance() - sectionRequest.getDistance();
                    Section section1 = new Section(sectionValue.getUpStation(), sectionRequest.getDownStation(), sectionRequest.getDistance());
                    Section section2 = new Section(sectionRequest.getDownStation(), sectionValue.getDownStation(), newDistance);
                    newSection.add(section1);
                    newSection.add(section2);
                }
            }
        }
        targetLine.changeSection(newSection);
        final Line line = lineRepository.save(targetLine);
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
}
