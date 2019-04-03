/*
package com.management.admin.utils;

import com.management.admin.biz.OrderService;
import com.management.admin.biz.impl.DictionaryServiceImpl;
import com.management.admin.biz.impl.OrderServiceImpl;
import com.management.admin.biz.impl.PayServiceImpl;
import com.management.admin.entity.db.CashbackProgress;
import com.management.admin.entity.db.Dictionary;
import com.management.admin.entity.db.Orders;
import com.management.admin.entity.param.PayParam;
import com.management.admin.repository.CashbackProgressMapper;
import com.management.admin.repository.DictionaryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

*/
/**
 * @ClassName RebateUtil
 * @Description 计算分红分红
 * @Author Timor
 * @Date 2018/12/25 15:16
 * Version 1.0
 **//*


@Component
public class RebateUtil {

    @Autowired
    private DictionaryMapper dictionaryMapper;

    public static RebateUtil rebateUtil;

    @PostConstruct
    public void init(){
        rebateUtil=this;
        rebateUtil.dictionaryMapper=this.dictionaryMapper;

    }


    */
/**
     * 分红百分比 1-6:  0.04
     *//*

    private static final double FIRST_SIXTH_PERCENTAGE=0.04;

    */
/**
     * 分红百分比 7-12： 0.05
     *//*

    private static final double SEVENTH_TWELFTH_PERCENTAGE=0.05;

    */
/**
     * 分红百分比 13-18：  0.06
     *//*

    private static final double  THIRTEENTH_EIGHTEENTH_PERCENTAGE=0.06;

    */
/**
     * 得到积分的常量
     *//*

    private static final double INTEGRAL=1.8;

    */
/**
     * 得到最低销售业绩
     *//*

    private static final double MIN_RESULTS=1.4;

    */
/**
     * 返回分红
     * @return
     *//*

    public static double shareBonus(double consumption,Integer progress){
            Dictionary dictionary=rebateUtil.dictionaryMapper.selectPerformance();
            double performance=Double.parseDouble(dictionary.getValue());
        //第七期期盈利分红
        double seven=10000*1.4*1.4*1.4*1.4*1.4*1.4;
        //第八期期盈利分红
        double eight=seven*1.4;
        //第九期期盈利分红
        double nine=eight*1.4;
        //第十期期盈利分红
        double ten=nine*1.4;
        //第十一期期盈利分红
        double eleven=ten*1.4;
        //第十二期期盈利分红
        double twelve=eleven*1.4;
        //第十三期期盈利分红
        double thirteen=twelve*1.4;
        //第十四期期盈利分红
        double fourteen=thirteen*1.4;
        //第十五期期盈利分红
        double fifteen=fourteen*1.4;
        //第十六期期盈利分红
        double sixteen=fifteen*1.4;
        //第十七期期盈利分红
        double seventeen=sixteen*1.4;
        //第十八期期盈利分红
        double eighteen=seventeen*1.4;

            if(performance>=10000&&progress==1){
                return consumption/2/INTEGRAL*FIRST_SIXTH_PERCENTAGE;
            }else if(performance>=(10000*1.4)  && progress==2){
                return consumption/2/INTEGRAL*FIRST_SIXTH_PERCENTAGE;
            }else if(performance>=(10000*1.4*1.4)  && progress==3){
                return consumption/2/INTEGRAL*FIRST_SIXTH_PERCENTAGE;
            }else if(performance>=(10000*1.4*1.4*1.4)  && progress==4){
                return consumption/2/INTEGRAL*FIRST_SIXTH_PERCENTAGE;
            }else if(performance>=(10000*1.4*1.4*1.4*1.4)  && progress==5){
                return consumption/2/INTEGRAL*FIRST_SIXTH_PERCENTAGE;
            }else if(performance>=(10000*1.4*1.4*1.4*1.4*1.4)  && progress==6){
                return consumption/2/INTEGRAL*FIRST_SIXTH_PERCENTAGE;
            }else if(performance>=seven && progress==7){
                return consumption/2/INTEGRAL*SEVENTH_TWELFTH_PERCENTAGE;
            }else if(performance>=eight && progress==8){
                return consumption/2/INTEGRAL*SEVENTH_TWELFTH_PERCENTAGE;
            }else if(performance>=nine && progress==9){
                return consumption/2/INTEGRAL*SEVENTH_TWELFTH_PERCENTAGE;
            }else if(performance>=ten && progress==10){
                return consumption/2/INTEGRAL*SEVENTH_TWELFTH_PERCENTAGE;
            }else if(performance>=eleven && progress==11){
                return consumption/2/INTEGRAL*SEVENTH_TWELFTH_PERCENTAGE;
            }else if(performance>=twelve && progress==12){
                return consumption/2/INTEGRAL*SEVENTH_TWELFTH_PERCENTAGE;
            }else if(performance>=thirteen && progress==13){
                return consumption/2/INTEGRAL*THIRTEENTH_EIGHTEENTH_PERCENTAGE;
            }else if(performance>=fourteen && progress==14){
                return consumption/2/INTEGRAL*THIRTEENTH_EIGHTEENTH_PERCENTAGE;
            }else if(performance>=fifteen && progress==15){
                return consumption/2/INTEGRAL*THIRTEENTH_EIGHTEENTH_PERCENTAGE;
            }else if(performance>=sixteen && progress==16){
                return consumption/2/INTEGRAL*THIRTEENTH_EIGHTEENTH_PERCENTAGE;
            }else if(performance>=seventeen && progress==17){
                return consumption/2/INTEGRAL*THIRTEENTH_EIGHTEENTH_PERCENTAGE;
            }else if(performance>=eighteen && progress==18){
                return consumption/2/INTEGRAL*THIRTEENTH_EIGHTEENTH_PERCENTAGE;
            }
            return 0;
    }


}
*/
