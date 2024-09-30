package mes.app.actas_inspec.service;


import mes.domain.DTO.TB_RP621Dto;
import mes.domain.repository.TB_RP620Repository;
import mes.domain.repository.TB_RP621Repository;
import mes.domain.repository.TB_RP622Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class LTSAService {

    @Autowired
    TB_RP620Repository tbRp620Repository;
    @Autowired
    TB_RP621Repository tbRp621Repository;
    @Autowired
    TB_RP622Repository tbRp622Repository;


    /*public Boolean saveProc(TB_RP621Dto dto){

            tbRp621Repository.save();
    }
*/
}
