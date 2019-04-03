package com.management.admin.apiController;

import com.github.kevinsawicki.http.HttpRequest;
import com.management.admin.biz.ICarService;
import com.management.admin.entity.JsonResult;
import com.management.admin.entity.db.Car;
import com.management.admin.utils.web.SessionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping(value = "api/car")
public class CarApiController {
    @Autowired
    private ICarService iCarService;

    /**
     * 根据用户ID查找用户的购物车 钉子 2019年3月11日17:51:55
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "getAllCarByUserId")
    public JsonResult getAllCarByUserId(HttpServletRequest request) {
        Integer userId = SessionUtil.getSession(request).getUserId();
        List<Car> list = iCarService.selectCarByUserId(userId);
        return new JsonResult(200, list);
    }

    /**
     * 用户添加商品到购物车 钉子 2019年3月11日17:55:41
     *
     * @param productId
     * @param request
     * @return
     */
    @RequestMapping(value = "insertCar")
    public JsonResult insertCar(String productId, HttpServletRequest request) {
        // TODO 用户编号写死
        /* Integer userId= SessionUtil.getSession( request).getUserId();*/
        Car car = new Car();
        car.setProductId(Long.valueOf(productId));
        // 为了测试方便现在直接传用户编号，等项目上线，userId即为token

        car.setUserId(19);
        Integer i = iCarService.insertCar(car);
        if (i > 0) {
            return JsonResult.successful();
        } else {
            return JsonResult.failing();
        }
    }
}
