package com.zhanglong.sg.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import javax.annotation.Resource;

import com.zhanglong.sg.dao.BattleLogDao;
import com.zhanglong.sg.dao.CrusadeDao;
import com.zhanglong.sg.dao.PowerDao;
import com.zhanglong.sg.entity.BattleLog;
import com.zhanglong.sg.entity.FinanceLog;
import com.zhanglong.sg.entity.Hero;
import com.zhanglong.sg.entity.Power;
import com.zhanglong.sg.entity.Role;
import com.zhanglong.sg.model.BattlePlayerModel;
import com.zhanglong.sg.model.CrusadeModel;
import com.zhanglong.sg.model.Reward;
import com.zhanglong.sg.result.ErrorResult;
import com.zhanglong.sg.result.Result;

public class CrusadeService extends BaseClass {

	@Resource
	private PowerDao powerDao;

	@Resource
	private CrusadeDao crusadeDao;

    /**
     * 
     * @return
     * @throws Throwable
     */
    public Object players() throws Throwable {

        int roleId = this.roleId();

        Power power = powerDao.findOne(roleId);

        int p = 4000;
        if (power != null) {
        	p = power.getPower();
        }

        Role role = this.roleDao.findOne(roleId);
        CrusadeModel battleInWorldModel = this.crusadeDao.findOne(roleId, role.level(), p);

        List<HashMap<String, Object>> res = new ArrayList<HashMap<String,Object>>();
        List<BattlePlayerModel> players = battleInWorldModel.getList();

        for (BattlePlayerModel battlePlayerModel : players) {
            
            //Object[] objectList = new Object[battlePlayerModel.getHeros().size()];
            ArrayList<Hero> heros = battlePlayerModel.getHeros();
            Object[] objectList = new Object[heros.size()];
            for (int i = 0 ; i < heros.size() ; i++) {
                objectList[i] = heros.get(i).toArray2();
            }

            List<Reward> rewards = battlePlayerModel.getRewards();

            int[][] items = new int[rewards.size()][];
            for (int i = 0 ; i < rewards.size() ; i++) {
            	for (int j = 0 ; j < rewards.get(i).getItem_id().length ; j++) {

                    int open = 0;
                    if (rewards.get(i).getHas() != null && rewards.get(i).getHas()) {
                        open = 1;
                    }

            		items[i] = new int[]{rewards.get(i).getItem_id()[j] , rewards.get(i).getItem_num()[j] , open};
				}
            }

            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("avatar", battlePlayerModel.getAvatar());
            map.put("name", battlePlayerModel.getName());
            map.put("level", battlePlayerModel.getLevel());
            map.put("rewardNum", battlePlayerModel.getRewardNum());
            map.put("heros", objectList);
            map.put("items", items);
            res.add(map);
        }

        Result result = new Result();
        result.setValue("players", res);
        result.setValue("num", battleInWorldModel.getNum());
        result.setValue("myhp", battleInWorldModel.getHpMap());
        result.setValue("mycd", battleInWorldModel.getCdMap());
        return result.toMap();
    }

    /**
     * 战斗开始
     * @param tokenS
     * @param index
     * @param heroId1
     * @param heroId2
     * @param heroId3
     * @param heroId4
     * @param power
     * @return
     * @throws Throwable
     */
    public Object battleBegin(String tokenS, int index, int heroId1, int heroId2, int heroId3, int heroId4, int power) throws Throwable {

        if (index <= 0 || index > 15) {
            throw new Throwable("参数出错");
        }

        int roleId = this.roleId();

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
            throw new Throwable("未拥有的武将");
        }

        Role role = this.roleDao.findOne(roleId);

        this.powerDao.save(role, power, hero2);

        Power p = this.powerDao.findOne(roleId);

        CrusadeModel battleInWorldModel = this.crusadeDao.findOne(roleId, role.level(), p.getPower());
        
        if (index > 1) {

            BattlePlayerModel player = battleInWorldModel.getList().get(index - 2);
            List<Hero> heros2 = player.getHeros();
            for (Hero hero : heros2) {
                if (hero.getHp() == null || hero.getHp() > 0) {
                    throw new Throwable("你丫前一关还没赢呢！ ");
                }
            }
        }

        BattleLogDao battleLogDao = new BattleLogDao();

        BattleLog battleLog = new BattleLog();
        battleLog.setRoleId(roleId);
        battleLog.setGeneralBaseId1(heroId1);
        battleLog.setGeneralBaseId2(heroId2);
        battleLog.setGeneralBaseId3(heroId3);
        battleLog.setGeneralBaseId4(heroId4);
        battleLog.setStoryType(4);
        battleLog.setStoryId(index);

        battleLog = battleLogDao.create(battleLog);

        ArrayList<Hero> heros2 = battleInWorldModel.getList().get(index - 1).getHeros();

        List<Object> list = new ArrayList<Object>();
        for (Hero hero : heros2) {
			list.add(hero.toArray2());
		}

        Result result = new Result();
        result.setValue("battle_id", battleLog.getId());
        result.setValue("myhp", battleInWorldModel.getHpMap());
        result.setValue("mycd", battleInWorldModel.getCdMap());
        result.setValue("heHeros", list);
        return result.toMap();
    }

    /**
     * 
     * @param battleId
     * @param myHp1
     * @param myHp2
     * @param myHp3
     * @param myHp4
     * @param hp1
     * @param hp2
     * @param hp3
     * @param hp4
     * @param mytime1
     * @param mytime2
     * @param mytime3
     * @param mytime4
     * @param time1
     * @param time2
     * @param time3
     * @param time4
     * @return
     * @throws Throwable
     */
    public Object battleEnd(int battleId, int myHp1, int myHp2, int myHp3, int myHp4, int hp1, int hp2, int hp3, int hp4, float mytime1, float mytime2, float mytime3, float mytime4, float time1, float time2, float time3, float time4) throws Throwable {

        int roleId = this.roleId();

        BattleLogDao battleLogDao = new BattleLogDao();
        BattleLog battleLog = battleLogDao.findOne(battleId);

        if (battleLog == null) {
            throw new Throwable("非法提交木有这个battleId");
        }

        if (battleLog.getStoryType() != 4) {
            throw new Throwable("非法提交木有这个battleId");
        }

        Role role = this.roleDao.findOne(roleId);

        Power p = this.powerDao.findOne(roleId);

        CrusadeModel battleInWorldModel = this.crusadeDao.findOne(roleId, role.level(), p.getPower());

        if (battleLog.getGeneralBaseId1() != 0) {
        	battleInWorldModel.getHpMap().put(battleLog.getGeneralBaseId1(), myHp1);
        	battleInWorldModel.getCdMap().put(battleLog.getGeneralBaseId1(), mytime1);
        }
        if (battleLog.getGeneralBaseId2() != 0) {
        	battleInWorldModel.getHpMap().put(battleLog.getGeneralBaseId2(), myHp2);
        	battleInWorldModel.getCdMap().put(battleLog.getGeneralBaseId2(), mytime2);
        }
        if (battleLog.getGeneralBaseId3() != 0) {
        	battleInWorldModel.getHpMap().put(battleLog.getGeneralBaseId3(), myHp3);
        	battleInWorldModel.getCdMap().put(battleLog.getGeneralBaseId3(), mytime3);
        }
        if (battleLog.getGeneralBaseId4() != 0) {
        	battleInWorldModel.getHpMap().put(battleLog.getGeneralBaseId4(), myHp4);
        	battleInWorldModel.getCdMap().put(battleLog.getGeneralBaseId4(), mytime4);
        }

        BattlePlayerModel player = battleInWorldModel.getList().get(battleLog.getStoryId() - 1);

        ArrayList<Hero> heroList = player.getHeros();

        for (int i = 0 ; i < heroList.size() ; i++) {
            Hero hero = heroList.get(i);
            if (i == 0) {
                hero.setHp(hp1);
                hero.setCd(time1);
            } else if (i == 1) {
                hero.setHp(hp2);
                hero.setCd(time2);
            } else if (i == 2) {
                hero.setHp(hp3);
                hero.setCd(time3);
            } else if (i == 3) {
                hero.setHp(hp4);
                hero.setCd(time4);
            }
        }

        boolean isWin = false;
        if (hp1 == 0 && hp2 == 0 && hp3 == 0 && hp4 == 0 && player.getRewards().size() == 0) {
        	isWin = true;
            player.setRewards(this.initReward(battleLog.getStoryId()));
        }

        this.crusadeDao.save(roleId, battleInWorldModel);

        int coin = (int)this.configs().get(battleLog.getStoryId() - 1)[0];

        coin  = (int) (coin * (0.8 + (role.level() - 30) * 0.02));

        int money4 = (int)this.configs().get(battleLog.getStoryId() - 1)[1];

        Result result = new Result();
        
        if (isWin) {
            this.roleDao.addCoin(role, coin, "讨伐天下第<" + battleLog.getStoryId() + ">关", 0, result);

            if (battleInWorldModel.getNum() < 2) {
                role.setMoney4(role.getMoney4() + money4);
                result.addRandomItem(new int[]{4 , money4});
                this.roleDao.update(role, result);
            }
        } else {
        	result.setValue("random_result", new int[]{});
        }

        List<HashMap<String, Object>> res = new ArrayList<HashMap<String,Object>>();
        List<BattlePlayerModel> players = battleInWorldModel.getList();

        for (BattlePlayerModel battlePlayerModel : players) {

            ArrayList<Hero> heros = battlePlayerModel.getHeros();
            Object[] objectList = new Object[heros.size()];
            for (int i = 0 ; i < heros.size() ; i++) {
                objectList[i] = heros.get(i).toArray2();
            }

            List<Reward> rewards = battlePlayerModel.getRewards();

            int[][] items = new int[rewards.size()][];
            for (int i = 0 ; i < rewards.size() ; i++) {
            	for (int j = 0 ; j < rewards.get(i).getItem_id().length ; j++) {

                    int open = 0;
                    if (rewards.get(i).getHas() != null && rewards.get(i).getHas()) {
                        open = 1;
                    }

            		items[i] = new int[]{rewards.get(i).getItem_id()[j] , rewards.get(i).getItem_num()[j] , open};
				}
            }

            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("avatar", battlePlayerModel.getAvatar());
            map.put("name", battlePlayerModel.getName());
            map.put("level", battlePlayerModel.getLevel());
            map.put("rewardNum", battlePlayerModel.getRewardNum());
            map.put("heros", objectList);
            map.put("items", items);
            res.add(map);
        }

        result.setValue("players", res);
        result.setValue("num", battleInWorldModel.getNum());
        result.setValue("myhp", battleInWorldModel.getHpMap());
        result.setValue("mycd", battleInWorldModel.getCdMap());

        // 讨伐天下日常任务
        this.dailyTaskDao.addCrusade(role, result);

        return result.toMap();
    }

    /**
     * 
     * @param tokenS
     * @return
     * @throws Throwable
     */
    public Object reset(String tokenS) throws Throwable {
    	
    	int roleId = this.roleId();

        Role role = this.roleDao.findOne(roleId);

        Power power = this.powerDao.findOne(roleId);

        int p = 4000;
        if (power != null) {
        	p = power.getPower();
        }

        CrusadeModel crusadeModel = this.crusadeDao.findOne(roleId, role.level(), p);

        if (crusadeModel.getNum() >= 2) {
        	throw new Throwable("重置两次不能再重置");
        }

        crusadeModel.newPlayers(role.level(), p);

        if (role.getVip() < 8) {
        	//
        }

        crusadeModel.setNum(crusadeModel.getNum() + 1);
        crusadeModel.setHpMap(new HashMap<Integer, Integer>());
        crusadeModel.setCdMap(new HashMap<Integer, Float>());
        this.crusadeDao.save(roleId, crusadeModel);

        return this.players();
    }

    /**
     * 
     * @param index 关卡位置
     * @param index2 宝箱位置
     * @return
     * @throws Throwable
     */
    public Object getReward(int index, int index2) throws Throwable {

        if (index <= 0 || index > 15) {
            throw new Throwable("参数出错");
        }

        if (index2 <= 0 || index2 > 4) {
            throw new Throwable("参数出错");
        }
        
        index--;
        index2--;

        int roleId = this.roleId();

        Power p = this.powerDao.findOne(roleId);

        Role role = this.roleDao.findOne(roleId);

        CrusadeModel crusadeModel = this.crusadeDao.findOne(roleId, role.level(), p.getPower());
        BattlePlayerModel player = crusadeModel.getList().get(index);
        List<Hero> heros2 = player.getHeros();
        for (Hero hero : heros2) {
            if (hero.getHp() == null || hero.getHp() > 0) {
                throw new Throwable("你丫还没赢呢 ");
            }
        }
        
        Result result = new Result();
        List<Reward> rewards = player.getRewards();
//        if (reward.getHas()) {
//        	throw new Throwable("你丫已经抽过这个宝箱了啊");
//        }
        int n = 0;
        for (int i = 0 ; i < rewards.size() ; i++) {
        	if (rewards.get(i).getHas() != null && rewards.get(i).getHas()) {
        		n++;
        		if (i == index2) {
        			throw new Throwable("你丫已经抽过这个宝箱了啊");
        		}
        	}
		}

        rewards.get(index2).setHas(true);
        
//        int n = player.getRewardNum();
//        if (n >= 4) {
//            throw new Throwable("你丫抽了4次还要抽啊");
//        }
        if (n > 0) {
            int gold = 0;
            if (n == 1) {
                gold = 50;
            } else if (n == 2) {
                gold = 100;
            } else if (n == 3) {
                gold = 150;
            }

            if (role.getGold() < gold) {
                return ErrorResult.NotEnoughGold;
            } else {
                this.roleDao.subGold(role, gold, "第<" + (index + 1) + ">关,第<" + (n + 1) + ">次抽奖", FinanceLog.STATUS_BATTLE_IN_WORLD);
                this.roleDao.update(role, result);
            }
        }

    	for (int j = 0 ; j < rewards.get(index2).getItem_id().length ; j++) {

    		int itemId = rewards.get(index2).getItem_id()[j];
    		int num = rewards.get(index2).getItem_num()[j];
    		
    		this.itemDao.addItem(roleId, itemId, num, result);
		}

        player.setRewardNum(player.getRewardNum() + 1);
        this.crusadeDao.save(roleId, crusadeModel);

        result.setValue("reward_num", player.getRewardNum());
        return result.toMap();
    }

    /**
     * 随机好奖励物品
     * @param index
     * @return
     */
    private List<Reward> initReward(int index) {

        int[][] ite = (int[][])this.configs().get(index - 1)[2];

        ArrayList<int[]> items = new ArrayList<int[]>();
        for (int[] l : ite) {
            items.add(l);
        }

        int count = 4;

        int rate = 0;
        for (int[] is : items) {
            rate += is[2];
            is[2] = rate;
        }

        Random random = new Random();

        List<Reward> list = new ArrayList<Reward>();
        while (true) {
			
            int r = random.nextInt(items.get(items.size() - 1)[2]);

            for (int i = 0 ; i < items.size() ; i++) {
            	 if (r < items.get(i)[2]) {
                     Reward reward = new Reward();

                     reward.setItem_id(new int[]{items.get(i)[0]});
                     reward.setItem_num(new int[]{items.get(i)[1]});

            		 list.add(reward);
            		 
            		 ArrayList<int[]> newItems = new ArrayList<int[]>();
                     for (int j = 0 ; j < items.size() ; j++) {
                         if (j != i) {

                             int[] arr = items.get(j);

                             if (j > i) {
                                 if (i == 0) {
                                     arr[2] -= items.get(i)[2];
                                 } else {
                                     arr[2] -= items.get(i)[2] - items.get(i - 1)[2];
                                 }
                             }

                             newItems.add(arr);
                         }
                     }
                     
                     items = newItems;
            		 break;
            	 }
            }

            if (list.size() >= count) {
            	break;
            }
		}

        return list;
    }

    private ArrayList<Object[]> configs() {

        ArrayList<Object[]> configs = new ArrayList<Object[]>();
        configs.add(new Object[]{3000 , 0 , new int[][]{new int[]{4002, 1, 3} , new int[]{4200 , 1, 10} , new int[]{3067, 1, 5} , new int[]{3052, 1, 10} , new int[]{3017, 1, 32} , new int[]{3000 ,1, 40}}});
        configs.add(new Object[]{5000 , 0 , new int[][]{new int[]{4011, 1, 3} , new int[]{4200 , 1, 10} , new int[]{3068, 1, 5} , new int[]{3053, 1, 10} , new int[]{3018, 1, 32} , new int[]{3001,1, 40}}});
        configs.add(new Object[]{7000 , 100 , new int[][]{new int[]{4004, 1, 3} , new int[]{4200 , 1, 10} , new int[]{3069, 1, 5} , new int[]{3054, 1, 10} , new int[]{3019, 1, 32} , new int[]{3002,1, 40}}});
        configs.add(new Object[]{9000 , 0 , new int[][]{new int[]{4016, 1, 3} , new int[]{4200 , 1, 10} , new int[]{3070 , 1, 5} , new int[]{3055, 1, 10} , new int[]{3020 , 1, 32} , new int[]{3003,1, 40}}});
        configs.add(new Object[]{11000 , 0 , new int[][]{new int[]{4010 , 1, 3} , new int[]{4200 , 1, 10} , new int[]{3071, 1, 5} , new int[]{3056, 1, 10} , new int[]{3021, 1, 32} , new int[]{3004,1, 40}}});
        configs.add(new Object[]{13000 , 200 , new int[][]{new int[]{4004, 1, 3} , new int[]{4201, 1, 10} , new int[]{3072, 1, 5} , new int[]{3057, 1, 10} , new int[]{3022, 1, 32} , new int[]{3005,1, 40}}});
        configs.add(new Object[]{15000 , 0 , new int[][]{new int[]{4015, 1, 3} , new int[]{4201, 1, 10} , new int[]{3073, 1, 5} , new int[]{3058, 1, 10} , new int[]{3023, 1, 32} , new int[]{3006,1, 40}}});
        configs.add(new Object[]{17000 , 0 , new int[][]{new int[]{4004, 1, 3} , new int[]{4201, 1, 10} , new int[]{3074, 1, 5} , new int[]{3059, 1, 10} , new int[]{3024, 1, 32} , new int[]{3007,1, 40}}});
        configs.add(new Object[]{19000 , 300 , new int[][]{new int[]{4010 , 1, 3} , new int[]{4201, 1, 10} , new int[]{3075, 1, 5} , new int[]{3060 , 1, 10} , new int[]{3025, 1, 32} , new int[]{3008,1, 40}}});
        configs.add(new Object[]{21000 , 0 , new int[][]{new int[]{4005, 1, 3} , new int[]{4201, 1, 10} , new int[]{3076, 1, 5} , new int[]{3061, 1, 10} , new int[]{3036, 1, 32} , new int[]{3009,1, 40}}});
        configs.add(new Object[]{23000 , 0 , new int[][]{new int[]{4003, 1, 3} , new int[]{4202, 1, 10} , new int[]{3077, 1, 5} , new int[]{3062, 1, 10} , new int[]{3030 , 1, 32} , new int[]{3010 ,1, 40}}});
        configs.add(new Object[]{25000 , 300 , new int[][]{new int[]{4001, 1, 3} , new int[]{4202, 1, 10} , new int[]{4488, 1, 5} , new int[]{3063, 1, 10} , new int[]{3041, 1, 32} , new int[]{3011,1, 40}}});
        configs.add(new Object[]{27000 , 0 , new int[][]{new int[]{4005, 1, 3} , new int[]{4202, 1, 10} , new int[]{4286, 1, 5} , new int[]{3064, 1, 10} , new int[]{3044, 1, 32} , new int[]{3012,1, 40}}});
        configs.add(new Object[]{29000 , 0 , new int[][]{new int[]{4014, 1, 3} , new int[]{4202, 1, 10} , new int[]{4303, 1, 5} , new int[]{3065, 1, 10} , new int[]{3047, 1, 32} , new int[]{3015,1, 40}}});
        configs.add(new Object[]{31000 , 400 , new int[][]{new int[]{4013, 1, 3} , new int[]{4202, 1, 10} , new int[]{4506, 1, 5} , new int[]{3066, 1, 10} , new int[]{3049, 1, 32} , new int[]{3016,1, 40}}});

        return configs;
    }
}