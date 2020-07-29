package top.crossoverjie.feign.test;

import feign.Param;
import feign.RequestLine;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import top.crossoverjie.feign.plus.register.FeignPlusClient;

import java.util.List;

/**
 * Function:
 *
 * @author crossoverJie
 * Date: 2020/7/25 02:54
 * @since JDK 11
 */
@FeignPlusClient(name = "github", url = "${github.url}")
public interface Github {

   /* @RequestMapping(value = "/repos/{owner}/{repo}/contributors", method = RequestMethod.GET)
    List<GitHubRes> mvcContributors(@RequestParam("owner") String owner, @RequestParam("repo") String repo);*/


    @RequestLine("GET /repos/{owner}/{repo}/contributors")
    List<GitHubRes> feignContributors(@Param("owner") String owner, @Param("repo") String repo);
}
