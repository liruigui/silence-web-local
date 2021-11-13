package com.silence.local.controller;

import com.silence.module.common.model.DataResult;
import com.silence.module.common.util.MysqlDatabaseUtil;
import freemarker.template.Configuration;
import freemarker.template.Template;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

/**
 * 主页
 *
 * @author silence.2020-03-03
 * <p>
 * let jsonstr='{"id":123456788890123456789}';
 * // 正则获取19位数字的值
 * const id = jsonstr.match(/[0-9]{19}/)[0];
 * // 补上双引号
 * jsonstr = jsonstr.replace(id,`"${id}"`);
 * const data = JSON.parse(jsonstr);
 */
@Slf4j
@RestController
public class HomeController {

    @Resource
    private FreeMarkerConfig freeMarkerConfig;

    @RequestMapping()
    public ModelAndView index(HttpServletRequest request, ModelAndView mav) {
        mav.setViewName("index");
        mav.addObject("user", new HashMap<String, Object>(10) {{
            put("name", request.getParameter("name"));
        }});
        return mav;
    }

    @RequestMapping("/first")
    public ModelAndView first(HttpServletRequest request, ModelAndView mav) {
        mav.setViewName("first");
        return mav;
    }

    /**
     * 整合springboot
     */
    @GetMapping("code")
    public String code() throws Exception {
        /*
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-freemarker</artifactId>
        </dependency>
         */
        // 1、从spring容器中获得FreeMarkerConfigurer对象。
        // 2、从FreeMarkerConfigurer对象中获得Configuration对象。
        FreeMarkerConfigurer configurer = freeMarkerConfig.getConfigurer();
        // 3、使用Configuration对象获得Template对象。
        Template template = configurer.getConfiguration().getTemplate("test1.html");
        // 4、创建数据集
        Map<String, Object> dataModel = new HashMap<>(16);

        dataModel.put("hello", "qwertyuiopoijhgfds");
        // 5、创建输出文件的Writer对象。 /Users/silence/projects/generator-output
        //ClassLoader.getSystemResource("")
        File outFile = new File("/Users/silence/projects/silence-codegenerator/target/classes/static/codetemplate-output/hello.html");
        if (outFile.exists()) {
            //outFile.delete();
        }
        Writer out = new FileWriter(outFile);

        // 6、调用模板对象的process方法，生成文件。
        template.process(dataModel, out);
        // 7、关闭流。
        out.close();
        return "OK";
    }

    @GetMapping("getTables")
    public DataResult getTables(@RequestParam String url, @RequestParam String username, @RequestParam String password) {

        MysqlDatabaseUtil util = new MysqlDatabaseUtil(url, username, password);
        try {
            return new DataResult<>(util.getTableNames(null));
        } catch (Exception e) {
            return new DataResult(e);
        } finally {
            util.close();
        }
    }


    /**
     * 直接调用API
     * https://www.jianshu.com/p/20fd71b2e6a0
     *
     * <dependency>
     * <groupId>org.freemarker</groupId>
     * <artifactId>freemarker</artifactId>
     * <version>2.3.23</version>
     * </dependency>
     *
     * @throws Exception
     */
    @GetMapping("test")
    public void test() throws Exception {
        // 第一步：创建一个Configuration对象，直接new一个对象。构造方法的参数就是freemarker对于的版本号。
        Configuration configuration = new Configuration(Configuration.getVersion());
        // 第二步：设置模板文件所在的路径。
        configuration.setDirectoryForTemplateLoading(new File("/Users/silence/projects/silence-codegenerator/src/main/resources/codetemplate-mc/package"));
        // 第三步：设置模板文件使用的字符集。一般就是utf-8.
        configuration.setDefaultEncoding("utf-8");
        // 第四步：加载一个模板，创建一个模板对象。
        Template template = configuration.getTemplate("index.jsp");
        // 第五步：创建一个模板使用的数据集，可以是pojo也可以是map。一般是Map。
        Map dataModel = new HashMap<>();
        //向数据集中添加数据
        dataModel.put("hello", "this is my first freemarker test.");
        // 第六步：创建一个Writer对象，一般创建一FileWriter对象，指定生成的文件名。
        //generator-output文件夹需要自行创建
        Writer out = new FileWriter(new File("/Users/silence/projects/generator-output/index.jsp"));
        // 第七步：调用模板对象的process方法输出文件。
        template.process(dataModel, out);
        // 第八步：关闭流。
        out.close();
    }

}
