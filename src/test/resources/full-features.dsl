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
  #[:null]
  #[:emptyString]
  #[:blankSpace]
  #[:eq]
  #[AND 0 != :noteq]
  #[:gt]
  #[AND 0 <= :notgt]
  #[:gte]
  #[AND 0 < :notgte]
  #[:lt]
  #[AND 0 >= :notlt]
  #[:lte]
  #[AND 0 > :notlte]
ORDER BY STAFF_NAME