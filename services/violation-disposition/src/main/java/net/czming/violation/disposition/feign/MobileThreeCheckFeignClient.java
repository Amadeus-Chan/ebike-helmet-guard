package net.czming.violation.disposition.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;


@FeignClient(value = "mobile-threeCheckClient", url = "https://kzmobilev2.market.alicloudapi.com")
public interface MobileThreeCheckFeignClient {

    @GetMapping(value = "/api/mobile_three/check")
    String check(@RequestHeader("Authorization") String authorization,
                 @RequestParam("name") String name,
                 @RequestParam("idcard") String idcard,
                 @RequestParam("mobile") String mobile);

}
