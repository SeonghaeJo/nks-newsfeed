package com.personal.nksnewfeed.user.client;

import com.personal.nksnewfeed.user.contract.UserApiContract;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "user-api", url = "${feign.user-api.url:http://localhost:8081}")
public interface UserFeignClient extends UserApiContract {
}