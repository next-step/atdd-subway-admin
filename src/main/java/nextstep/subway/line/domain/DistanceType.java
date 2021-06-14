package nextstep.subway.line.domain;

public enum DistanceType {
    EXTENDED_DISTANCE((preValue, postValue) -> preValue + postValue),
    DIVIDE_DISTANCE((preValue, postValue) -> preValue - postValue);

    private static final int MINIMUN_NEW_DISTANCE = 0;
    private static final String EXCEPTION_FOR_DISTANCE = "기존 구간 안에 구간 등록시 distance는 기존 구간보다 작아야 합니다.";

    private DistanceFinder finder;

    DistanceType(DistanceFinder finder) {
        this.finder = finder;
    }

    public int calculate(int preDistance, int postDistance) {
        int calculated = finder.measure(preDistance, postDistance);
        if (isIllegalDistance(calculated)) {
            throw new IllegalArgumentException(EXCEPTION_FOR_DISTANCE);
        }
        return calculated;
    }

    private boolean isIllegalDistance(int calculatedDistance) {
        return calculatedDistance <= MINIMUN_NEW_DISTANCE;
    }
}
