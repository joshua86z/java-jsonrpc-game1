package com.zhanglong.sg.dao;

import java.util.List;

import javax.annotation.Resource;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import com.zhanglong.sg.entity.Hero;
import com.zhanglong.sg.entity.Role;
import com.zhanglong.sg.result.Result;

@Repository
public class HeroDao extends BaseDao {

	public static int[] MAX_LEVEL = new int[]{10,10,10,10,15,15,15,15,15,15,15,16,16,17,17,18,18,19,19,20,21,22,23,24,25,26,27,28,29,30,31,32,33,34,35,36,37,38,39,40,41,42,43,44,45,46,47,48,49,50,51,52,53,54,55,56,57,58,59,60,61,62,63,64,65,66,67,68,69,70,71,72,73,74,75,76,77,78,79,80,81,82,83,84,85,86,87,88,89,90};

	@Resource
	private BaseHeroDao baseHeroDao;

	public List<Hero> findAll(int roleId) {

		Session session = this.getSessionFactory().getCurrentSession();
		Criteria criteria = session.createCriteria(Hero.class);
		return criteria.add(Restrictions.eq("aRoleId", roleId)).list();
	}

    public Hero findOne(int roleId, int heroId) {

		Session session = this.getSessionFactory().getCurrentSession();
		Criteria criteria = session.createCriteria(Hero.class);
		
		List<Hero> list = criteria.add(Restrictions.eq("aRoleId", roleId)).add(Restrictions.eq("heroId", heroId)).setMaxResults(1).list();
		if (list.size() == 0) {
			return null;
		}

		return list.get(0);
    }

	public void update(Hero hero) {

		Session session = this.getSessionFactory().getCurrentSession();
		session.update(hero);
	}
	
	public void update(Hero hero, Result result) throws Throwable {

		Session session = this.getSessionFactory().getCurrentSession();
		session.update(hero);
		result.addHero(hero);
	}
	
	public void delete(Hero hero) {

		Session session = this.getSessionFactory().getCurrentSession();
		session.delete(hero);
	}

    public Hero create(Role role, int heroId, Result result) throws Throwable {

        Hero general = new Hero();
        general.setARoleId(role.getRoleId());
        general.setHeroId(heroId);
        general.setStar(this.baseHeroDao.findOne(heroId).getStar());
        general.setExp(0);
        general.setStr(0);
        general.setINT(0);
        general.setDex(0);
        general.setPoint(0);
        general.setCLASS(0);
        general.setIsBattle(0);
        general.setSkill1Level(1);
        general.setSkill2Level(1);
        general.setSkill3Level(1);
        general.setSkill4Level(1);
        general.setEquip1(0);
        general.setEquip2(0);
        general.setEquip3(0);
        general.setEquip4(0);
        general.setEquip5(0);
        general.setEquip6(0);

//        TaskDao taskDao = new TaskDao(role);
//
//        taskDao.checkHeroId(heroId, 1, result);
//        taskDao.checkHeroNum(1, result);

        result.addRandomItem(new int[]{heroId, 1});

        Session session = this.getSessionFactory().getCurrentSession();
        session.save(general);

        result.addHero(general);
        return general;
    }

    public int maxExp(int rolelevel) {
    	return 999;
    }
    
    public int soulNumByStar(int heroId) {
    	return 18;
    }

    public void addExp(Hero hero, int rolelevel, int exp, Result result) throws Throwable {
    	hero.setExp(hero.getExp() + exp);
        result.addHero(hero);
    }
}
