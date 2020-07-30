# 방의 멤버 권한 컬럼 추가
# 추후에 권한 추가 될 상황을 대비해, OWNER, MEMBER 으로 처리
ALTER TABLE `travel_budget_db`.`trip_member`
    ADD COLUMN authority
        ENUM ('OWNER', 'MEMBER')
        NOT NULL AFTER `id`;

# 공용 방인지, 개인 방인지 처리하는 컬럼 추가
ALTER TABLE `travel_budget_db`.`trip_plan`
    ADD COLUMN is_public
        ENUM ('Y', 'N') NOT NULL DEFAULT 'N'
        AFTER `is_delete`;


# user: payment_case = 1 : N
ALTER TABLE `travel_budget_db`.`payment_case`
    DROP COLUMN `create_user_id`,
    DROP COLUMN `update_user_id`;
