package com.zhanglong.sg.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.googlecode.jsonrpc4j.JsonRpcService;
import com.zhanglong.sg.dao.BaseSpecialCopyDao;
import com.zhanglong.sg.dao.BaseStoryDao;
import com.zhanglong.sg.dao.BattleLogDao;
import com.zhanglong.sg.dao.PowerDao;
import com.zhanglong.sg.entity.BaseSpecialCopy;
import com.zhanglong.sg.entity.BaseStory;
import com.zhanglong.sg.entity.BattleLog;
import com.zhanglong.sg.entity.Hero;
import com.zhanglong.sg.entity.Role;
import com.zhanglong.sg.model.DateNumModel;
import com.zhanglong.sg.model.SpecialCopyModel;
import com.zhanglong.sg.result.Result;

@Service
@JsonRpcService("/specialcopy")
public class SpecialCopyService extends BaseClass {

    public static int MAX_NUM = 3;

	@Resource
	private BaseStoryDao baseStoryDao;

	@Resource
	private BattleLogDao battleLogDao;

    @Resource
    private PowerDao powerDao;

	@Resource
	private BaseSpecialCopyDao baseSpecialCopyDao;

    public Object list() throws Throwable {

    	int roleId = this.roleId();

        List<BaseStory> baseStoryList = this.baseStoryDao.specialCopys();

        List<SpecialCopyModel> copys1 = new ArrayList<SpecialCopyModel>();
        List<SpecialCopyModel> copys2 = new ArrayList<SpecialCopyModel>();
        List<SpecialCopyModel> copys3 = new ArrayList<SpecialCopyModel>();
        List<SpecialCopyModel> copys4 = new ArrayList<SpecialCopyModel>();

        for (int i = 0 ; i < baseStoryList.size() ; i++) {

            if (i == 0) {
                //continue;
            }

            BaseStory baseStory = baseStoryList.get(i);

            int[][] item = this.baseStoryDao.itemIds(baseStory);
            int[] items = new int[item.length];
            for (int j = 0; j < item.length; j++) {
                items[j] = item[j][0];
            }

            SpecialCopyModel specialCopyModel = new SpecialCopyModel();
            //specialCopyModel.setEnable(true);
            specialCopyModel.setId(baseStory.getId());
            specialCopyModel.setLevel(baseStory.getUnlockLevel());
            specialCopyModel.setItems(items);

            if (baseStory.getId() < 100) {
                copys1.add(specialCopyModel);
            } else if (baseStory.getId() < 200) {
                copys2.add(specialCopyModel);
            } else if (baseStory.getId() < 300) {
                copys3.add(specialCopyModel);
            } else if (baseStory.getId() < 400) {
                copys4.add(specialCopyModel);
            }
        }

        DateNumModel dateNum = this.dateNumDao.findOne(roleId);

        List<BaseSpecialCopy> baseList = this.baseSpecialCopyDao.findTodayAll(); 

        boolean enable = false;
        for (BaseSpecialCopy baseSpecialCopy : baseList) {
            if (baseSpecialCopy.getType() == 1) {
                enable = true;
            }
        }

        long coolTime = dateNum.getSpecialCopyCoolTime1() - System.currentTimeMillis();

        HashMap<String, Object> hashMap1 = new HashMap<String, Object>();
        hashMap1.put("num", dateNum.getSpecialCopy1());
        hashMap1.put("numDate", MAX_NUM);
        hashMap1.put("enable", enable);
        hashMap1.put("copys", copys1);
        hashMap1.put("coolTime", coolTime < 0 ? 0 : coolTime);

        enable = false;
        for (BaseSpecialCopy baseSpecialCopy : baseList) {
            if (baseSpecialCopy.getType() == 2) {
                enable = true;
            }
        }

        coolTime = dateNum.getSpecialCopyCoolTime2() - System.currentTimeMillis();
        
        HashMap<String, Object> hashMap2 = new HashMap<String, Object>();
        hashMap2.put("num", dateNum.getSpecialCopy2());
        hashMap2.put("numDate", MAX_NUM);
        hashMap2.put("enable", enable);
        hashMap2.put("copys", copys2);
        hashMap2.put("coolTime", coolTime < 0 ? 0 : coolTime);
        
        enable = false;
        for (BaseSpecialCopy baseSpecialCopy : baseList) {
            if (baseSpecialCopy.getType() == 3) {
                enable = true;
            }
        }

        coolTime = dateNum.getSpecialCopyCoolTime3() - System.currentTimeMillis();

        HashMap<String, Object> hashMap3 = new HashMap<String, Object>();
        hashMap3.put("num", dateNum.getSpecialCopy3());
        hashMap3.put("numDate", MAX_NUM);
        hashMap3.put("enable", enable);
        hashMap3.put("copys", copys3);
        hashMap3.put("coolTime", coolTime < 0 ? 0 : coolTime);

        enable = false;
        for (BaseSpecialCopy baseSpecialCopy : baseList) {
            if (baseSpecialCopy.getType() == 4) {
                enable = true;
            }
        }

        coolTime = dateNum.getSpecialCopyCoolTime4() - System.currentTimeMillis();

        HashMap<String, Object> hashMap4 = new HashMap<String, Object>();
        hashMap4.put("num", dateNum.getSpecialCopy4());
        hashMap4.put("numDate", MAX_NUM);
        hashMap4.put("enable", enable);
        hashMap4.put("copys", copys4);
        hashMap4.put("coolTime", coolTime < 0 ? 0 : coolTime);

        List<HashMap<String, Object>> list = new ArrayList<HashMap<String,Object>>();
        list.add(hashMap1);
        list.add(hashMap2);
        list.add(hashMap3);
        list.add(hashMap4);

        Result result = new Result();
        result.setValue("list", list);
        
        return result.toMap();
    }
//        
////        SpecialCopy.SpecialCopys.Builder specialCopyBuild = SpecialCopy.SpecialCopys.newBuilder();
////        
////        
////        
////        specialCopyBuild.setId(55000);
////        specialCopyBuild.setLevel(1);
////        specialCopyBuild.setMaxNum(5);
////        specialCopyBuild.setNum(3);
////        specialCopyBuild.setType(1);
////        specialCopyBuild.setUnlock(true);
////        specialCopyBuild.addItemIds(90);
////    
////
////        SpecialCopy.SpecialCopyResponse.Builder specialCopyResponseBuilder = SpecialCopy.SpecialCopyResponse.newBuilder();
////        specialCopyResponseBuilder.addSpecialCopys(specialCopyBuild);
////
////
////    //    specialCopyBuild.build().toByteString();
////        
////    //    SpecialCopy.SpecialCopyResponse specialCopyResponse = specialCopyResponseBuilder.build();
////
////        ByteString  data = specialCopyResponseBuilder.build().toByteString();
//
//
//
//    //    SpecialCopy.SpecialCopyResponse specialCopyResponse2 = SpecialCopy.SpecialCopyResponse.parseFrom(ByteString.copyFrom(data.toStringUtf8(), "utf8"));
//
//    //    int i = specialCopyResponse2.getSpecialCopys(0).getId();
//
//    //    System.out.print(i);
//
//
    /**
     * 
     * @param copyId
     * @param heroId1
     * @param heroId2
     * @param heroId3
     * @param heroId4
     * @param power
     * @return
     * @throws Throwable
     */
    public Object battleBegin(int copyId, int heroId1, int heroId2, int heroId3, int heroId4, int power) throws Throwable {

        int roleId = this.roleId();

        BaseStory baseStory = this.baseStoryDao.findOne(copyId, BaseStory.SPECIAL_COPY_TYPE);

        if (baseStory == null) {
            throw new Throwable("没有这关");
        }

        Role role = this.roleDao.findOne(roleId);

        if (role.getPhysicalStrength() < baseStory.getTeamExp()) {
        	throw new Throwable("体力不足");
        }

        List<Hero> heros = this.heroDao.findAll(roleId);
        List<Hero> hero2 = new ArrayList<Hero>();

        boolean find1 = false;
        boolean find2 = false;
        boolean find3 = false;
        boolean find4 = false;
        for (Hero hero : heros) {
            if (heroId1 != 0 && (int)hero.getHeroId() == heroId1) {
                find1 = true;
                hero2.add(hero);
            }
            if (heroId2 != 0 && (int)hero.getHeroId() == heroId2) {
                find2 = true;
                hero2.add(hero);
            }
            if (heroId3 != 0 && (int)hero.getHeroId() == heroId3) {
                find3 = true;
                hero2.add(hero);
            }
            if (heroId4 != 0 && (int)hero.getHeroId() == heroId4) {
                find4 = true;
                hero2.add(hero);
            }
        }

        if (heroId1 != 0 && !find1 || heroId2 != 0 && !find2 || heroId3 != 0 && !find3 || heroId4 != 0 && !find4) {
            throw new Throwable("未拥有的武将 ");
        }

        this.powerDao.save(role, power, heros);

        int num = 0;
        
        DateNumModel dateNum = this.dateNumDao.findOne(roleId);

        long coolTime = 0l;
        
        if (copyId < 100) {

            num = dateNum.getSpecialCopy1();
            coolTime = dateNum.getSpecialCopyCoolTime1();
        } else if (copyId < 200) {

            num = dateNum.getSpecialCopy2();
            coolTime = dateNum.getSpecialCopyCoolTime2();
        } else if (copyId < 300) {

            num = dateNum.getSpecialCopy3();
            coolTime = dateNum.getSpecialCopyCoolTime3();
        } else if (copyId < 400) {

            num = dateNum.getSpecialCopy4();
            coolTime = dateNum.getSpecialCopyCoolTime4();
        }

        if (num >= MAX_NUM) {
            throw new Throwable("关卡攻打次数已用尽");
        } else if (coolTime - System.currentTimeMillis() > 0) {
        	throw new Throwable("冷却时间未结束");
        }

        Result result = new Result();

        result.setValue("num", num);

        int[][] items = this.baseStoryDao.itemIds(baseStory);

        // 先加起来总数 
        num = 0;

        for (int[] is : items) {
            num += is[1];
        }

        int num1 = num * 3 / 10;
        int num2 = num - num1;

        Random random = new Random();
        num = random.nextInt(num1);
        num += num2;

        HashMap<Integer, Integer> map = new HashMap<Integer, Integer>();
        for (int i = 0 ; i < num ; i++) {

            int rank = 0;
            for (int[] is : items) {
                is[2] += rank;
                rank = is[2];
            }

            if (rank <= 0) {
            	break;
            }

            int r = random.nextInt(rank);

            for (int j = 0 ; j < items.length ; j++) {

                int[] ite = items[j];

                int itemRate = ite[2];
                int itemId = ite[0];

                if (r < itemRate) {
                    if (map.get(itemId) != null) {
                        map.put(itemId, map.get(itemId) + 1);
                    } else {
                        map.put(itemId, 1);
                    }
                    ite[1]--;

                    if (ite[1] == 0) {

                        int sub = ite[2];
                        for (int k = j ; k < items.length ; k++) {
                            items[k][2] -= sub;
                        }
                    }
                    break;
                }
            }
        }

        ArrayList<int[]> itemss = new ArrayList<int[]>();
        for (Iterator<Map.Entry<Integer, Integer>> iter = map.entrySet().iterator(); iter.hasNext();) {
            Map.Entry<Integer, Integer> entry = iter.next();
            itemss.add(new int[]{entry.getKey(), entry.getValue()});
        }

        result.setValue("items", itemss);

        BattleLog battleLog = new BattleLog();
        battleLog.setRoleId(roleId);
        battleLog.setGeneralBaseId1(heroId1);
        battleLog.setGeneralBaseId2(heroId2);
        battleLog.setGeneralBaseId3(heroId3);
        battleLog.setGeneralBaseId4(heroId4);
        battleLog.setStoryType(BaseStory.SPECIAL_COPY_TYPE);
        battleLog.setStoryId(copyId);
        
        ObjectMapper objectMapper = new ObjectMapper();
        battleLog.setData(objectMapper.writeValueAsString(itemss));

        this.battleLogDao.create(battleLog);

        result.setValue("battle_id", battleLog.getId());
        return result.toMap();
    }

    /**
     * 
     * @param battleId
     * @param win
     * @return
     * @throws Throwable
     */
    public Object battleEnd(int battleId, Boolean win) throws Throwable {

        int roleId = this.roleId();

        BattleLog battleLog = this.battleLogDao.findOne(battleId);

        if (battleLog == null) {
            throw new Throwable("参数出错,没有此战斗");
        }

        if (battleLog.getBattleResult() != 0) {
            if (battleLog.getData().equals("")) {
                throw new Throwable("战斗已提交");
            } else {
                ObjectMapper objectMapper = new ObjectMapper();
                HashMap<String, Object> object = objectMapper.readValue(battleLog.getData(), new TypeReference<Map<String, Object>>(){});
                return object;
            }
        }

        int battleResult = BattleLog.BATTLE_LOG_LOST;
        if (win) {
            battleResult = BattleLog.BATTLE_LOG_WIN;
        }

        battleLog.setBattleResult(battleResult);
        battleLog.setEndTime((int)(System.currentTimeMillis() / 1000));

        Role role = this.roleDao.findOne(roleId);
        
        Result result = new Result();
        if (!win) {

            role.setPhysicalStrength(role.getPhysicalStrength() - 1);

            this.roleDao.update(role, result);

            ObjectMapper objectMapper = new ObjectMapper();
            battleLog.setData(objectMapper.writeValueAsString(result.toMap()));
            battleLogDao.update(battleLog);
            return result.toMap();
        } else {
            battleLogDao.update(battleLog);
        }

        int todayNum = 0;

        int copyId = battleLog.getStoryId();

        // -------------------------------  扣体力 -----------------------------  //

        BaseStory baseStory = this.baseStoryDao.findOne(copyId, BaseStory.SPECIAL_COPY_TYPE);

    	role.setPhysicalStrength(role.getPhysicalStrength() - baseStory.getTeamExp());
    	this.roleDao.update(role, result);
        // -------------------------------  扣体力 -----------------------------  //

        DateNumModel dateNum = this.dateNumDao.findOne(roleId);

        int type = 1;
        long coolTime = 5l * 60l * 1000l;

        if (copyId < 100) {

                dateNum.setSpecialCopy1(dateNum.getSpecialCopy1() + 1);
                dateNum.setSpecialCopyCoolTime1(coolTime + System.currentTimeMillis());
                this.dateNumDao.save(roleId, dateNum);
                todayNum = dateNum.getSpecialCopy1();

        } else if (copyId < 200) {

        	type = 2;
            dateNum.setSpecialCopy2(dateNum.getSpecialCopy2() + 1);
            dateNum.setSpecialCopyCoolTime2(coolTime + System.currentTimeMillis());
            this.dateNumDao.save(roleId, dateNum);
            todayNum = dateNum.getSpecialCopy2();

        } else if (copyId < 300) {

        	type = 3;
            dateNum.setSpecialCopy3(dateNum.getSpecialCopy3() + 1);
            dateNum.setSpecialCopyCoolTime3(coolTime + System.currentTimeMillis());
            this.dateNumDao.save(roleId, dateNum);
            todayNum = dateNum.getSpecialCopy3();
            
        } else if (copyId < 400) {

        	type = 4;
            dateNum.setSpecialCopy4(dateNum.getSpecialCopy4() + 1);
            dateNum.setSpecialCopyCoolTime4(coolTime + System.currentTimeMillis());
            this.dateNumDao.save(roleId, dateNum);
            todayNum = dateNum.getSpecialCopy4();
        }

        ObjectMapper objectMapper = new ObjectMapper();
        int[][] items = objectMapper.readValue(battleLog.getData(), int[][].class);

        for (int[] is : items) {
            this.itemDao.addItem(roleId, is[0], is[1], result);
        }

        // 三国学院日常任务
        this.dailyTaskDao.addSpecialCopy(role, result);

        result.setValue("num", todayNum);
        result.setValue("type", type);
        result.setValue("coolTime", coolTime);

        battleLog.setData(objectMapper.writeValueAsString(result.toMap()));
        battleLogDao.update(battleLog);

        return result.toMap();
    }
}