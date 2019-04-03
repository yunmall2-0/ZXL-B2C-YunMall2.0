package com.management.admin.biz;

import com.management.admin.entity.db.Car;

import java.util.List;

public interface ICarService {
    /**
     * 根据用户ID查找用户的购物车  钉子 2019年3月11日14:24:09
     * @param userId
     * @return
     */
    List<Car> selectCarByUserId(Integer userId);

    /**
     * 添加到购物车  钉子 2019年3月11日14:28:03
     * @param car
     * @return
     */
    Integer insertCar(Car car);

    /**
     * 删除购物车 钉子 2019年3月11日17:37:27
     * @param carId
     * @return
     */
    Integer deleteCar(Integer carId);
}
