package mes.app.mobile_CurrentStatus.service;

import lombok.extern.slf4j.Slf4j;
import mes.domain.services.SqlRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class CurrentStatusService {
    @Autowired
    SqlRunner sqlRunner;

    public List<Map<String, Object>> getCurrentStatus(String custCd, String spjangCd) {
        MapSqlParameterSource param = new MapSqlParameterSource();
        // 날짜를 yyyyMMdd 형식으로 포맷
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        String formattedDate = LocalDate.now().format(formatter);

        param.addValue("PS_CUSTCD", custCd);
        param.addValue("PS_SPJANGCD", spjangCd);
        param.addValue("PS_TODAY", formattedDate);

        String sql= """
       SELECT TB_FPLAN.ord_edate,
               TB_FPLAN.cltcd,   
               DBO.DF_NM_RTN('TB_XCLIENT', TB_FPLAN.custcd, TB_FPLAN.cltcd, '', '') cltnm,  
               TB_FPLAN.pcode,
               DBO.DF_NM_RTN('TB_CA501', TB_FPLAN.pcode, '', '', '') pname,
               TB_FPLAN.end_qty qty
       FROM TB_FPLAN WITH(NOLOCK)
       WHERE (TB_FPLAN.custcd     = :PS_CUSTCD)
           AND (TB_FPLAN.spjangcd   = :PS_SPJANGCD)
           AND (TB_FPLAN.ord_edate <= :PS_TODAY)
           AND (TB_FPLAN.cls_flag   = '4')
           AND (TB_FPLAN.orddate + TB_FPLAN.ordnum + TB_FPLAN.ordseq 
               IN (SELECT TB_DA007.orddate + TB_DA007.ordnum + TB_DA007.ordseq 
                 FROM TB_DA007 WITH(NOLOCK) 
       WHERE TB_DA007.custcd = :PS_CUSTCD
          AND TB_DA007.spjangcd = :PS_SPJANGCD
          AND TB_DA007.deldate <= :PS_TODAY
          AND TB_DA007.delflag IN ('0', '1')))
          ORDER BY 1, 2, 4                
          """;

        /*log.info("SQL: {}", sql);
        log.info("Parameters: {}", param.getValues());
*/
        return sqlRunner.getRows(sql, param);
    }

    public String getCustCdByUsername(String username) {
        MapSqlParameterSource param = new MapSqlParameterSource();
        param.addValue("userid", username);

        String sql = """
            SELECT
            custcd AS custCd
            FROM TB_XUSERS WHERE userid = :userid
            """;

        // 결과 조회
        List<Map<String, Object>> rows = sqlRunner.getRows(sql, param);

        // 결과가 존재하면 첫 번째 행의 custCd 반환
        if (rows != null && !rows.isEmpty()) {
            return (String) rows.get(0).get("custCd");
        }

        return null;
    }

}
