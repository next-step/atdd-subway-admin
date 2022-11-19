package nextstep.subway.application;

import nextstep.subway.domain.*;
import nextstep.subway.dto.SectionRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;

@Service
@Transactional
public class SectionService {

    private static final String MESSAGE_CAN_NOT_ADD_SECTION = "구간을 등록하지 못했습니다";
    private final LineService lineService;
    private final LineRepository lineRepository;
    private final StationRepository stationRepository;

    public SectionService(LineService lineService, LineRepository lineRepository, StationRepository stationRepository) {
        this.lineService = lineService;
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }


    public void createSection(Long lineId, SectionRequest request) {
        Station upStation = stationRepository.findById(request.getUpStationId()).orElseThrow(EntityNotFoundException::new);
        Station downStation = stationRepository.findById(request.getDownStationId()).orElseThrow(EntityNotFoundException::new);
        Line line = lineService.findLineOrThrowException(lineId);
        addRequestSection(upStation, downStation, request.getDistance(), line);
        lineRepository.save(line);
    }

    private void addRequestSection(Station upStation, Station downStation, Long distance, Line line) {
        boolean extended = line.extend(upStation, downStation, distance);
        if (extended) {
            return;
        }
        boolean inserted = line.insert(upStation, downStation, distance);
        if (inserted) {
            return;
        }
        throw new IllegalArgumentException(MESSAGE_CAN_NOT_ADD_SECTION);
    }
}
