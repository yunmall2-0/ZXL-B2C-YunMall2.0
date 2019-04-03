package com.management.admin.biz.impl;

import com.management.admin.biz.ICarService;
import com.management.admin.entity.db.Car;
import com.management.admin.repository.CarMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class CarServiceImpl implements ICarService {
    private final CarMapper carMapper;
    @Autowired
    public CarServiceImpl(CarMapper carMapper) {
        this.carMapper = carMapper;
    }

    /**
     * 根据用户ID查找用户的购物车  钉子 2019年3月11日14:24:09
     *
     * @param userId
     * @return
     */
    @Override
    public List<Car> selectCarByUserId(Integer userId) {
        return carMapper.selectCarByUserId(userId);
    }

    /**
     * 添加到购物车  钉子 2019年3月11日14:28:03
     *
     * @param car
     * @return
     */
    @Override
    public Integer insertCar(Car car) {
        return carMapper.insertCar(car);
    }

    /**
     * 删除购物车 钉子 2019年3月11日17:37:27
     *
     * @param carId
     * @return
     */
    @Override
    public Integer deleteCar(Integer carId) {
        return carMapper.deleteCar(carId);
    }
}
