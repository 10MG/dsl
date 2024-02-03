SELECT
  STAFF_ID,
  STAFF_NAME,
  POSITION,
  STATE,
  CREATE_TIME
FROM STAFF_INFO
WHERE enabled = :enabled
  AND STATE = :state
  AND CREATE_TIME >= :beginDate
  AND CREATE_TIME < :endDate
  AND POSITION in (:positions)
  AND STAFF_NAME LIKE :staffName
  AND STAFF_ID = :staff.staffId
  AND STAFF_ID = :map.staffId
  AND STAFF_NAME = :map[staffName]-- 单行注释
  AND STAFF_ID = :array[0]/*多行
  注释*/
  AND 0 != :noteq
  /*永远出现的
  多行注释*/
  AND 0 <= :notgt
  AND 0 < :notgte
  AND 0 >= :notlt
  AND 0 > :notlte
ORDER BY CASE STAFF_ID WHEN :map.excellent[0] THEN 0 WHEN :map[excellent][1] THEN 1 ELSE 3 END, STAFF_NAME