SELECT
  STAFF_ID,
  STAFF_NAME,
  POSITION,
  STATE,
  CREATE_TIME
FROM STAFF_INFO
WHERE enabled = :enabled
  #[AND STATE = :state]
  #[AND CREATE_TIME >= :beginDate]
  #[AND CREATE_TIME < :endDate]
  #[AND POSITION in (:positions)]
  #[AND STAFF_NAME LIKE :staffName]
  #[AND STAFF_ID = :staff.staffId]
  #[AND STAFF_ID = :map.staffId]
  #[if(:map['staffName']!=null&&:staffName!=null)AND STAFF_NAME = :map[staffName]-- 单行注释]
  #[AND STAFF_ID = :array[0]/*多行
  注释*/]
  #[:null-- 会消失的单行注释]
  #[:emptyString/*会消失的多行
  注释*/]
  #[:blankSpace]
  #[:eq]
  #[AND 0 != :noteq]
  /*永远出现的
  多行注释*/
  #[:gt]
  #[AND 0 <= :notgt]
  #[:gte]
  #[AND 0 < :notgte]
  #[:lt]
  #[AND 0 >= :notlt]
  #[:lte]
  #[AND 0 > :notlte]
ORDER BY #[CASE STAFF_ID WHEN :map.excellent[0] THEN 0 #[WHEN :map[excellent][1] THEN 1] #[WHEN :map.excellent[2] THEN 2] ELSE 3 END,] STAFF_NAME