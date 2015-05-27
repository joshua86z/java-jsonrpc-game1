package com.zhanglong.sg.dao;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import com.zhanglong.sg.entity2.BaseItemShop;
import com.zhanglong.sg.entity2.BaseShopDiscount;

@Repository
public class BaseItemShopDao extends BaseDao2 {

	private static List<BaseItemShop> list;

	@SuppressWarnings("unchecked")
	public List<BaseItemShop> findAll() {

		if (BaseItemShopDao.list == null) {
			Session session = this.getBaseSessionFactory().getCurrentSession();
			BaseItemShopDao.list = session.createCriteria(BaseItemShop.class).list();
		}

		return BaseItemShopDao.list;
	}

	public List<BaseItemShop> findByType(int type) throws CloneNotSupportedException {

		ArrayList<BaseItemShop> result = new ArrayList<BaseItemShop>();
		List<BaseItemShop> list = this.findAll();
		for (BaseItemShop baseItemShop : list) {
			if (baseItemShop.getType() == type) {
				result.add(baseItemShop.clone());
			}
		}
		return result;
	}

	public int getDiscount(int type) {

		Session session = this.getBaseSessionFactory().getCurrentSession();
		@SuppressWarnings("unchecked")
		List<BaseShopDiscount> list = session.createCriteria(BaseShopDiscount.class).list();

		long time = System.currentTimeMillis();

		for (BaseShopDiscount discount : list) {
			if (discount.getType() == type && discount.getBeginTime().getTime() <= time && discount.getEndTime().getTime() > time) {
				return discount.getDiscount();
			}
		}
        return 10;
	}
}
