package cn.example.binapi.interfaces.gateway.config;


import cn.example.binapi.common.model.entity.User;
import cn.example.binapi.common.service.inner.InnerInterfaceInfoService;
import cn.example.binapi.common.service.inner.InnerUserInterfaceInfoService;
import cn.example.binapi.common.service.inner.InnerUserService;
import cn.example.binapi.sdk.util.SignUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.nacos.api.utils.StringUtils;
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
public class InterfacesCallFilter implements GlobalFilter, Ordered {

    @DubboReference
    private InnerUserService innerUserService;

    @DubboReference
    private InnerInterfaceInfoService innerInterfaceInfoService;

    @DubboReference
    private InnerUserInterfaceInfoService innerUserInterfaceInfoService;

    private static final List<String> IP_WHITE_LIST = Arrays.asList("127.0.0.1", "/service/user/**", "/service/interfaceInfo/**", "/service/userInterfaceInfo/**");

    private static final String INTERFACE_HOST = "http://localhost:9000";

    /**
     * TODO: 如何让不同请求域名地址的接口通过网关请求转发到对应不同的服务器接口地址上?
     *
     * 根据实际的请求地址应该请求转发到具体的域名请求地址当中去访问，主要方法是维护一个hashmap或者redis缓存(提前记录好所有对应的k、v键值对信息)，记录对应请求地址前缀key与要对应映射的域名地址value，根据此次请求的前缀地址，查询出要转发的域名地址，利用编程式实现转发到对应的接口地址当中去请求，比如前缀是 /info 要转发到 http:www.baidu.com 则可以将前缀作为key、请求转发的域名地址为value，key可以通过request.getPath().value()然后截取对应字符串获得。
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 1. 请求日志
        ServerHttpRequest request = exchange.getRequest();
        String path = INTERFACE_HOST + request.getPath().value();
        String method = Objects.requireNonNull(request.getMethod()).toString();
        log.info("请求唯一标识：" + request.getId());
        log.info("请求路径：" + path); // http://localhost:9000/interfaces/main
        log.info("请求方法：" + method);
        log.info("请求参数：" + request.getQueryParams()); // 请求参数：{name=[loves]}
        String sourceAddress = Objects.requireNonNull(request.getLocalAddress()).getHostString();
        log.info("请求来源地址：" + sourceAddress); // 请求来源地址：127.0.0.1
        log.info("请求来源地址2：" + request.getRemoteAddress()); // /127.0.0.1:61878
        ServerHttpResponse response = exchange.getResponse();
        // 2. 网关统一访问控制 - 黑白名单
        if (!IP_WHITE_LIST.contains(sourceAddress)) {
            log.error("EXIT at whitelist");
            response.setStatusCode(HttpStatus.FORBIDDEN);
            return response.setComplete();
        }
        // 3. 请求头校验
        HttpHeaders headers = request.getHeaders();
        log.info("headers" + headers);
        String userId = headers.getFirst("userId");
        log.info("userId: " + userId);
        String account = headers.getFirst("account");
        log.info("account: " + account);
        String interfaceId = headers.getFirst("interfaceId");
        log.info("interfaceId: " + interfaceId);
        String url = headers.getFirst("url");
        log.info("url: " + url);
        String appId = headers.getFirst("appId");
        log.info("appId: " + appId);
        String accessKey = headers.getFirst("accessKey");
        log.info("accessKey: " + accessKey);
        String body = headers.getFirst("body");
        log.info("body: " + body);
        String timeSecond = headers.getFirst("timeSecond");
        log.info("timeSecond: " + timeSecond);
        String nonce = headers.getFirst("nonce");
        log.info("nonce: " + nonce);
        String sign = headers.getFirst("sign");
        log.info("sign: " + sign);

        if (!StrUtil.isAllNotBlank(accessKey, userId, interfaceId) || Objects.isNull(interfaceId) || Objects.isNull(userId)) {
            log.error("EXIT at authentication");
            return handleNoAuth(response);
        }
        if (StrUtil.isBlank(sign) || StringUtils.isBlank(url) || url.trim().isEmpty()) {
            log.error("EXIT at sign = {}, url = {}", sign, url);
            response.setStatusCode(HttpStatus.FORBIDDEN);
            return response.setComplete();
        }
        // 4.用户鉴权
        User invokeUser = innerUserService.getInvokeUser(accessKey);
        if (ObjectUtil.isNull(invokeUser)) { // Find if a user has been assigned a key
            log.error("getInvokeUser error");
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
        final Duration FIVE_MINUTES_DURATION = Duration.ofSeconds(5);
        if (duration.compareTo(FIVE_MINUTES_DURATION) >= 0) {
            log.error("EXIT at timeSecond");
            return handleNoAuth(response);
        }
        // genSign
        String secretKey = invokeUser.getSecretKey();
        String serverSign = SignUtil.genSign(body, secretKey);
        if (StrUtil.isBlank(sign) || !sign.equals(serverSign)) {
            log.error("EXIT at sign");
            return handleNoAuth(response);
        }
        // 判断用户是否有操作该接口的权利，从用户接口信息表中查询，当用户购买了对应的接口获得权限才可调用该接口
        // 5. 判断用户是否有相应权限调用该接口，如果是还要判断用户对该接口是否还有剩余的调用次数
        boolean hasLeftNum = innerUserInterfaceInfoService.hasLeftNum(Long.parseLong(interfaceId), Long.parseLong(userId));
        if (!hasLeftNum) { // 调用次数不足
            log.error("EXIT at insufficient count");
            response.setStatusCode(HttpStatus.FORBIDDEN);
            DataBufferFactory bufferFactory = response.bufferFactory();
            ObjectMapper objectMapper = new ObjectMapper();
            DataBuffer wrap;
            try {
                wrap = bufferFactory.wrap(objectMapper.writeValueAsBytes(new RestResult<>(HttpStatus.FORBIDDEN.value(), COUNT_EMPTY)));
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
            ServerHttpResponseDecorator decoratedResponse = getServerHttpResponseDecorator(interfaceId, userId, request, originalResponse, bufferFactory);
            // 设置response对象为装饰过的
            return chain.filter(exchange.mutate().response(decoratedResponse).build());
        } catch (Exception e) {
            log.error("The gateway handles the response exception" + e);
            log.info("=====  {} over =====", request.getId());
            return chain.filter(exchange);
        }
    }

    @NotNull
    private ServerHttpResponseDecorator getServerHttpResponseDecorator(long interfaceId, long userId, ServerHttpRequest request, ServerHttpResponse originalResponse, DataBufferFactory bufferFactory) {
        // 装饰: 增强能力
        return new ServerHttpResponseDecorator(originalResponse) {
            // 等调用完转发的接口后才会执行
            @NotNull
            @Override
            public Mono<Void> writeWith(@NotNull Publisher<? extends DataBuffer> body) {
                log.info("body instanceof Flux: {}", (body instanceof Flux));
                if (body instanceof Flux) {
                    Flux<? extends DataBuffer> fluxBody = Flux.from(body);
                    // 往返回值里写数据，拼接字符串; 走到这里表示请求已经发送到目标接口服务地址了，如果出现错误是接口地址自己的事情，这里的服务将会照常执行
                    return super.writeWith(fluxBody.map(dataBuffer -> {
                        // 7. 调用成功，用户操作此次接口的调用次数 + 1 invokeCount, 对接口剩余次数 -1 ; 且还要对接口信息表的总调用次数 +1 ，而剩余接口调用次数不用修改，因为用户能够调用该接口的次数在一开始购买接口时就已经被分配到用户接口信息表了。
                        try {
                            boolean a = innerUserInterfaceInfoService.invokeCount(interfaceId, userId);
                            log.info("<-------修改接口调用次数：{}", a ? "成功" : "失败");
                            boolean b = false;
                            if (a) {
                                b = innerInterfaceInfoService.increaseTotalNum(interfaceId);
                                while (!b) b = innerInterfaceInfoService.increaseTotalNum(interfaceId);
                            }
                            log.info("<-------修改接口总调用次数：{}", b ? "成功" : "失败");

                        } catch (Exception e) {
                            // log.error("invokeCount error::{}", e.getMessage());
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