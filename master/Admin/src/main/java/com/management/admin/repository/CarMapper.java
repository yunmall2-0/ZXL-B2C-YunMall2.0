package com.management.admin.repository;

import com.management.admin.entity.db.Car;
import org.apache.ibatis.annotations.*;

import java.util.List;
@Mapper
public interface CarMapper extends MyMapper<Car> {
    /**
     * 根据用户ID查找用户的购物车  钉子 2019年3月11日14:24:09
     * @param userId
     * @return
     */
    @Select("select * from car where user_id=#{userId} and is_delete=0")
    @Results({
            @Result(property = "productList", column = "product_id",many = @Many(select = "com.management.admin.repository.ProductMapper.selectById"))
    })
    List<Car> selectCarByUserId(Integer userId);

    /**
     * 添加到购物车  钉子 2019年3月11日14:28:03
     * @param car
     * @return
     */
    @Insert("insert into car(user_id,product_id) values(#{userId},#{productId})")
    Integer insertCar(Car car);

    /**
     * 删除购物车 钉子 2019年3月11日17:37:27
     * @param carId
     * @return
     */
    @Update("update car set is_delete=1 where car_id=#{carId}")
    Integer deleteCar(Integer carId);
}
