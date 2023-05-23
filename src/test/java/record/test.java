package record;

import com.memory.yun.YunApplication;
import com.memory.yun.service.LabelService;
import com.memory.yun.service.RecordService;
import com.memory.yun.service.TeamService;
import com.memory.yun.util.JsonData;
import com.memory.yun.vo.RecordVO;
import io.netty.util.internal.MacAddressUtil;
import lombok.extern.slf4j.Slf4j;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.*;

/**
 * @Author NJUPT wly
 * @Date 2022/11/26 12:06 上午
 * @Version 1.0
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = YunApplication.class)
@Slf4j
public class test {

    @Autowired
    private RecordService recordService;

    @Autowired
    private LabelService labelService;

    @Autowired
    private TeamService teamService;

    @Test
    public void testAddRecord(){
        RecordVO recordVO = new RecordVO();
        recordVO.setName("1");
        recordVO.setEndTime(new Date());
        recordVO.setLabel(1);
        recordVO.setState(2);
        recordVO.setEndTime(new Date());
        recordVO.setIntroduction("1");
        recordVO.setTeam1(3);
        recordService.add(recordVO);
    }

    @Test
    public void test(){
        Map<Long, List<Long>> map = new HashMap<>();
        List<Long> m = new ArrayList<>();
        m.add(1L);
        m.add(2L);
        map.put(1L,m);
        log.info(map.toString());
    }





}
