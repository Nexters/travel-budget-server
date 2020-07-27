# 개인 방에는, 공용 예산이 없으므로 NULL 허용
ALTER TABLE `travel_budget_db`.`trip_plan` MODIFY budget_id BIGINT NULL;
