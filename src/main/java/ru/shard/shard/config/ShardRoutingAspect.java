package ru.shard.shard.config;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import ru.shard.shard.service.ShardService;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

@Aspect
@Component
@RequiredArgsConstructor
public class ShardRoutingAspect {

    private final ShardService shardService;

    @Around("@annotation(withShardRouting)")
    public Object routeToShard(ProceedingJoinPoint joinPoint, WithShardRouting withShardRouting) throws Throwable {
        String shardName;

        if (withShardRouting.byId()) {
            Long id = extractIdFromParams(joinPoint, withShardRouting.idParam());
            shardName = shardService.getShardNameByCreditId(id);
        } else if (!withShardRouting.shard().isEmpty()) {
            shardName = withShardRouting.shard();
        } else {
            shardName = "shard02";
        }

        RoutingDataSource.setCurrentShard(shardName);
        try {
            return joinPoint.proceed();
        } finally {
            RoutingDataSource.clearCurrentShard();
        }
    }

    private Long extractIdFromParams(ProceedingJoinPoint joinPoint, String paramName) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Parameter[] parameters = method.getParameters();
        Object[] args = joinPoint.getArgs();

        if (paramName.isEmpty()) {
            for (int i = 0; i < parameters.length; i++) {
                if (parameters[i].getType().equals(Long.class) && args[i] != null) {
                    return (Long) args[i];
                }
            }
        } else {
            for (int i = 0; i < parameters.length; i++) {
                if (parameters[i].getName().equals(paramName) && args[i] != null) {
                    return (Long) args[i];
                }
            }
        }

        throw new IllegalArgumentException("ID parameter not found for shard routing");
    }
}
