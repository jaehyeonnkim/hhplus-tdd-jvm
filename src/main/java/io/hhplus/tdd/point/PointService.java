package io.hhplus.tdd.point;

import io.hhplus.tdd.ErrorResponse;
import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.database.PointHistoryTable;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;

@Service
public class PointService {

    private final UserPointTable userPointTable;        //사용자 포인트 데이터
    private final PointHistoryTable pointHistoryTable;  // 포인트 이력 데이터

    private static final long MAX_POINT = 1_000_000;

    // 의존성 주입 방식의 생성자
    public PointService(UserPointTable userPointTable, PointHistoryTable pointHistoryTable) {
        this.userPointTable = userPointTable;
        this.pointHistoryTable = pointHistoryTable;
    }

    //포인트 조회
    public UserPoint getPoint(long id)  {
        //id가 유효하지 않을 때 (0이하일 때)
        if(id <= 0){
            throw new IllegalArgumentException(
                    new ErrorResponse("INVALID_ID", "유효하지 않은 ID입니다.").toString());
        }
        UserPoint userPoint = userPointTable.selectById(id);
        //포인트 정보가 없을 때
        if(userPoint == null){
            throw new NullPointerException(
                    new ErrorResponse("NULL_EXCEPTION", "포인트 정보를 찾을 수 없습니다.").toString());
        }
        return userPoint;
    }

    //포인트 충전
    public UserPoint chargePoints(long point, long id) throws Exception {
        //포인트가 최대한도거나 0, 음수일 때
        if (point <= 0 || point > MAX_POINT) {
            throw new IllegalArgumentException(
                    new ErrorResponse("INVALID_AMOUNT", "충전할 포인트가 유효하지 않습니다").toString());
        }

        //유저의 현재포인트
        UserPoint userPoint = userPointTable.selectById(id);

        //새로운 포인트 잔액 계산
        long newBalance = userPoint.point() + point;

        //잔고 금액이 최대한도를 넘었을 때
        if (newBalance > MAX_POINT){
            throw new IllegalArgumentException(
                    new ErrorResponse("EXCEED_BALANCE", "최대 잔고 금액을 초과할 수 없습니다.").toString());
        }

        //유저 포인트 업데이트
        userPointTable.insertOrUpdate(id, newBalance);
        return userPoint;
    }

    public UserPoint getBalance(long id) {
        return userPointTable.selectById(id);
    }


}
