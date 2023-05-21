package cn.example.binapi.gateway.config;


import cn.example.binapi.common.model.entity.User;
import cn.example.binapi.common.service.inner.InnerInterfaceInfoService;
import cn.example.binapi.common.service.inner.InnerUserInterfaceInfoService;
import cn.example.binapi.common.service.inner.InnerUserService;
import cn.example.binapi.sdk.util.SignUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.nacos.common.model.RestResult;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.jetbrains.annotations.NotNull;
import org.reactivestreams.Publisher;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static cn.example.binapi.common.constant.CommonConstant.COUNT_EMPTY;

/**
 * 全局过滤
 */
@Slf4j
@Component
public class CustomGlobalFilter implements GlobalFilter, Ordered {

    @DubboReference
    private InnerUserService innerUserService;

    @DubboReference
    private InnerInterfaceInfoService innerInterfaceInfoService;

    @DubboReference
    private InnerUserInterfaceInfoService innerUserInterfaceInfoService;

    private static final List<String> IP_WHITE_LIST = Arrays.asList("127.0.0.1", "/service/apiclient", "/service/user/**", "/service/soulSoup/**");

    private static final String INTERFACE_HOST = "http://localhost:9000";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 1. 请求日志
        ServerHttpRequest request = exchange.getRequest();
        String path = INTERFACE_HOST + request.getPath().value();
        String method = Objects.requireNonNull(request.getMethod()).toString();
        log.info("请求唯一标识：" + request.getId());
        log.info("请求路径：" + path);
        log.info("请求方法：" + method);
        log.info("请求参数：" + request.getQueryParams());
        String sourceAddress = Objects.requireNonNull(request.getLocalAddress()).getHostString();
        log.info("请求来源地址：" + sourceAddress);
        log.info("请求方法1：" + method);
        log.info("请求来源地址：" + request.getRemoteAddress());
        log.info("请求方法2：" + method);
        ServerHttpResponse response = exchange.getResponse();
        log.info("请求方法3：" + method);
        // 2. 网关统一访问控制 - 黑白名单
        if (!IP_WHITE_LIST.contains(sourceAddress)) {
            log.error("EXIT at whitelist");
            response.setStatusCode(HttpStatus.FORBIDDEN);
            return response.setComplete();
        }
        // 3. 请求头校验
        HttpHeaders headers = request.getHeaders();
        log.info("headers" + headers);
        String accessKey = headers.getFirst("accessKey");
        log.info("accessKey: " + accessKey);
        String nonce = headers.getFirst("nonce");
        log.info("nonce: " + nonce);
        String timeSecond = headers.getFirst("timeSecond");
        log.info("timeSecond: " + timeSecond);
        String sign = headers.getFirst("sign");
        log.info("sign: " + sign);
        String body = headers.getFirst("body");
        log.info("body: " + body);
        String userId = headers.getFirst("userId");
        log.info("userId: " + userId);
        String interfaceId = headers.getFirst("interfaceId");
        log.info("interfaceId: " + interfaceId);

        if (!StrUtil.isAllNotBlank(accessKey, userId, interfaceId)) {
            log.error("EXIT at authentication");
            return handleInvokeError(response);
        }

        if (interfaceId == null || interfaceId.trim().isEmpty() || userId == null || userId.trim().isEmpty()) { // This step seems to have been judged above
            log.error("EXIT at interfaceId = {}, userId = {}", interfaceId, userId);
            response.setStatusCode(HttpStatus.FORBIDDEN);
            return response.setComplete();
        }
        // 4.用户鉴权
        User invokeUser = null; // Find if a user has been assigned a key
        try {
            invokeUser = innerUserService.getInvokeUser(accessKey);
        } catch (Exception e) {
            log.error("getInvokeUser error", e);
        }
        if (ObjectUtil.isNull(invokeUser)) {
            log.error("EXIT at NoInvokeUser");
            return handleNoAuth(response);
        }
        if (Long.parseLong(Objects.requireNonNull(nonce)) > 10000L) {
            log.error("EXIT at nonce");
            return handleNoAuth(response);
        }
        // Time and current time cannot be more than 5 minutes apart
        Instant currentInstant = Instant.now();
        Instant requestInstant = Instant.ofEpochSecond(Long.parseLong(Objects.requireNonNull(timeSecond)));
        Duration duration = Duration.between(requestInstant, currentInstant);
        final Duration FIVE_MINUTES_DURATION = Duration.ofMinutes(5);
        if (duration.compareTo(FIVE_MINUTES_DURATION) >= 0) {
            log.error("EXIT at timeSecond");
            return handleNoAuth(response);
        }
        // genSign
        String secretKey = invokeUser.getSecretKey();
        String serverSign = SignUtil.genSign(body, secretKey);
        if (ObjectUtil.isNull(sign) || !sign.equals(serverSign)) {
            log.error("EXIT at sign");
            return handleNoAuth(response);
        }
        // 5. 查询用户是否还有调用次数
        boolean hasCount = innerInterfaceInfoService.hasCount(Long.parseLong(interfaceId), Long.parseLong(userId));
        if (!hasCount) {
            // 调用次数不足
            log.error("EXIT at insufficient count");
            response.setStatusCode(HttpStatus.FORBIDDEN);
            DataBufferFactory bufferFactory = response.bufferFactory();
            ObjectMapper objectMapper = new ObjectMapper();
            DataBuffer wrap;
            try {
                wrap = bufferFactory.wrap(objectMapper.writeValueAsBytes(new RestResult<>(403, COUNT_EMPTY)));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
            return response.writeWith(Mono.fromSupplier(() -> wrap));
            // return handleNoAuth(response);
        }
        return handleResponse(exchange, chain, Long.parseLong(interfaceId), invokeUser.getId());

    }

    /**
     * 6.处理响应日志
     */
    private Mono<Void> handleResponse(ServerWebExchange exchange, GatewayFilterChain chain, long interfaceId, long userId) {
        ServerHttpRequest request = exchange.getRequest();
        try {
            ServerHttpResponse originalResponse = exchange.getResponse();
            // 缓存数据的工厂
            DataBufferFactory bufferFactory = originalResponse.bufferFactory();
            HttpStatus statusCode = originalResponse.getStatusCode();
            log.info("response code : {}", statusCode);
            if (statusCode != HttpStatus.OK) {
                // 降级处理返回数据
                log.info("=====  {} 结束 =====", request.getId());
                return chain.filter(exchange);
            }
            // 装饰: 增强能力
            ServerHttpResponseDecorator decoratedResponse = new ServerHttpResponseDecorator(originalResponse) {
                // 等调用完转发的接口后才会执行
                @NotNull
                @Override
                public Mono<Void> writeWith(@NotNull Publisher<? extends DataBuffer> body) {
                    log.info("body instanceof Flux: {}", (body instanceof Flux));
                    if (body instanceof Flux) {
                        Flux<? extends DataBuffer> fluxBody = Flux.from(body);
                        // 往返回值里写数据，拼接字符串
                        return super.writeWith(fluxBody.map(dataBuffer -> {
                            // 7. 调用成功，接口调用次数 + 1 invokeCount
                            try {
                                boolean b = innerUserInterfaceInfoService.invokeCount(interfaceId, userId);
                                log.info("<-------修改接口调用次数：{}", b ? "成功" : "失败");
                            } catch (Exception e) {
                                log.error("invokeCount error", e);
                            }
                            byte[] content = new byte[dataBuffer.readableByteCount()];
                            dataBuffer.read(content);
                            DataBufferUtils.release(dataBuffer);//释放掉内存
                            // 构建日志
                            StringBuilder sb2 = new StringBuilder(200);
                            List<Object> rspArgs = new ArrayList<>();
                            rspArgs.add(originalResponse.getStatusCode());
                            String data = new String(content, StandardCharsets.UTF_8); //data
                            sb2.append(data);
                            // 打印日志
                            log.info("响应结果：" + data);
                            log.info("=====  {} 结束 =====", request.getId());
                            return bufferFactory.wrap(content);
                        }));
                    } else {
                        // 8. 调用失败，返回一个规范的错误码
                        log.error("<--- {} 响应code异常", getStatusCode());
                    }
                    return super.writeWith(body);
                }
            };
            // 设置response对象为装饰过的
            return chain.filter(exchange.mutate().response(decoratedResponse).build());
        } catch (Exception e) {
            log.error("The gateway handles the response exception" + e);
            log.info("=====  {} over =====", request.getId());
            return chain.filter(exchange);
        }
    }

    @Override
    public int getOrder() {
        return -1;
    }

    public Mono<Void> handleNoAuth(ServerHttpResponse response) {
        response.setStatusCode(HttpStatus.FORBIDDEN);
        return response.setComplete();
    }

    public Mono<Void> handleInvokeError(ServerHttpResponse response) {
        response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
        return response.setComplete();
    }
}