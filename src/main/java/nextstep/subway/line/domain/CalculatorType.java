package nextstep.subway.line.domain;

import java.util.function.BinaryOperator;

public enum CalculatorType {
    ADD((preValue, postValue) -> preValue + postValue),
    SUBTRACT((preValue, postValue) -> preValue - postValue);

    private static final int MINIMUN_NEW_DISTANCE = 0;
    private static final String EXCEPTION_FOR_DISTANCE = "기존 구간 안에 구간 등록시 distance는 기존 구간보다 작아야 합니다.";

    private BinaryOperator<Integer> operator;

    CalculatorType(BinaryOperator<Integer> operator) {
        this.operator = operator;
    }

    public int calculateDistance(int preDistance, int postDistance) {
        Integer calculated = operator.apply(preDistance, postDistance);
        if (isIllegalDistance(calculated)) {
            throw new IllegalArgumentException(EXCEPTION_FOR_DISTANCE);
        }
        return calculated;
    }

    private boolean isIllegalDistance(int calculatedDistance) {
        return calculatedDistance <= MINIMUN_NEW_DISTANCE;
    }
}
