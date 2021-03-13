package io.jnyou.service.impl;


import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.RandomUtil;
import com.alibaba.nacos.api.config.ConfigService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.jnyou.domain.CashRecharge;
import io.jnyou.domain.Coin;
import io.jnyou.dto.AdminBankDto;
import io.jnyou.dto.UserDto;
import io.jnyou.feign.UserFeignClient;
import io.jnyou.mapper.CashRechargeMapper;
import io.jnyou.model.CashParam;
import io.jnyou.service.CashRechargeService;
import io.jnyou.service.CoinService;
import io.jnyou.vo.CashTradeVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CashRechargeServiceImpl extends ServiceImpl<CashRechargeMapper, CashRecharge> implements CashRechargeService {

    @Autowired
    private UserFeignClient userServiceFeign;

    @Autowired
    private ConfigService configService;

    @Autowired
    private AdminBankServiceFeign adminBankServiceFeign;

    @Autowired
    private CoinService coinService;

    @Autowired
    private Snowflake snowflake;

    /**
     * 分页条件查询充值记录  -- 最多进行一次远程调用
     *
     * @param page      分页参数
     * @param coinId    币种的Id
     * @param userId    用户的Id
     * @param userName  用户的名称
     * @param mobile    用户的手机号
     * @param status    充值的状态
     * @param numMin    充值的最小金额
     * @param numMax    充值的最大金额
     * @param startTime 充值的开始时间
     * @param endTime   充值的截至时间
     * @return
     */
    @Override
    public Page<CashRecharge> findByPage(Page<CashRecharge> page, Long coinId, Long userId, String userName, String mobile,
                                         Byte status, String numMin, String numMax, String startTime, String endTime) {
        LambdaQueryWrapper<CashRecharge> cashRechargeLambdaQueryWrapper = new LambdaQueryWrapper<>();
        // 1 若用户本次的查询中,带了用户的信息userId, userName,mobile ----> 本质就是要把用户的Id 放在我们的查询条件里面
        Map<Long, UserDto> basicUsers = null;
        if (userId != null || !StringUtils.isEmpty(userName) | !StringUtils.isEmpty(mobile)) { // 使用用户的信息查询
            // 需要远程调用查询用户的信息
            basicUsers = userServiceFeign.getBasicUsers(userId == null ? null : Arrays.asList(userId), userName, mobile);
            if (CollectionUtils.isEmpty(basicUsers)) { // 找不到这样的用户->
                return page;
            }
            Set<Long> userIds = basicUsers.keySet();
            cashRechargeLambdaQueryWrapper.in(!CollectionUtils.isEmpty(userIds), CashRecharge::getUserId, userIds);
        }
        // 2 若用户本次的查询中,没有带了用户的信息
        cashRechargeLambdaQueryWrapper.eq(coinId != null, CashRecharge::getCoinId, coinId)
                .eq(status != null, CashRecharge::getStatus, status)
                .between(
                        !(StringUtils.isEmpty(numMin) || StringUtils.isEmpty(numMax)),
                        CashRecharge::getNum,
                        new BigDecimal(StringUtils.isEmpty(numMin) ? "0" : numMin),
                        new BigDecimal(StringUtils.isEmpty(numMax) ? "0" : numMax)
                )
                .between(
                        !(StringUtils.isEmpty(startTime) || StringUtils.isEmpty(endTime)),
                        CashRecharge::getCreated,
                        startTime, endTime + " 23:59:59"
                );
        Page<CashRecharge> cashRechargePage = page(page, cashRechargeLambdaQueryWrapper);
        List<CashRecharge> records = cashRechargePage.getRecords();
        if (!CollectionUtils.isEmpty(records)) {
            List<Long> userIds = records.stream().map(CashRecharge::getUserId).collect(Collectors.toList());
            if (CollectionUtils.isEmpty(basicUsers)) {
                basicUsers = userServiceFeign.getBasicUsers(userIds, null, null);
            }
            Map<Long, UserDto> finalBasicUsers = basicUsers;
            records.forEach(cashRecharge -> { // 设置用户相关的数据
                UserDto userDto = finalBasicUsers.get(cashRecharge.getUserId());
                if (userDto != null) {
                    cashRecharge.setUsername(userDto.getUsername()); // 远程调用查询用户的信息
                    cashRecharge.setRealName(userDto.getRealName());
                }
            });
        }

        return cashRechargePage;
    }

    /**
     * 查询当前用户的充值的数据
     *
     * @param page   分页对象
     * @param userId 用户的Id
     * @param status 订单的状态
     * @return
     */
    @Override
    public Page<CashRecharge> findUserCashRecharge(Page<CashRecharge> page, Long userId, Byte status) {
        return page(page, new LambdaQueryWrapper<CashRecharge>()
                .eq(CashRecharge::getUserId, userId)
                .eq(status != null, CashRecharge::getStatus, status)
        );
    }

    /**
     * 进行一个GCN/充值/购买
     *
     * @param userId    用户的id
     * @param cashParam 现金参数
     * @return
     */
    @Override
    public CashTradeVo buy(Long userId, CashParam cashParam) {
        //1 校验现金参数
        checkCashParam(cashParam);
        // 2 查询我们公司的银行卡
        List<AdminBankDto> allAdminBanks = adminBankServiceFeign.getAllAdminBanks();
        // 仅仅需要一张银行卡
        AdminBankDto adminBankDto = loadbalancer(allAdminBanks);
        //3 生成订单号\参考号
        String orderNo = String.valueOf(snowflake.nextId());
        String remark = RandomUtil.randomNumbers(6);

        Coin coin = coinService.getById(cashParam.getCoinId());

        if (coin == null) {
            throw new IllegalArgumentException("coinId不存在");
        }
        //cashParam.getMum()这是前端给我们的金额,前端可能因为浏览器的缓存导致价格不准确,因此,我们需要在后台进行计算
        Config buyGCNRate = configService.getConfigByCode("CNY2USDT");
        BigDecimal realMum = cashParam.getMum().multiply(new BigDecimal(buyGCNRate.getValue())).setScale(2, RoundingMode.HALF_UP);
        // 4 在数据库里面插入一条充值的记录


        CashRecharge cashRecharge = new CashRecharge();
        cashRecharge.setUserId(userId);
        // 银行卡的信息
        cashRecharge.setName(adminBankDto.getName());
        cashRecharge.setBankName(adminBankDto.getBankName());
        cashRecharge.setBankCard(adminBankDto.getBankCard());

        cashRecharge.setTradeno(orderNo);
        cashRecharge.setCoinId(cashParam.getCoinId());
        cashRecharge.setCoinName(coin.getName());
        cashRecharge.setNum(cashParam.getNum());
        cashRecharge.setMum(realMum); // 实际的交易金额
        cashRecharge.setRemark(remark);
        cashRecharge.setFee(BigDecimal.ZERO);
        cashRecharge.setType("linepay"); // 在线支付
        cashRecharge.setStatus((byte) 0); // 待审核
        cashRecharge.setStep((byte) 1);// 第一步

        boolean save = save(cashRecharge);
        if (save) {
            // 5 返回我们的成功对象
            CashTradeVo cashTradeVo = new CashTradeVo();
            // 我们收户的银行卡信息
            cashTradeVo.setAmount(realMum);
            cashTradeVo.setStatus((byte)0);
            cashTradeVo.setName(adminBankDto.getName());
            cashTradeVo.setBankName(adminBankDto.getBankName());
            cashTradeVo.setBankCard(adminBankDto.getBankCard());
            cashTradeVo.setRemark(remark);
            return cashTradeVo;
        }
        return null;
    }

    /**
     * 从一个list 里面随机选一个出来
     * @param allAdminBanks
     * @return
     */
    private AdminBankDto loadbalancer(List<AdminBankDto> allAdminBanks) {
        if (CollectionUtils.isEmpty(allAdminBanks)) {
            throw new RuntimeException("没有发现可用的银行卡");
        }
        int size = allAdminBanks.size();
        if (size == 1) {
            return allAdminBanks.get(0);
        }
        Random random = new Random();
        return allAdminBanks.get(random.nextInt(size));
    }

    private void checkCashParam(CashParam cashParam) {
        @NotNull BigDecimal num = cashParam.getNum(); // 现金充值的数量
        Config withDrowConfig = configService.getConfigByCode("WITH_DROW");
        @NotBlank String value = withDrowConfig.getValue();
        BigDecimal minRecharge = new BigDecimal(value);
        if (num.compareTo(minRecharge) < 0) {
            throw new IllegalArgumentException("充值数量太小");
        }
    }
}

