package org.facmc.common.config;

import com.alibaba.fastjson.JSON;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.facmc.common.pojo.SysLog;
import org.facmc.common.utils.HttpContextUtils;
import org.facmc.common.utils.IPUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;

@Slf4j
@Aspect
@Component
public class LogAspects {

    @Autowired
    private MongoTemplate mongoTemplate;
    //   @Autowired
    //   private SysLogDao sysLogDao;

//    @Pointcut("@annotation(com.lezhi.course.config.log.Log)")
//    public void pointcut() { }

//    @Around("pointcut()")

    @Around("execution(* org.facmc.*.controller.*.*(..))")
    public Object around(ProceedingJoinPoint point) {
        Object result = null;
        long beginTime = System.currentTimeMillis();
        try {
            // 执行方法
            result = point.proceed();
            log.error("返回值为：" + JSON.toJSONString(result));

        } catch (Throwable e) {
            e.printStackTrace();
        }
        // 执行时长(毫秒)
        long time = System.currentTimeMillis() - beginTime;
        // 保存日志
        saveLog(point, time, result);
        return result;
    }

    private void saveLog(ProceedingJoinPoint joinPoint, long time, Object result) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        SysLog sysLog = new SysLog();
        Log logAnnotation = method.getAnnotation(Log.class);
//        if (logAnnotation != null) {
//            // 注解上的描述
//            sysLog.setOperation(logAnnotation.value());
//        }
        // 请求的方法名
        String className = joinPoint.getTarget().getClass().getName();
        String methodName = signature.getName();
        sysLog.setMethod(className + "." + methodName + "()");
        // 请求的方法参数值
        Object[] args = joinPoint.getArgs();
        // 请求的方法参数名称
        LocalVariableTableParameterNameDiscoverer u = new LocalVariableTableParameterNameDiscoverer();
        String[] paramNames = u.getParameterNames(method);
        if (args != null && paramNames != null) {
            String params = "";
            for (int i = 0; i < args.length; i++) {
                params += "  " + paramNames[i] + ": " + args[i];
            }
            sysLog.setParams(params);
        }
        // 获取request

        Mono<ServerHttpRequest> request = HttpContextUtils.getRequest();
        // 设置IP地址
        sysLog.setIp(IPUtils.getIpAddr((ServerHttpRequest) request));
        // 模拟一个用户名
//        SecurityUserDetails userDetails = (SecurityUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//        Long userId = Long.parseLong(userDetails.getUsername());
//        sysLog.setUserId(userId);
        sysLog.setTime(String.valueOf(time) + "毫秒");
        //设置日期格式   HH:mm:ss中的HH大写为24小时制。HH和hh的差别是前者为24小时制，后者为12小时制
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        // new Date()为获取当前系统时间
        String dateTime = df.format(new Date());
        sysLog.setCreateTime(dateTime);
        sysLog.setResult(JSON.toJSONString(result));


        log.error("新增的log日志：：" + sysLog.toString());
        // 保存系统日志
        //    sysLogDao.saveSysLog(sysLog);
        mongoTemplate.save(JSON.toJSONString(sysLog), "fac_sys_log");
    }


}