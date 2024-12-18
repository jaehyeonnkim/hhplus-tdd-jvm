package io.hhplus.tdd.point;

import io.hhplus.tdd.database.UserPointTable;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;

@Service
public class PointService {

    private final UserPointTable userPointTable;
    private static final long MAX_POINT = 1_000_000;

    // 의존성 주입 방식의 생성자
    public PointService(UserPointTable userPointTable) {
        this.userPointTable = userPointTable;
    }

    //포인트 충전
    public UserPoint chargePoints(long point, long userId) throws Exception {
        //포인트가 최대한도거나 0, 음수일 때
        if (point <= 0 || point > MAX_POINT) {
            throw new BadRequestException("INVALID_AMOUNT");
        }

        //유저의 현재포인트
        UserPoint userPoint = userPointTable.selectById(userId);

        //새로운 포인트 잔액 계산
        long newBalance = userPoint.point() + point;

        //잔고 금액이 최대한도를 넘었을 때
        if (newBalance > MAX_POINT){
            throw new BadRequestException("EXCEED_BALANCE");
        }

        //유저 포인트 업데이트
        userPointTable.insertOrUpdate(userId, newBalance);
        return userPoint;
    }

    public UserPoint getBalance(long userId) {
        return userPointTable.selectById(userId);
    }

}
