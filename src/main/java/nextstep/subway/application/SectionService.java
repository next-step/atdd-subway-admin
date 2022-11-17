package nextstep.subway.application;

import nextstep.subway.domain.*;
import nextstep.subway.dto.SectionListResponse;
import nextstep.subway.dto.SectionRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.Optional;

@Service
@Transactional
public class SectionService {

    private final LineRepository lineRepository;
    private final StationRepository stationRepository;

    public SectionService(LineRepository lineRepository, StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }


    public void createSection(Long lineId, SectionRequest request) {
        Station upStation = stationRepository.findById(request.getUpStationId()).orElseThrow(EntityNotFoundException::new);
        Station downStation = stationRepository.findById(request.getDownStationId()).orElseThrow(EntityNotFoundException::new);
        Line line = addSection(lineId, request, upStation, downStation);
        lineRepository.save(line);
    }

    private Line addSection(Long lineId, SectionRequest request, Station upStation, Station downStation) {
        Line line = getLine(lineId);
        Sections sections = line.toSections();
        sections.insertInsideFromUpStation(upStation, downStation, request.getDistance());
        sections.insertInsideFromDownStation(downStation, upStation, request.getDistance());
        sections.extendFromUpStation(upStation, downStation, request.getDistance());
        sections.extendFromDownStation(upStation, downStation, request.getDistance());
        return line;
    }

    private Line getLine(Long lineId) {
        Optional<Line> lineOptional = lineRepository.findById(lineId);
        if (!lineOptional.isPresent()) {
            throw new IllegalArgumentException("요청하신 line은 존재하지 않습니다");
        }
        return lineOptional.get();
    }

    public SectionListResponse querySections(Long lineId) {
        return null;
    }
}
