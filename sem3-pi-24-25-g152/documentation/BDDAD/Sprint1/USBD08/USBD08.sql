SELECT DISTINCT O.OPID AS "Operation ID", O.Name AS "Operation Name"
FROM WorkstationTypes W
JOIN OperationWorkstationTypes OW ON OW.WTID = W.WTID
JOIN Operation O ON OW.OPID = O.OPID
ORDER BY O.Name;